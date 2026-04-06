# Employee Attendance Microservices (ELAMS) - Architecture

## System Overview

ELAMS is a microservices-based employee management system built with Spring Boot and Spring Cloud. It manages employee information, attendance tracking, leave management, and shift scheduling across multiple decoupled services.

## Architecture Pattern

**Microservices Architecture** with:
- Service Discovery (Eureka)
- Centralized Configuration (Spring Cloud Config Server)
- API Gateway (Spring Cloud Gateway)
- Inter-service Communication (Feign Client)

```
Client Requests
      ↓
   API Gateway (9090)
      ↓
   Eureka Registry (8761)
      ↓
   Config Server (8888)
      ↓
   Microservices
   ├── Employee Management (9191)
   ├── Attendance Management (9193)
   └── Leave Management (9192)
      ↓
    MySQL Database
```

## Services

### 1. **API Gateway** (Port 9090)
- Single entry point for all client requests
- Route management and load balancing
- JWT-based security and authentication
- Routes requests to backend services

### 2. **Config Server** (Port 8888)
- Centralized configuration management
- Native profile with classpath configuration
- Provides configuration to all microservices at startup
- Configuration files: `/config/*.properties`

### 3. **Registry Server - Eureka** (Port 8761)
- Service discovery and registration
- Health checking for registered services
- Dynamic service lookup for inter-service communication

### 4. **Employee Management Service** (Port 9191)
- Employee CRUD operations
- Role management (EMPLOYEE, MANAGER, ADMIN)
- Shift assignment and management
- Team member tracking

### 5. **Attendance Management Service** (Port 9193)
- Clock-in and clock-out functionality
- Attendance record tracking
- Attendance report generation

### 6. **Leave Management Service** (Port 9192)
- Leave balance management
- Leave request submission and workflow
- Leave request approval/rejection
- Integration with attendance data

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.5.0 |
| Service Framework | Spring Cloud | 2025.0.0 |
| Database | MySQL | 8.0 |
| Build Tool | Maven | 3.x |
| Container | Docker | Latest |
| Orchestration | Docker Compose | 3.8 |

## Data Persistence

### Database Structure
- **auth_service_db** - API Gateway (authentication)
- **employee_management** - Employee Service
- **attendance_management** - Attendance Service
- **leave_management** - Leave Service

**Connection Details:**
- Host: `mysql:3306` (Docker service name)
- User: `root`
- Password: `root`

## Service Communication

### Synchronous Communication (Feign Client)
- Employee Service → API Gateway
- Attendance Service → Employee Service
- Leave Service → Employee Service

### Async Service Discovery
- All services register with Eureka on startup
- Client-side load balancing via load-balancer (lb://) notation

## Configuration Flow

```
1. Service starts → Fetches config from config-server:8888
2. Registers with registry-server:8761 (Eureka)
3. Connects to MySQL database
4. Available for requests via API Gateway
```

## Deployment

- **Container Image**: OpenJDK 21 + Spring Boot JAR
- **Orchestration**: Docker Compose
- **Networking**: Custom bridge network (`elams-network`)
- **Volume Management**: MySQL data persistence (`mysql_data` volume)

## API Document

See `docker-setup.md` for running the system.
See service endpoints in README.md for full API documentation.

## Development Notes

- All services use native Spring Cloud Config (classpath)
- No external Git-based config server (configuration is embedded)
- JWT tokens used for API Gateway authentication
- Dockerfile for each service (minimal, multi-stage ready)
