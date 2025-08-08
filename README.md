
# Mortgage Portal Microservice

[![CI/CD Pipeline](https://github.com/your-org/mortgage-portal/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/your-org/mortgage-portal/actions)

## Table of Contents
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Local Setup](#local-setup)
- [Running the Application](#running-the-application)
- [API Examples](#api-examples)
- [Kafka Topics](#kafka-topics)
- [Configuration](#configuration)
- [CI/CD](#cicd)

## Architecture

```
component "Mortgage Portal" {
    [Application Service] as App
    [Document Service] as Doc
    [Decision Service] as Dec
}

database PostgreSQL {
    [Applications]
    [Documents]
    [Decisions]
}

queue Kafka {
    [loan.applications]
}

[App] --> [Applications]
[App] --> [loan.applications]
[Doc] --> [Documents]
[Dec] --> [Decisions]
[loan.applications] --> [External Systems]

cloud {
    [S3 Storage] as S3
}

[Doc] --> [S3]
```

## Prerequisites

- Java 17+
- Docker 20.10+
- Docker Compose 2.0+
- Gradle 7.5+

## Local Setup

1. Clone the repository:
```bash
git clone https://github.com/your-org/mortgage-portal.git
cd mortgage-portal
```

2. Create environment file:
```bash
cp .env.example .env
```

3. Start dependencies:
```bash
docker-compose up -d postgres kafka zookeeper jaeger kafka-ui
```

## Running the Application

### Option 1: With Docker Compose
```bash
docker-compose up -d --build mortgage-service
```

### Option 2: With Gradle
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Access services:
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Kafka UI: http://localhost:8081
- Jaeger UI: http://localhost:16686

## API Examples

### Create Application
```bash
curl -X POST http://localhost:8080/api/v1/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "nationalId": "A12345678",
    "loanAmount": 250000,
    "propertyValue": 500000,
    "preferredPaymentDate": "2025-12-01"
  }'
```

### Get Application Status
```bash
curl http://localhost:8080/api/v1/applications/{id} \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Submit Decision
```bash
curl -X PATCH http://localhost:8080/api/v1/applications/{id}/decision \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "decision": "APPROVED",
    "comments": "Meets all requirements"
  }'
```

## Kafka Topics

### `loan.applications`
- **Key**: Application ID (String)
- **Value Schema**:
```json
{
  "type": "object",
  "properties": {
    "eventId": {"type": "string"},
    "eventType": {"type": "string", "enum": ["CREATED", "UPDATED", "DELETED"]},
    "applicationId": {"type": "string"},
    "status": {"type": "string", "enum": ["SUBMITTED", "APPROVED", "REJECTED"]},
    "timestamp": {"type": "string", "format": "date-time"},
    "payload": {"type": "object"}
  },
  "required": ["eventId", "eventType", "applicationId", "timestamp"]
}
```

## Configuration

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `DB_PASSWORD` | PostgreSQL password | `postgres` |
| `KAFKA_HOST` | Kafka bootstrap server | `kafka` |
| `JWT_SECRET` | JWT signing key | `secret` |
| `AWS_S3_BUCKET` | Document storage bucket | `mortgage-documents` |

### Profiles
- `dev`: Development settings with debug logging
- `prod`: Production configuration with external services

## CI/CD

The pipeline includes:
1. Linting and code formatting
2. Build and test with coverage reporting
3. Docker image build and security scan
4. Deployment to staging environment
# mortgage-api
