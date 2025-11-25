# Student Management CRUD API

Production-ready Student Management API built with Spring Boot 3, Maven, and MySQL. It exposes validated CRUD endpoints, supports filtering/pagination, and ships with Docker, Postman collection, and automated tests.

## Tech Stack
- Java 17, Spring Boot 3.4, Spring Data JPA, Bean Validation
- MySQL 8.4 (Testcontainers for integration tests)
- Lombok, MapStruct-free manual mapping
- Docker & Docker Compose

## Project Structure
```
com.kabir.student
├── config        # CORS, JPA auditing, dev data loader
├── controller    # REST controllers
├── dto           # Request/response payloads & pagination wrapper
├── exception     # Custom exceptions & error contract
├── mapper        # DTO ↔ entity conversion
├── model.entity  # JPA entities
├── repository    # Spring Data repositories
├── service       # Business logic
└── util          # Specs & pagination helpers
```

## Environment
Copy `.env.example` to `.env` and adjust values:

| Variable | Description | Default |
| --- | --- | --- |
| `SERVER_PORT` | HTTP port | 8080 |
| `SPRING_PROFILES_ACTIVE` | Spring profile | dev |
| `SPRING_DATASOURCE_URL` | JDBC URL | `jdbc:mysql://localhost:3306/student_db...` |
| `SPRING_DATASOURCE_USERNAME` | DB user | root |
| `SPRING_DATASOURCE_PASSWORD` | DB password | root |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Schema mode | update |
| `MYSQL_ROOT_PASSWORD` | Docker MySQL root password | change-me |
| `MYSQL_DATABASE` | Docker MySQL schema | student_db |
| `APP_JWT_SECRET` | HMAC secret for JWT signing | `ChangeMeToAStrong...` |
| `APP_JWT_EXPIRATION` | Token lifetime in ms | `3600000` |

## Local Development
```bash
# install deps & run unit/integration tests
mvn clean verify

# run the API (requires MySQL running locally or via Docker)
mvn spring-boot:run
```

### With Docker Compose
```bash
docker compose up --build
```
This starts both MySQL (`mysql:8.4`) and the API. Compose reads `.env` for credentials and wires the service-to-service JDBC URL automatically.

### Sample Data
When `dev` profile is active, `DataInitializer` seeds three demo students exactly as shown in the spec screenshots.

## Testing Strategy
- **Unit Tests**: Service layer covered with JUnit 5 + Mockito.
- **Integration Tests**: `@SpringBootTest` + MockMvc backed by a real MySQL Testcontainer.
- Run everything with `mvn test` (Docker Desktop or compatible runtime must be running for Testcontainers; when Docker is unavailable the integration tests are automatically skipped).

## Authentication
- Endpoints:  
  - `POST /api/auth/signup` – register a user and receive a token  
  - `POST /api/auth/login` – exchange credentials for a JWT  
- Default dev credentials (seeded when `dev` profile is active): `admin@college.local / Admin@123`
- Include the token in subsequent calls: `Authorization: Bearer <token>`

```bash
# Signup
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Demo User","email":"demo@example.com","password":"Password!1"}' | jq -r '.token')

# Login (if already registered)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"Password!1"}' | jq -r '.token')
```

## REST API
Base URL: `/api/students` (all secured – supply `Authorization: Bearer <token>`)

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/` | Create student |
| `GET` | `/{id}` | Fetch active student |
| `GET` | `/` | List active students with `branch`, `yop`, pagination (`page`, `size`) and sorting (`sort=field,direction`) |
| `PUT` | `/{id}` | Full update |
| `PATCH` | `/{id}` | Partial update (full validation on provided fields) |
| `DELETE` | `/{id}` | Soft delete (toggle `active=false`) |
| `DELETE` | `/{id}?hard=true` | Hard delete record |

### Sample cURL
```bash
# Create
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"fullName":"Aisha Verma","email":"aisha@example.com","phone":"+91-9000011111","branch":"CSE","yop":2023}'

# List with filters & pagination
curl "http://localhost:8080/api/students?branch=CSE&yop=2023&page=0&size=5&sort=fullName,desc" \
  -H "Authorization: Bearer $TOKEN"

# PATCH
curl -X PATCH http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"phone":"+91-9000099999","active":true}'

# Soft delete
curl -X DELETE http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer $TOKEN"

# Hard delete
curl -X DELETE "http://localhost:8080/api/students/1?hard=true" \
  -H "Authorization: Bearer $TOKEN"
```

### Response (example)
```json
{
  "id": 1,
  "fullName": "Aisha Verma",
  "email": "aisha@example.com",
  "phone": "+91-9000011111",
  "branch": "CSE",
  "yop": 2023,
  "active": true,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

### Error Contract
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/students",
  "errors": [
    "email: Email must be valid"
  ]
}
```

## Postman Collection
Import `postman/StudentManagementCrudAPI.postman_collection.json` and set `baseUrl` / `studentId` environment variables as needed.

## Design Highlights & Assumptions
- DTO-only surface; entities never leave controller boundary.
- Centralized `GlobalExceptionHandler` to align with error spec.
- `StudentSpecifications` + `PagedResponse` handle filters, pagination, and sorting in a reusable manner.
- Soft delete is enforced in queries via specification; hard delete opt-in through query flag.
- CORS wide-open for sandboxing; tighten before production.
- `DataInitializer` seeds demo students plus a default admin user in `dev` for easy logins.
- Stateless JWT security protects every endpoint except `/api/auth/**` and `/actuator`.

## Deployment Notes
- Build image: `docker build -t student-management-api .`
- Run jar: `java -jar target/student-management-api-0.0.1-SNAPSHOT.jar`
- Tag release: `git tag v1.0.0 && git push origin v1.0.0`

## Useful Commands
```bash
mvn clean verify           # Run unit + integration tests
mvn spring-boot:run        # Start the API locally
docker compose up --build  # Run MySQL + API together
```

## Next Steps
- Optional pagination metadata caching layer (Redis) for heavy list usage.
- Wire RBAC/permissions if different roles should have limited access.

