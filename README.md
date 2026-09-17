🚧 In active development

Currently focused on the authentication and user management module.



## Features

* User registration
* User and Admin roles
* Password hashing with BCrypt
* Automatic Provider profile creation
* REST API
* Request validation
* Spring Security authorization
* Swagger / OpenAPI documentation



## Architecture

Modular monolith with Hexagonal Architecture (Ports & Adapters).



## Tech Stack

* Java 21
* Spring Boot 3
* Spring Web
* Spring Security
* Spring Data JPA / Hibernate
* PostgreSQL
* Flyway
* Gradle
* OpenAPI / Swagger

### Planned

* JWT authentication
* Redis
* Apache Kafka
* Docker / Docker Compose
* JUnit 5 / Mockito / Testcontainers
* GitHub Actions
* Micrometer / Prometheus



## Modules

auth
booking
notification



## Running

bash
./gradlew bootRun



Application runs on `http://localhost:8080`.

Swagger UI:

`http://localhost:8080/swagger-ui/index.html`