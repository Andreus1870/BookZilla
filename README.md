# BookZilla

**BookZilla** is a backend booking platform built with Java 21 and Spring Boot. The system is designed to manage users, service providers, bookable resources, schedules, and reservations.

The project follows a **modular monolith architecture** with **Hexagonal Architecture (Ports & Adapters)** principles, emphasizing separation of concerns, maintainability, and testability.

> 🚧 **Under active development**
>
> Current focus: authentication, authorization, and user management.




## Features

### Implemented

- User registration and profile management
- Role-based authorization (User, Moderator, Admin)
- Keycloak integration for identity and token management
- JWT-based authentication and refresh token handling
- RESTful API with request validation
- Spring Security integration
- PostgreSQL persistence with Flyway migrations
- Interactive API documentation with Swagger / OpenAPI

### Planned

- Provider and resource management
- Scheduling and availability management
- Booking creation and cancellation
- Event-driven notifications
- Redis caching
- Apache Kafka integration
- Application monitoring and metrics



## Architecture

BookZilla is structured as a **modular monolith** using **Hexagonal Architecture (Ports & Adapters)**.

The application is organized around the following layers:

- **Domain** — business entities, rules, and behavior
- **Application** — use cases, services, and ports
- **Infrastructure** — database adapters, external integrations, security, and REST controllers

Domain models remain independent of persistence frameworks, while infrastructure adapters handle communication with external systems.



### Modules

| Module | Responsibility |
|---|---|
| `auth` | Authentication, authorization, and user management |
| `booking` | Providers, resources, schedules, and reservations |
| `notification` | Event-driven notifications |



## Tech Stack

| Category | Technologies |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3, Spring Web |
| Security | Spring Security, Keycloak, JWT |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL |
| Database migrations | Flyway |
| Build | Gradle |
| API documentation | OpenAPI / Swagger |
| Testing | JUnit 5, Mockito, Testcontainers |
| Containerization | Docker, Docker Compose |
| CI | GitHub Actions |


### Planned Technologies

- **Redis** — caching
- **Apache Kafka** — asynchronous event processing
- **Micrometer / Prometheus** — metrics and monitoring



## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose
- Configured application environment and required services

### Run the Application

```bash
./gradlew bootRun
```

The application runs at:

`http://localhost:8080`

**Swagger UI:**

`http://localhost:8080/swagger-ui/index.html`

## Development Status

BookZilla is an evolving project. Features, architecture, and integrations are being developed incrementally.

Current development priorities include completing the authentication module, expanding automated test coverage, and implementing the booking domain.
