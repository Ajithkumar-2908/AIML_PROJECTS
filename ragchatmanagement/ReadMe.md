# AI RAG Chat Management

A robust AI Chat Session Management system built with Java, Spring Boot, Postgres DB, Redis Cache and FlywayDB Migration. This project enables users to create, manage, and interact with chat sessions and messages efficiently.

## Features

- Create and Retrieve USER details.
- Create, Rename, Retrieve and Delete chat sessions.
- Mark/Un-Mark sessions as favourite.
- Create and Delete Chat Messages within sessions.
- Retrieve Chat Messages with Pagination.
- User existence validation
- Error handling with meaningful HTTP status codes and messages.
- Healthy checks for application and dependencies
- Health Check Endpoint: `/actuator/health`
- Comprehensive unit tests with Mockito and JUnit

## Key Features

- Rate Limiting working with Redis Cache to prevent abuse and ensure fair usage.
- API-KEY based authentication for secure access to endpoints.
- Spring Security Filters for handling Rate Limiting and API key authentication.
- CORS configuration to control resource access from different origins.
- Postgres DB for reliable data storage. Persisted with Volumes in Docker Compose.
- Flyway migrations auto-applied on application startup for seamless database versioning.
- API documentation using Swagger UI for easy exploration of endpoints.
- Dockerized application for easy deployment and scalability.
- Docker Compose setup for seamless orchestration of application and dependencies (Spring Boot App, Postgres DB, Redis Cache, PG Admin).
- Centralized logging configuration (AOP) for better monitoring and debugging.

## Technologies Used

- Java 21
- Spring Framework with Spring Boot
- Postgres DB
- REDIS Cache
- FlywayDB
- PG Admin (Postgres DB Management Tool)
- Maven
- GIT
- Docker and Docker Compose
- IntelliJ IDEA
- JUnit 5 & Mockito

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Maven 3.6+
- GIT
- Docker
- IntelliJ IDEA (recommended)

## Data Model
The application consists of three main entities: User, ChatSession, and ChatMessage.
- **User**: Represents a user of the chat application. Each user has a unique identifier and can have multiple chat sessions.
- **ChatSession**: Represents a chat session associated with a user. Each session has a unique identifier, a name, a favourite flag, and is linked to multiple chat messages.
- **ChatMessage**: Represents a message within a chat session. Each message has a unique identifier, content, context (key and value pair to hold json context), timestamp, and is linked to a specific chat session.
- The relationships between these entities are as follows:
  - A User can have multiple ChatSessions (One-to-Many relationship).
  - A ChatSession can have multiple ChatMessages (One-to-Many relationship).

```yaml
User
   |
   | 1 → N
   |
   ChatSession
   |
   | 1 → N
   |
   ChatMessage
```


## Installation

1. **Clone the repository:**
    ```bash
    git clone -b feature/develop_aiml_projects https://github.com/Ajithkumar-2908/AIML_PROJECTS.git

2. **Configurations:**

Note: No need to update the following configurations if you want to run the application as it is. Skip to Step 3 (Build) if you don't want to change any default configurations.

Path: src/main/resources/application.yaml

Configs: Flyway DB Migration, Postgres DB, Redis Cache, Swagger UI, CORS, Security (API-KEY, Rate Limiting), Logging
```yaml
server:
  port: ${APP_PORT:8080}

spring:
  application:
    name: ragchatmanagement
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    out-of-order: false
  data:
    redis:
      host: ${SPRING_REDIS_HOST:localhost}
      port: ${SPRING_REDIS_PORT:6379}

  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/rag_db?serverTimezone=Asia/Kolkata}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:mysecretpassword}
    driver-class-name: org.postgresql.Driver

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        jdbc:
          time_zone: Asia/Kolkata
        format_sql: true

  jackson:
    time-zone: Asia/Kolkata

springdoc:
  swagger-ui:
    path: /           # Make Swagger UI available at the root URL
    operationsSorter: method
    display-request-duration: true
    doc-expansion: list
  api-docs:
    path: /v3/api-docs

cors:
  allowed-origins:
    - "http://localhost:8080"
  allowed-methods:
    - "GET"
    - "POST"
    - "PUT"
    - "PATCH"
    - "DELETE"
    - "OPTIONS"
  allowed-headers:
    - "Authorization"
    - "Content-Type"
    - "X-API-KEY"
  exposed-headers:
    - "Authorization"
  allow-credentials: true

security:
  api-key: my-secret-api-key
  rate-limit:
    capacity: 100
    duration-minutes: 1


logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```
Path: compose.yaml:

Services: Postgres, Redis, Spring Boot App, PG Admin
```yaml
services:
  postgres:
    image: 'postgres:latest'
    environment:
      POSTGRES_DB: rag_db
      POSTGRES_PASSWORD: mysecretpassword
      POSTGRES_USER: postgres
    ports:
      - '5432:5432'
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - /etc/localtime:/etc/localtime:ro
      - /usr/share/zoneinfo:/usr/share/zoneinfo:ro

  redis:
    image: redis:7.2-alpine
    container_name: ragchat-redis
    ports:
      - "6379:6379"
    healthcheck:
      test: [ "CMD", "redis-cli", "ping" ]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s
    command: [ "redis-server", "--appendonly", "yes" ]
    volumes:
      - redis-data:/data
    restart: unless-stopped

  app:
    image: rag-chat-management  # Docker image name (built from Dockerfile)
    container_name: rag-chat-management  # Custom container name
    build: .  # Build from Dockerfile in current directory
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/rag_db?serverTimezone=Asia/Kolkata
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: mysecretpassword
      SPRING_REDIS_HOST: redis  # Service name as hostname
      SPRING_REDIS_PORT: 6379
    depends_on:
      redis:
        condition: service_healthy

    # pgAdmin for database browsing
  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: ragchat-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@ragchat.com
      PGADMIN_DEFAULT_PASSWORD: admin123
    ports:
      - "5050:80"  # Access on http://localhost:5050
    volumes:
      - pgadmin-data:/var/lib/pgadmin
    depends_on:
      - postgres
    restart: unless-stopped



volumes:
  postgres-data:
    driver: local
  redis-data:
    driver: local
  pgadmin-data:
    driver: local
```

Environment Variables:
- `APP_PORT`: Port on which the application will run (default: 8080)
- `SPRING_DATASOURCE_URL`: JDBC URL for Postgres database
- `SPRING_DATASOURCE_USERNAME`: Username for Postgres database
- `SPRING_DATASOURCE_PASSWORD`: Password for Postgres database
- `SPRING_REDIS_HOST`: Hostname for Redis server
- `SPRING_REDIS_PORT`: Port for Redis server
- `security.api-key`: API key for authentication
- `security.rate-limit.capacity`: Number of requests allowed in the specified duration
- `security.rate-limit.duration-minutes`: Duration in minutes for rate limiting
- `PGADMIN_DEFAULT_EMAIL`: Email to login to PG Admin Postgres DB Management Tool
- `PGADMIN_DEFAULT_PASSWORD`: Password to login to PG Admin Postgres DB Management Tool

3. **Build:**
    ```bash
    mvn clean install
    ```
4. **Run with Docker Compose:**
    ```bash
   docker compose up --build
    ```
Note: 
**The above command should be run in the root folder of the project.**
**The command will Build the Docker Image for the Spring Boot Application and start all the services defined in the compose.yaml file.**

Services:
- Spring Boot Application: http://localhost:8080
- PG Admin: http://localhost:5050 (Use the environment variables defined above to login)
- Postgres DB: localhost:5432
- Redis Cache: localhost:6379

## API Documentation
Once the application is running, you can access the Swagger UI for API documentation at:
http://localhost:8080/swagger-ui/index.html

Health Check Endpoint:
http://localhost:8080/actuator/health

Note: The above mention Swagger UI does not require any authentication and is publicly accessible but the actual APIs require API-KEY based authentication.

Pass the Header with the API requests:
```
X-API-KEY: my-secret-api-key
```

Note:
1. To update the "Favorite" status of a chat session, use the following endpoints:
- Mark as Favorite: `/{sessionId}/updateFavourite`
- Body should have the Key and Value:
```json
{
  "favourite": true
}
```

2. To rename a chat session, use the following endpoint:
- Rename Session: `/{sessionId}/rename`
- Body should have the Key and Value:
```json
{
  "name": "New Session Name"
}
```

## Running Tests
To run the unit tests, use the following Maven command:
```bash
mvn test
```

## License
This project is owned by B.AJITHKUMAR

## Contact
Maintainer: Ajithkumar B

LinkedIn: https://www.linkedin.com/in/ajithkumar2908/

Github: https://github.com/Ajithkumar-2908
