# Debugging Log

## Purpose
This file serves as a tracking document for debugging issues, troubleshooting steps, and investigation notes during development and deployment.

## Log Template

```
### Date: YYYY-MM-DD | Issue: [Brief Description]

**Symptoms:**
- 

**Investigation Steps:**
1. 
2. 
3. 

**Root Cause:**
- 

**Resolution:**
- 

**Files Changed/Affected:**
- 

**Status:** ✓ RESOLVED / ⏳ IN PROGRESS / ✗ BLOCKED

---
```

## Recent Issues

### Issue #1: [Add your first issue here when debugging]

**Symptoms:**
- 

**Investigation Steps:**
1. 
2. 

**Root Cause:**
- 

**Resolution:**
- 

**Files Changed:**
- 

**Status:** Not Started

---

## Useful Debugging Commands

```bash
# Check all logs
docker-compose logs -f

# Check specific service logs
docker-compose logs -f [service-name]

# Inspect running container
docker exec -it [container-name] bash

# Check network connectivity between services
docker exec [container-name] ping [other-service]

# View system resource usage
docker stats

# Check Docker Compose configuration
docker-compose config

# Validate docker-compose.yml
docker-compose config --quiet

# List all networks
docker network ls

# Inspect a network
docker network inspect [network-name]

# Check service health
curl http://[service-name]:[port]/actuator/health

# List all volumes
docker volume ls

# Inspect a volume
docker volume inspect [volume-name]

# Get service IP address
docker inspect [container-name] | grep IPAddress
```

## Common Issues Reference

| Issue | Command to Debug | Expected Output |
|-------|------------------|------------------|
| Service won't start | `docker-compose logs [service]` | No error messages |
| Network connectivity | `docker exec [container] ping [service]` | PING successful |
| Database connection | `docker exec mysql mysql -u root -proot -e "SELECT 1;"` | Query OK |
| Port conflict | `docker ps \| grep :[port]` | Should show only 1 service |
| Config retrieval | `curl http://config-server:8888/config/api-gateway.json` | Valid JSON |
| Eureka registration | Visit http://localhost:8761 | All services listed |

---

## Notes

- Add detailed entries as you encounter issues
- Document solutions for future reference
- Include links to relevant logs or terminal output
- Mark issues as RESOLVED once fixed
