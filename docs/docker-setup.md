# Docker Setup Guide

## Prerequisites

- Docker installed and running
- Docker Compose installed (v3.8 or higher)
- Git (for version control)

## Project Structure

```
employee-attendance-microservice/
├── services/
│   ├── api-gateway/
│   ├── config-server/
│   ├── registry-server/
│   ├── employee-management/
│   ├── attendance-management/
│   └── leave-management/
│
├── infra/
│   └── docker/
│       └── docker-compose.yml
│
├── docs/
│   ├── architecture.md
│   ├── docker-setup.md
│   └── debugging-log.md
│
└── README.md
```

## Building JAR Files

Before running Docker Compose, build all microservices:

```bash
cd services/api-gateway
mvn clean package -DskipTests

cd ../config-server
mvn clean package -DskipTests

cd ../registry-server
mvn clean package -DskipTests

cd ../employee-management
mvn clean package -DskipTests

cd ../attendance-management
mvn clean package -DskipTests

cd ../leave-management
mvn clean package -DskipTests
```

Or build all at once from the project root:

```bash
for dir in services/*; do
  cd "$dir"
  mvn clean package -DskipTests
  cd ../..
done
```

## Running the System

### Option 1: Using Docker Compose (Full Stack)

From project root:

```bash
cd infra/docker
docker-compose up -d
```

This will:
1. Start MySQL database
2. Start Eureka Registry Server
3. Start Config Server
4. Start all microservices
5. Establish inter-service networking

### Option 2: Step-by-Step

```bash
# Start MySQL only
cd infra/docker
docker-compose up -d mysql

# Wait for MySQL to be ready (10-15 seconds)
sleep 15

# Start Registry Server
docker-compose up -d registry-server

# Start Config Server
docker-compose up -d config-server

# Wait for Config Server to be ready (10-15 seconds)
sleep 15

# Start all microservices
docker-compose up -d employee-management attendance-management leave-management

# Start API Gateway last
docker-compose up -d api-gateway
```

## Verifying Services

### Check Running Containers

```bash
docker-compose ps
```

Expected output:
```
NAME                      STATUS
mysql-db                  Up
registry-server           Up
config-server            Up
employee-management      Up
attendance-management    Up
leave-management         Up
api-gateway              Up
```

### Check Service Health

**Eureka Dashboard:**
```
http://localhost:8761
```
All services should show as registered.

**Config Server:**
```bash
curl http://localhost:8888/config/api-gateway.json
```

**API Gateway Health:**
```bash
curl http://localhost:9090/actuator/health
```

**MySQL Connection:**
```bash
docker exec mysql-db mysql -u root -proot -e "SELECT 1;"
```

## Testing APIs

### Example: Create an Employee

```bash
curl -X POST http://localhost:9090/api/employees/add-employee \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com"
  }'
```

See `README.md` for complete API documentation.

## Viewing Logs

### All Services
```bash
docker-compose logs -f
```

### Specific Service
```bash
docker-compose logs -f api-gateway
docker-compose logs -f config-server
docker-compose logs -f employee-management
```

### Real-time Logs
```bash
docker-compose logs -f --tail=50
```

## Stopping the System

```bash
# Stop all containers (keep data)
docker-compose stop

# Remove all containers (keep volumes/data)
docker-compose down

# Remove everything including volumes
docker-compose down -v
```

## Troubleshooting

### Services failing to start

Check logs:
```bash
docker-compose logs
```

Common issues:
- **MySQL not ready**: Wait 15 seconds after starting MySQL before starting other services
- **Port already in use**: Change port in docker-compose.yml
- **JAR files not found**: Run `mvn clean package` for each service

### Services can't communicate

Ensure all services are on the same network (`elams-network`):
```bash
docker network inspect elams-network
```

### Database connection errors

Verify MySQL is running:
```bash
docker-compose ps mysql
```

### Config Server not providing configuration

Check Config Server logs:
```bash
docker-compose logs config-server
```

Config must be available at: `http://config-server:8888/config/api-gateway.json`

## Environment Variables

Set in docker-compose.yml or .env file:
```
SPRING_PROFILES_ACTIVE=default
```

## Data Persistence

MySQL data is stored in the `mysql_data` volume:
```bash
# View volumes
docker volume ls

# View volume data
docker volume inspect mysql_data
```

## Cleanup

Remove images after stopping:
```bash
docker-compose down --rmi all
```

## Performance Tips

- Allocate at least 2GB RAM to Docker
- For M1/M2 Mac: Use `platform: linux/amd64` in docker-compose.yml
- Monitor resource usage: `docker stats`
