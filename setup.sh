#!/bin/bash

# URL Shortener Setup Script for macOS
# This script installs Java 17 and Maven, then builds and runs the project

set -e

echo "🚀 URL Shortener Setup Script"
echo "=============================="
echo ""

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "❌ Homebrew is not installed. Please install it first:"
    echo "   /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
    exit 1
fi

echo "✅ Homebrew found"
echo ""

# Install Java 17
echo "📦 Installing Java 17..."
if brew list --formula | grep -q "openjdk@17"; then
    echo "   Java 17 already installed"
else
    brew install openjdk@17
    echo "   ✅ Java 17 installed"
fi

# Link Java
echo "🔗 Setting up Java..."
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk 2>/dev/null || true

# Set JAVA_HOME for this session
export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || echo "/opt/homebrew/opt/openjdk@17")
export PATH="$JAVA_HOME/bin:$PATH"

# Install Maven
echo "📦 Installing Maven..."
if brew list --formula | grep -q "^maven$"; then
    echo "   Maven already installed"
else
    brew install maven
    echo "   ✅ Maven installed"
fi

# Add to PATH for this session
export PATH="/opt/homebrew/bin:$PATH"

echo ""
echo "🔍 Verifying installation..."
echo ""

# Verify Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo "✅ Java: $JAVA_VERSION"
else
    echo "⚠️  Java not found in PATH. You may need to restart your terminal or run:"
    echo "   export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
    echo "   export PATH=\"\$JAVA_HOME/bin:\$PATH\""
fi

# Verify Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn --version | head -1)
    echo "✅ Maven: $MVN_VERSION"
else
    echo "⚠️  Maven not found in PATH. You may need to restart your terminal."
fi

echo ""
echo "📦 Building the project..."
echo ""

# Build the project
mvn clean install -DskipTests || {
    echo "❌ Build failed. Please check the error messages above."
    exit 1
}

echo ""
echo "✅ Setup complete!"
echo ""
echo "🎯 To run the application, use:"
echo "   mvn spring-boot:run"
echo ""
echo "   Or run this script with --run flag:"
echo "   ./setup.sh --run"
echo ""

# Run if requested
if [[ "$1" == "--run" ]]; then
    echo "🚀 Starting the application..."
    echo ""
    mvn spring-boot:run
fi

