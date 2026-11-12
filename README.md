# Streamly — Distributed Telemetry & Event Streaming Platform

Streamly is a Java-based telemetry ingestion and event-streaming backend designed to explore how distributed systems receive, publish, process, and persist high-volume device data reliably.

The project starts with a local, containerized telemetry pipeline and evolves incrementally through focused engineering issues. Its emphasis is not only on moving messages through Kafka, but also on the backend concerns that appear as a streaming system grows: reliable persistence, duplicate delivery, failure recovery, schema evolution, partitioning, consumer scalability, real-time processing, observability, and production deployment.

> **Project direction:** Build and evolve a realistic event-driven backend in deliberate stages, making the design decisions and trade-offs visible through implementation and issue-level documentation.

---

## What Streamly Is Trying to Achieve

A telemetry platform receives measurements from devices such as sensors and makes those measurements available to downstream services. Streamly models that workflow:

1. A telemetry event is created with information such as an event ID, device ID, temperature, humidity, and timestamp.
2. The producer publishes the event asynchronously to Apache Kafka.
3. Kafka consumers receive and process the event.
4. The processing and persistence layers validate and map the event into the application's storage model.
5. PostgreSQL stores the resulting telemetry record for later retrieval and analysis.
6. Reliability mechanisms protect the pipeline from duplicate deliveries and processing failures.

The current work uses generated/sample telemetry to exercise the pipeline. Integration with physical IoT devices and a complete cloud deployment are not assumed to be part of the current implementation.

### Engineering goals

- **Reliable event processing:** handle retries, failed messages, and duplicate deliveries without corrupting persisted data.
- **Clear boundaries:** separate transport events, versioned payloads, processing logic, and persistence entities.
- **Safe contract evolution:** explore how producers and consumers can evolve without requiring every service to change at once.
- **Scalable streaming:** build toward partition-aware publishing and horizontally scaled consumer groups.
- **Operational visibility:** measure message throughput, consumer lag, processing latency, and failures.
- **Incremental design:** implement each capability through a focused issue, with the trade-offs documented rather than hiding them behind a single large rewrite.

---

## Current Architecture

The baseline ingestion and persistence path is:

```text
Telemetry Source
      |
      v
TelemetryProducerService
      |
      v
Apache Kafka
      |
      v
Kafka Consumer(s)
      |
      v
Telemetry Processing
      |
      v
Persistence Service
      |
      v
Spring Data JPA / Repository
      |
      v
PostgreSQL
```

Failure handling extends the consumer path with retry and dead-letter handling:

```text
Kafka Topic
    |
    v
Consumer
    |
    v
Processing / Persistence
    |
    +------ success ------> PostgreSQL
    |
    +------ retryable failure ------> Retry Topic(s) / Retry Handling
    |
    +------ exhausted or non-retryable failure ------> Dead Letter Topic
```

The schema-evolution approaches described below are **separate design strategies**. They are documented together for comparison; they should not be interpreted as one mandatory serialization path in which every strategy is active at the same time.

---

## Technology Stack

| Area | Technology | Role in Streamly |
|---|---|---|
| Language | Java 25 | Backend implementation and event-processing logic |
| Application framework | Spring Boot | Application configuration, dependency injection, and service lifecycle |
| Messaging | Apache Kafka 3.9 (KRaft mode) | Durable event streaming between producers and consumers |
| Kafka integration | Spring Kafka | `KafkaTemplate`, consumer listeners, and Kafka configuration |
| Serialization | Jackson / JSON | Serialization used by the existing application message flow |
| Persistence | Spring Data JPA / Hibernate | Mapping and persistence of telemetry records |
| Database | PostgreSQL 18 | Durable telemetry storage |
| Schema evolution exploration | Apache Avro | Language-neutral event schema definition and Java code generation |
| Schema governance exploration | Confluent Schema Registry | Local schema-registry infrastructure for the Avro strategy demonstration |
| Infrastructure | Docker Compose | Local multi-container development environment |
| Build | Maven | Dependency management, compilation, and Avro code-generation integration |
| Health and diagnostics | Spring Boot Actuator | Application health and information endpoints |

> The Avro and Schema Registry work is currently an isolated strategy demonstration. The existing producer/consumer runtime remains JSON-based; this repository does not claim to have completed an end-to-end migration to `KafkaAvroSerializer` and `KafkaAvroDeserializer`.

---

## Event Model

The telemetry domain currently centers on measurements and identifiers such as:

| Field | Meaning |
|---|---|
| `eventId` | Unique identifier used to identify a telemetry event and support duplicate protection |
| `deviceId` | Identifier of the device that produced the measurement |
| `temperature` | Temperature reading |
| `humidity` | Humidity reading |
| `batteryLevel` | Battery measurement introduced in a later payload version |
| `timestamp` | Time at which the telemetry event was generated |

The exact fields available in a payload depend on its version. For example, the versioned Java payload models distinguish the earlier telemetry contract from the later battery-aware contract.

---

## Schema Evolution Strategies

Schema evolution is a central architectural concern in Streamly. Producers and consumers may be deployed independently, so changing an event contract must be handled deliberately.

The project explores four approaches. **Strategy 3 was implemented earlier in Issue #13. Issue #26 was created later to implement and compare the other strategy work; it reuses the Strategy 3 implementation rather than duplicating it.**

### Strategy 1 — Event Envelope Pattern

**Purpose:** Keep transport metadata separate from the business payload.

The event is wrapped in a stable envelope rather than publishing a raw telemetry object alone:

```text
EventEnvelope<TelemetryPayload>
    ├── eventId
    ├── eventType
    ├── schemaVersion
    ├── timestamp
    ├── source
    └── payload
```

The envelope provides a consistent place for event identity, type, version, origin, and timing metadata. The payload can evolve independently of those transport-level attributes.

**Implemented components:**

- `EventEnvelope`
- `EventType`

### Strategy 2 — Topic-Based Versioning

**Purpose:** isolate incompatible event contracts at the Kafka topic boundary.

Different payload versions are published to different topics:

```text
                    Version-aware producer
                       /           \
                      v             v
          telemetry-events-v1   telemetry-events-v2
                    |                 |
                    v                 v
             V1 consumer        V2 consumer
```

The producer selects the destination topic based on the event version. Each consumer subscribes to the contract it understands, avoiding a version switch in every consumer's business logic.

**Implemented components include:**

- `VersionAwareTelemetryProducer`
- `TelemetryV1Consumer`
- `TelemetryV2Consumer`
- Version-specific Kafka topic configuration

**Trade-off:** version isolation is clear, but topic and consumer management grows as more versions are introduced.

### Strategy 3 — Versioned Payload Models / Application-Level Evolution

**Purpose:** represent each event contract with a distinct application model while keeping downstream processing dependent on a shared abstraction.

This strategy was implemented in **Issue #13 — Implement Telemetry Event Schema Evolution** and is not reimplemented in Issue #26.

The implementation includes:

- `TelemetryEventV1`
- `TelemetryEventV2`
- `VersionedTelemetryEvent`
- `TelemetryEventVersionMapper`
- A processing layer that accepts the shared versioned-event abstraction

The mapper converts the incoming domain event into the appropriate payload model according to its event version. This keeps version-specific mapping at a clear boundary and reduces the need for downstream processing code to depend directly on one concrete payload class.

### Strategy 4 — Schema Registry-Based Evolution

**Purpose:** maintain schemas centrally and make schema definitions independent of application-language classes.

The Streamly demonstration introduces:

- A Schema Registry service in the local Docker Compose environment
- An Avro schema at `src/main/resources/avro/TelemetryEvent.avsc`
- Avro dependencies and Maven code-generation configuration
- A generated Java model based on the `.avsc` contract
- Schema Registry URL configuration in the application configuration

The current Avro schema describes the telemetry contract, including the event and device identifiers, measurements, optional battery level, and timestamp. The optional battery field uses a `null` union with a default so that the field can be absent in records written against an older version of the contract.

Conceptually, the workflow is:

```text
TelemetryEvent.avsc
        |
        v
Avro Maven Plugin
        |
        v
Generated Avro Java type
        |
        v
Schema Registry (local infrastructure)
```

**Scope boundary:** this is an infrastructure, schema-definition, and code-generation demonstration. The existing application producer and consumer continue to use JSON. A complete runtime migration to Avro serialization/deserialization, automated compatibility enforcement, and end-to-end Avro message tests are future work—not completed features of this strategy demonstration.

### Strategy comparison

| Strategy | Where versioning is handled | Main advantage | Main trade-off |
|---|---|---|---|
| 1. Event Envelope | Event/message contract | Separates metadata from payload | Consumers still need a policy for interpreting payload versions |
| 2. Topic-Based Versioning | Kafka topic boundary | Strong isolation between versions | More topics and version-specific consumers to operate |
| 3. Versioned Payload Models | Application model and mapping layer | Explicit typed models and centralized mapping | Application code must maintain and map supported versions |
| 4. Schema Registry | Shared schema governance infrastructure | Central schema storage and compatibility controls | Requires registry operations and serializer/deserializer integration |

---

## Reliability and Data Consistency

### Reliable persistence — Issue #11

The persistence path is separated from the Kafka transport model through a processing/service boundary. The implementation focuses on transactional database writes and idempotent handling so that repeated delivery of the same event does not create duplicate telemetry records.

### Retry and Dead Letter Queue — Issue #12

The failure-handling work introduces retry handling and a dead-letter path for messages that cannot be processed successfully. This separates recoverable failures from messages that require inspection or later remediation, and prevents a single poison message from permanently blocking normal processing.

---

## Repository Layout

The source is organized around application responsibilities. The following is a conceptual view; individual packages may expand as later issues are implemented.

```text
src/
└── main/
    ├── java/com/streamly/telemetry/
    │   ├── config/          # Spring, Kafka, topic, and infrastructure configuration
    │   ├── consumer/        # Kafka consumers
    │   ├── producer/        # Telemetry publishing and version-aware routing
    │   ├── domain/          # Core telemetry domain model
    │   ├── event/
    │   │   ├── envelope/   # Event envelope and event metadata
    │   │   ├── mapper/     # Version conversion/mapping
    │   │   └── payload/    # Version-specific payload contracts
    │   ├── service/         # Processing and persistence services
    │   └── repository/      # Persistence access
    └── resources/
        ├── application.yml
        └── avro/            # Avro schema definitions
```

---

## Run Locally

### Prerequisites

- Java 25
- Docker and Docker Compose
- Maven (or the Maven Wrapper, if included in the checkout)

### Environment configuration

Create a local `.env` file with the values required by `docker-compose.yml`. Keep real credentials out of source control.

```dotenv
POSTGRES_DB=<database_name>
POSTGRES_USER=<database_user>
POSTGRES_PASSWORD=<database_password>
POSTGRES_PORT=5432
```

### Start the services

```bash
docker compose up -d --build
```

Check the running containers:

```bash
docker compose ps
```

The Compose network uses service names for container-to-container communication (for example, `postgres:5432` and `kafka:9092`). Host-side tools use the ports published by Compose.

### Health check

When the application is running, its Actuator health endpoint is available at:

```text
http://localhost:8080/actuator/health
```

If the Schema Registry demonstration service is enabled in the local Compose configuration, its HTTP endpoint is exposed on port `8081`.

---

## Issues and Implementation Tracker

Issues are listed by **implementation sequence and project phase**, not strictly by issue number. This is intentional: **Issue #26 was created later and is being handled before Issue #14. After the current Issue #26 work, Issue #14 is the next planned implementation.**

| Issue | Scope | Implementation / planned outcome | Status / sequence |
|---|---|---|---|
| [#1](https://github.com/Pujagithub2006/Streamly/issues/1) | Initialize project structure and development environment | Spring Boot project foundation, Java/Maven setup, package structure, local development configuration, and repository basics | Closed |
| [#2](https://github.com/Pujagithub2006/Streamly/issues/2) | Set up PostgreSQL infrastructure | PostgreSQL service in Docker Compose, environment-based credentials, port mapping, and persistent volume | Closed |
| [#3](https://github.com/Pujagithub2006/Streamly/issues/3) | Set up Apache Kafka infrastructure | Kafka broker in Docker Compose using KRaft configuration, internal listener, and persistent storage | Closed |
| [#4](https://github.com/Pujagithub2006/Streamly/issues/4) | Integrate the application with local infrastructure | Connect the Spring Boot container to PostgreSQL and Kafka through the Docker network and externalized configuration | Closed |
| [#6](https://github.com/Pujagithub2006/Streamly/issues/6) | Define telemetry event schema | Establish the telemetry domain event and its core fields for message exchange and persistence | Closed |
| [#7](https://github.com/Pujagithub2006/Streamly/issues/7) | Implement Kafka producer | Publish telemetry events asynchronously using Spring Kafka and JSON serialization | Closed |
| [#8](https://github.com/Pujagithub2006/Streamly/issues/8) | Implement Kafka consumer | Subscribe to the telemetry topic, deserialize incoming JSON events, and hand them to downstream processing | Closed |
| [#9](https://github.com/Pujagithub2006/Streamly/issues/9) | Persist streamed telemetry data | Map consumed events to persistence models and store telemetry records in PostgreSQL | Closed |
| [#11](https://github.com/Pujagithub2006/Streamly/issues/11) | Implement reliable event persistence | Introduce a processing/persistence boundary, transactional writes, and event-ID-based idempotent handling | Closed |
| [#12](https://github.com/Pujagithub2006/Streamly/issues/12) | Implement retry and Dead Letter Queue handling | Add retry handling, retry topics, and a dead-letter path for messages that cannot be processed | Closed |
| [#13](https://github.com/Pujagithub2006/Streamly/issues/13) | Implement telemetry event schema evolution | Add version-aware payload models, `VersionedTelemetryEvent`, mapping, and common processing support (Strategy 3) | Closed |
| [#26](https://github.com/Pujagithub2006/Streamly/issues/26) | Implement production-grade Kafka schema evolution strategies | Add the event envelope pattern (Strategy 1), topic-based version routing and consumers (Strategy 2), and an Avro/Schema Registry demonstration (Strategy 4); reuse Strategy 3 from #13 | **Current issue — handled before #14** |
| [#14](https://github.com/Pujagithub2006/Streamly/issues/14) | Implement Kafka partitioning strategy | Configure partitions and use `deviceId` as the message key to distribute events while preserving ordering for a device | **Next planned issue** |
| [#15](https://github.com/Pujagithub2006/Streamly/issues/15) | Implement consumer horizontal scaling | Run multiple consumer instances in a group, validate partition assignment, and evaluate throughput/scaling behavior | Planned |
| [#16](https://github.com/Pujagithub2006/Streamly/issues/16) | Implement real-time telemetry processing | Add an asynchronous processing pipeline, abnormal-value detection, and threshold-based alerts | Planned |
| [#17](https://github.com/Pujagithub2006/Streamly/issues/17) | Implement telemetry query optimization | Add indexes, pagination, and time-range queries; measure query performance on larger datasets | Planned |
| [#18](https://github.com/Pujagithub2006/Streamly/issues/18) | Add Redis caching layer | Cache frequently accessed telemetry data and define expiration and invalidation behavior | Planned |
| [#19](https://github.com/Pujagithub2006/Streamly/issues/19) | Implement real-time dashboard communication | Stream processed telemetry updates to clients using WebSocket or SSE | Planned |
| [#20](https://github.com/Pujagithub2006/Streamly/issues/20) | Add search and analytics pipeline | Index telemetry data and separate search/analytics workloads from transactional persistence | Planned |
| [#21](https://github.com/Pujagithub2006/Streamly/issues/21) | Implement observability stack | Expose application and Kafka metrics, including consumer lag, throughput, latency, and error rate, using Actuator, Prometheus, and Grafana | Planned |
| [#22](https://github.com/Pujagithub2006/Streamly/issues/22) | Production deployment and load testing | Container deployment with Kubernetes, load testing, bottleneck identification, and deployment documentation | Planned |
| [#25](https://github.com/Pujagithub2006/Streamly/issues/25) | Producer versioning and polymorphic Kafka event publishing | Related open work concerning producer-side version handling and polymorphic event publishing; keep its scope distinct from the strategy comparison in #26 | Open — separately tracked |

### Execution order note

Issue numbers reflect when work was created, not necessarily the order in which it is implemented. Issue #26 was added after the original roadmap issues and is the current schema-evolution strategy work. **Once #26 is finalized, development proceeds to Issue #14 — Kafka Partitioning Strategy.**

---

## Roadmap

The next engineering phase focuses on scaling the streaming pipeline beyond a single-partition/single-consumer baseline:

1. Partition telemetry by `deviceId` and preserve per-device ordering.
2. Scale consumers horizontally through Kafka consumer groups.
3. Add real-time processing and threshold-based alerting.
4. Improve database query performance and add caching.
5. Deliver real-time telemetry updates and a search/analytics path.
6. Add metrics, dashboards, Kubernetes deployment, and load testing.

The roadmap is tracked through the linked GitHub issues so implementation decisions and acceptance criteria remain reviewable.

---

## Project Status

Streamly has established the local telemetry ingestion and persistence foundation, added reliability mechanisms, and is expanding its treatment of event schema evolution. The next planned milestone is Kafka partitioning (**Issue #14**), followed by consumer scaling and the processing/operational capabilities listed above.

For implementation details and progress, see the [Streamly GitHub repository](https://github.com/Pujagithub2006/Streamly) and its [issue tracker](https://github.com/Pujagithub2006/Streamly/issues).
