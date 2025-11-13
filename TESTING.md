# How to Run Tests

## Quick Commands

### 1. Run All Tests
```bash
mvn test
```

### 2. Run Only Unit Tests (Service Tests)
```bash
mvn test -Dtest=*ServiceTest
```

### 3. Run Only Integration Tests
```bash
mvn verify
# or
mvn test -Dtest=*IntegrationTest
```

### 4. Run a Specific Test Class
```bash
mvn test -Dtest=UrlShortenerServiceTest
```

### 5. Run a Specific Test Method
```bash
mvn test -Dtest=UrlShortenerServiceTest#testShortenUrl_ValidUrl_ReturnsShortCode
```

### 6. Run Tests with Coverage (JaCoCo)
```bash
mvn clean test jacoco:report
```

View coverage report:
```bash
open target/site/jacoco/index.html
```

### 7. Run Mutation Testing (PiTest)
```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

View mutation report:
```bash
open target/pit-reports/index.html
```

### 8. Skip Tests (Build Only)
```bash
mvn clean install -DskipTests
```

### 9. Run Tests in Verbose Mode
```bash
mvn test -X
```

### 10. Run Tests and See Output
```bash
mvn test -Dmaven.test.failure.ignore=true
```

## Test Structure

### Unit Tests (Service Layer)
- `UrlShortenerServiceTest.java` - Tests URL shortening logic
- `UserServiceTest.java` - Tests user management
- `JwtTokenProviderTest.java` - Tests JWT token generation

### Controller Tests (Web Layer)
- `UrlControllerTest.java` - Tests REST endpoints
- `AuthControllerTest.java` - Tests authentication endpoints

### Integration Tests
- `UrlShortenerIntegrationTest.java` - End-to-end tests

## Current Test Status

Some controller tests are currently failing due to Spring context loading issues. The service tests (unit tests) should pass.

## Quick Test Commands Reference

```bash
# All tests
mvn test

# Only passing tests (service tests)
mvn test -Dtest=*ServiceTest,*JwtTokenProviderTest

# With coverage
mvn clean test jacoco:report

# Specific test
mvn test -Dtest=UrlShortenerServiceTest
```

