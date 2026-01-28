# Bidly Platform Architecture

## 1. Project Structure Overview

The project follows a **Microservices Architecture** organized into a multi-module Maven project.

```
bidly/
├── pom.xml                  # Parent POM (Dependency Management)
├── docker-compose.yml       # Infrastructure Orchestration
├── infrastructure/          # Core technical services
│   ├── config-server/       # Centralized Configuration
│   ├── eureka-server/       # Service Discovery
│   └── api-gateway/         # Entry Point & Routing
├── services/                # Business Domains (Microservices)
├── shared/                  # Reusable Libraries
│   ├── common-lib/          # Shared DTOs, Utils
│   ├── event-lib/           # Kafka Event Models
│   └── security-lib/        # Auth & Security Utilities
└── docs/                    # Documentation
```

---

## 2. Infrastructure Components

### 2.1. Config Server (`infrastructure/config-server`)
*   **Port**: `8888`
*   **Role**: Centralizes configuration for all microservices. Instead of managing `application.yml` files in every service, all configurations are stored in `infrastructure/config-repo`.
*   **Key Feature**: Allows changing configuration without redeploying the services.
*   **Tech**: Spring Cloud Config Server.

### 2.2. Eureka Server (`infrastructure/eureka-server`)
*   **Port**: `8761`
*   **Role**: **Service Discovery Registry**.
*   **Function**:
    *   Every microservice registers itself here upon startup (e.g., "I am USER-SERVICE at 192.168.x.x:8081").
    *   Services find each other by name (e.g., `http://user-service`) instead of hardcoded IPs.
*   **Tech**: Netflix Eureka.

### 2.3. API Gateway (`infrastructure/api-gateway`)
*   **Port**: `8080`
*   **Role**: **Single Entry Point** for all external clients (Web, Mobile).
*   **Function**:
    *   **Routing**: Forwards requests to the appropriate microservice (e.g., `/api/users` -> `user-service`).
    *   **Load Balancing**: Distributes traffic if multiple instances of a service are running.
    *   **Security**: Will handle JWT validation (future implementation).
*   **Tech**: Spring Cloud Gateway.

---

## 3. Shared Libraries (`shared/`)

Building these as libraries prevents code duplication across microservices.

### 3.1. Common Lib (`shared/common-lib`)
*   Contains generic utility classes used everywhere.
*   **Global Exceptions**: `ResourceNotFoundException`, `BusinessException`.
*   **Standard Responses**: `ApiResponse<T>` wrapper.
*   **Utils**: Date formatters, validation logic.

### 3.2. Event Lib (`shared/event-lib`)
*   Defines the **Contract** for asynchronous communication.
*   Contains **POJOs** for Kafka events.
    *   Example: `UserRegisteredEvent`, `BidPlacedEvent`.
*   Ensures Producer and Consumer use the exact same event structure.

### 3.3. Security Lib (`shared/security-lib`)
*   Contains shared Spring Security configurations.
*   **JWT Utilities**: Token parsing, claim extraction.
*   **Security Config**: Standard `SecurityFilterChain` setup to be imported by microservices.

---

## 4. Backing Services (Docker)

Defined in `docker-compose.yml`, these are the databases and brokers required for the platform.

| Service | Port | Role |
| :--- | :--- | :--- |
| **PostgreSQL** | `5432` | Primary Database. Each microservice has its defined DB (e.g., `user_db`). |
| **Kafka** | `9092` | Message Broker for asynchronous events. |
| **Zookeeper** | `2181` | Manages Kafka cluster state. |
| **Redis** | `6379` | Implementation of Distributed Cache. |
| **Zipkin** | `9411` | Distributed Tracing UI. Visualizes request flow across services. |
| **Maildev** | `1080` | SMTP Server for testing emails locally. |

---

## 5. Development Workflow

1.  **Start Infrastructure**: Run `docker-compose up -d` to start Postgres, Kafka, etc.
2.  **Start Config Server**: Needed first so other services can fetch their config.
3.  **Start Eureka Server**: Needed so services can register.
4.  **Start Business Services**: Start `User Service`, `Wallet Service`, etc.
5.  **Start Gateway**: Finally, start the gateway to route traffic.

Access the system:
*   **Gateway**: `http://localhost:8080`
*   **Eureka Dashboard**: `http://localhost:8761`
*   **Zipkin Tracing**: `http://localhost:9411`
