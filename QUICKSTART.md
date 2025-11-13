# Quick Start Guide - How to Run the URL Shortener

## Prerequisites

You need to install the following before running the project:

### Option 1: Install Java and Maven (Recommended for Development)

#### Install Java 17
**On macOS (using Homebrew):**
```bash
brew install openjdk@17
```

**On macOS (using SDKMAN - Recommended):**
```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 17
sdk install java 17.0.9-tem
```

**On Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**On Windows:**
- Download from: https://adoptium.net/temurin/releases/?version=17
- Install and set JAVA_HOME environment variable

#### Install Maven
**On macOS (using Homebrew):**
```bash
brew install maven
```

**On macOS (using SDKMAN):**
```bash
sdk install maven
```

**On Linux (Ubuntu/Debian):**
```bash
sudo apt install maven
```

**On Windows:**
- Download from: https://maven.apache.org/download.cgi
- Extract and add to PATH

#### Verify Installation
```bash
java -version  # Should show version 17 or higher
mvn --version  # Should show Maven 3.9+
```

### Option 2: Use Docker (Easiest - No Installation Needed)

If you have Docker installed, you can skip Java/Maven installation and use Docker instead.

## Running the Project

### Method 1: Using Maven (After Installing Prerequisites)

#### Step 1: Build the Project
```bash
cd /Users/naijawebmaster/Documents/GitHub/software-dependability-accessment
mvn clean install
```

#### Step 2: Run the Application
```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

#### Step 3: Verify It's Running
Open your browser and go to:
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console

### Method 2: Using Docker (Easiest)

#### Step 1: Build Docker Image
```bash
cd /Users/naijawebmaster/Documents/GitHub/software-dependability-accessment
docker build -t url-shortener:latest .
```

#### Step 2: Run with Docker
```bash
docker run -p 8080:8080 url-shortener:latest
```

### Method 3: Using Docker Compose (With PostgreSQL)

#### Step 1: Start Everything
```bash
cd /Users/naijawebmaster/Documents/GitHub/software-dependability-accessment
docker-compose up -d
```

This starts:
- URL Shortener app on port 8080
- PostgreSQL database on port 5432

#### Step 2: View Logs
```bash
docker-compose logs -f url-shortener
```

#### Step 3: Stop Everything
```bash
docker-compose down
```

## Testing the API

Once the application is running, you can test it using `curl` or any REST client.

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 2. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "testuser"
}
```

Save the token for next steps.

### 3. Shorten a URL
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.example.com"
  }'
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/ABCD1234",
  "shortCode": "ABCD1234",
  "originalUrl": "https://www.example.com"
}
```

### 4. Redirect to Original URL
Open in browser or use curl:
```bash
curl -L http://localhost:8080/ABCD1234
```

### 5. Get Statistics (Requires Authentication)
```bash
curl -X GET http://localhost:8080/api/stats/ABCD1234 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Using the H2 Database Console (Development Only)

1. Go to: http://localhost:8080/h2-console
2. Use these settings:
   - **JDBC URL**: `jdbc:h2:mem:urlshortener`
   - **Username**: `sa`
   - **Password**: (leave empty)
3. Click "Connect"

## Troubleshooting

### Port 8080 Already in Use
If port 8080 is already in use, change it in `src/main/resources/application.yml`:
```yaml
server:
  port: 8081  # Change to any available port
```

### Java Version Issues
Make sure you're using Java 17:
```bash
java -version
# Should show: openjdk version "17.x.x"
```

### Maven Build Fails
Try cleaning and rebuilding:
```bash
mvn clean
mvn install -U  # -U forces update of dependencies
```

### Docker Issues
If Docker build fails:
```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker build --no-cache -t url-shortener:latest .
```

## Next Steps

- Read the full [README.md](README.md) for detailed documentation
- Run tests: `mvn test`
- Check code coverage: `mvn jacoco:report`
- View coverage report: `open target/site/jacoco/index.html`

## Quick Commands Reference

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Run with Docker
docker build -t url-shortener . && docker run -p 8080:8080 url-shortener

# Run with Docker Compose
docker-compose up -d
```

