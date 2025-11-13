#!/bin/bash

echo "🧪 Testing URL Shortener API"
echo "============================"
echo ""

# Test 1: Check API info
echo "1️⃣  Testing root endpoint..."
curl -s http://localhost:8080/ | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/
echo ""
echo ""

# Test 2: Shorten URL
echo "2️⃣  Shortening a URL..."
SHORTEN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.example.com"}')
echo "$SHORTEN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$SHORTEN_RESPONSE"
SHORT_CODE=$(echo "$SHORTEN_RESPONSE" | grep -o '"shortCode":"[^"]*' | cut -d'"' -f4)
echo "Short Code: $SHORT_CODE"
echo ""
echo ""

# Test 3: Register user
echo "3️⃣  Registering a user..."
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}' | python3 -m json.tool 2>/dev/null || echo "User registered"
echo ""
echo ""

# Test 4: Login
echo "4️⃣  Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}')
echo "$LOGIN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$LOGIN_RESPONSE"
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token received: ${TOKEN:0:50}..."
echo ""
echo ""

# Test 5: Get statistics
if [ ! -z "$SHORT_CODE" ] && [ ! -z "$TOKEN" ]; then
  echo "5️⃣  Getting statistics..."
  curl -s -X GET "http://localhost:8080/api/stats/$SHORT_CODE" \
    -H "Authorization: Bearer $TOKEN" | python3 -m json.tool 2>/dev/null || \
    curl -s -X GET "http://localhost:8080/api/stats/$SHORT_CODE" \
    -H "Authorization: Bearer $TOKEN"
  echo ""
  echo ""
fi

# Test 6: Health check
echo "6️⃣  Health check..."
curl -s http://localhost:8080/actuator/health | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/actuator/health
echo ""
echo ""

echo "✅ All tests completed!"
