# URL Shortener - Production-Grade Java Web Application

A comprehensive, production-ready URL Shortener web application built with Spring Boot, featuring formal verification (JML), comprehensive testing, security scanning, and CI/CD integration.

## Features

- **RESTful API** for URL shortening and retrieval
- **JWT-based Authentication** for secure API access
- **Visit Statistics** tracking for shortened URLs
- **Formal Verification** using JML (Java Modeling Language) annotations
- **Comprehensive Testing** with JUnit 5, Mockito, JaCoCo, and PiTest
- **Performance Benchmarks** using JMH (Java Microbenchmark Harness)
- **Security Scanning** integrated with GitGuardian, Snyk, and SonarCloud
- **Docker Support** with multi-stage builds
- **CI/CD Pipeline** with GitHub Actions

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Java Version**: 17
- **Build Tool**: Maven 3.9+
- **Database**: H2 (in-memory) / PostgreSQL
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, Mockito, JaCoCo, PiTest, JMH
- **Formal Verification**: OpenJML
- **Containerization**: Docker

## Prerequisites

- Java 17 or higher
- Maven 3.9+ 
- Docker and Docker Compose (optional, for containerized deployment)
- PostgreSQL (optional, for production database)

## Building and Running Locally

### 1. Clone the Repository

```bash
git clone <repository-url>
cd software-dependability-accessment
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Using H2 Console (Development)

Access the H2 console at `http://localhost:8080/h2-console` with:
- JDBC URL: `jdbc:h2:mem:urlshortener`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "testuser"
}
```

### URL Shortening

#### Shorten URL
```http
POST /api/shorten
Content-Type: application/json

{
  "url": "https://www.example.com"
}
```

Response:
```json
{
  "shortUrl": "http://localhost:8080/ABCD1234",
  "shortCode": "ABCD1234",
  "originalUrl": "https://www.example.com"
}
```

#### Redirect to Original URL
```http
GET /{shortCode}
```

Example: `GET /ABCD1234` redirects to the original URL.

#### Get Statistics
```http
GET /api/stats/{shortCode}
Authorization: Bearer {token}
```

Response:
```json
{
  "shortCode": "ABCD1234",
  "originalUrl": "https://www.example.com",
  "shortUrl": "http://localhost:8080/ABCD1234",
  "clickCount": 42,
  "createdAt": "2024-01-15T10:30:00",
  "expiresAt": "2025-01-15T10:30:00"
}
```

## Testing

### Run All Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Code Coverage (JaCoCo)

Generate coverage report:

```bash
mvn jacoco:report
```

View HTML report:
```bash
open target/site/jacoco/index.html
```

Check coverage threshold:
```bash
mvn jacoco:check
```

### Mutation Testing (PiTest)

Run mutation tests:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

View HTML report:
```bash
open target/pit-reports/index.html
```

### Performance Benchmarks (JMH)

Build benchmarks:

```bash
mvn clean package -DskipTests
```

Run benchmarks:

```bash
java -jar target/benchmarks.jar
```

Run specific benchmark:

```bash
java -jar target/benchmarks.jar UrlShortenerBenchmark
```

## JML Verification

The project uses JML (Java Modeling Language) annotations for formal verification. Core methods in `UrlShortenerService` and `JwtTokenProvider` include JML specifications.

### Verify JML Contracts

1. Install OpenJML:
   ```bash
   wget https://github.com/OpenJML/OpenJML/releases/download/0.9.0/openjml-0.9.0.zip
   unzip openjml-0.9.0.zip
   export PATH=$PATH:$(pwd)/openjml
   ```

2. Compile with OpenJML:
   ```bash
   mvn clean compile
   ```

3. Run verification:
   ```bash
   openjml -check src/main/java/com/urlshortener/service/UrlShortenerService.java
   ```

## Docker

### Build Docker Image

```bash
docker build -t url-shortener:latest .
```

### Run with Docker

```bash
docker run -p 8080:8080 url-shortener:latest
```

### Docker Compose (with PostgreSQL)

```bash
docker-compose up -d
```

This starts:
- URL Shortener application on port 8080
- PostgreSQL database on port 5432

### Push to DockerHub

1. Login to DockerHub:
   ```bash
   docker login
   ```

2. Tag the image:
   ```bash
   docker tag url-shortener:latest <your-username>/url-shortener:latest
   ```

3. Push:
   ```bash
   docker push <your-username>/url-shortener:latest
   ```

## Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/urlshortener
    username: postgres
    password: postgres

jwt:
  secret: your-256-bit-secret-key-minimum-32-characters
  expiration: 86400000  # 24 hours
```

### Environment Variables

- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: JWT secret key (minimum 32 characters)

## CI/CD Pipeline

The project includes a comprehensive GitHub Actions workflow (`.github/workflows/ci-cd.yml`) that:

1. **Builds and Tests**: Compiles, runs unit and integration tests
2. **Code Coverage**: Generates JaCoCo reports and checks thresholds
3. **Mutation Testing**: Runs PiTest mutation analysis
4. **JML Verification**: Verifies JML contracts (placeholder)
5. **Security Scanning**: 
   - GitGuardian for secret scanning
   - Snyk for dependency vulnerabilities
   - SonarCloud for code quality
6. **Docker Build**: Builds and pushes Docker image to DockerHub
7. **Performance Benchmarks**: Runs JMH benchmarks

### Required GitHub Secrets

Configure these secrets in your GitHub repository:

- `DOCKER_USERNAME`: DockerHub username
- `DOCKER_PASSWORD`: DockerHub password/token
- `GITGUARDIAN_API_KEY`: GitGuardian API key
- `SNYK_TOKEN`: Snyk API token
- `SONAR_TOKEN`: SonarCloud token

### SonarCloud Setup

1. Create a project on [SonarCloud](https://sonarcloud.io)
2. Update `sonar-project.properties` with your organization key
3. Add `SONAR_TOKEN` to GitHub secrets

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/com/urlshortener/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic with JML
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── security/        # JWT and security
│   │   │   ├── config/          # Configuration
│   │   │   ├── exception/       # Exception handlers
│   │   │   └── benchmark/       # JMH benchmarks
│   │   └── resources/
│   │       └── application.yml  # Configuration
│   └── test/
│       └── java/com/urlshortener/
│           ├── service/         # Unit tests
│           ├── controller/      # Controller tests
│           ├── integration/    # Integration tests
│           └── security/        # Security tests
├── .github/workflows/           # CI/CD pipelines
├── Dockerfile                   # Docker image definition
├── docker-compose.yml          # Docker Compose setup
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

## Security

- **JWT Authentication**: Secure token-based authentication
- **Password Encryption**: BCrypt password hashing
- **Input Validation**: Bean validation on all endpoints
- **Security Scanning**: Automated vulnerability detection
- **No Known Vulnerabilities**: All dependencies are scanned and updated

## Performance

- **JMH Benchmarks**: Microbenchmarks for critical operations
- **Database Indexing**: Optimized queries with indexes
- **Connection Pooling**: Efficient database connection management

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please open an issue on GitHub.

## Acknowledgments

- Spring Boot team
- OpenJML contributors
- PiTest developers
- JMH team

