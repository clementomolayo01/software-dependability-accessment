# How to Run the URL Shortener Project

## Quick Start (Recommended)

### Option 1: Use the Setup Script (Easiest)

```bash
# Run the setup script (installs Java & Maven, then builds)
./setup.sh

# Or install and run in one command
./setup.sh --run
```

### Option 2: Manual Installation

#### Step 1: Install Java 17
```bash
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

#### Step 2: Install Maven
```bash
brew install maven
```

#### Step 3: Set up Java in your shell
Add these lines to your `~/.zshrc`:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

Then reload:
```bash
source ~/.zshrc
```

#### Step 4: Verify Installation
```bash
java -version  # Should show Java 17
mvn --version  # Should show Maven
```

#### Step 5: Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## After Running

The application will be available at:
- **Main URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console

## Test the API

### 1. Shorten a URL
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.example.com"}'
```

### 2. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

## Troubleshooting

### "command not found: mvn" or "command not found: java"

**Solution**: Restart your terminal or run:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
export PATH="/opt/homebrew/bin:$PATH"
```

### Port 8080 already in use

Change the port in `src/main/resources/application.yml`:
```yaml
server:
  port: 8081
```

### Build fails

Try:
```bash
mvn clean
mvn install -U
```

