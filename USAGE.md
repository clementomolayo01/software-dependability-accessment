# How to Use the URL Shortener Application

## Quick Start

The application is running at **http://localhost:8080**

## 1. Shorten a URL (No Authentication Required)

### Using curl:
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.example.com"}'
```

### Response:
```json
{
  "shortUrl": "http://localhost:8080/zbTYjcoL",
  "shortCode": "zbTYjcoL",
  "originalUrl": "https://www.example.com"
}
```

### Using a REST Client (Postman, Insomnia, etc.):
- **Method**: POST
- **URL**: `http://localhost:8080/api/shorten`
- **Headers**: `Content-Type: application/json`
- **Body** (JSON):
  ```json
  {
    "url": "https://www.example.com"
  }
  ```

## 2. Access Shortened URL

Simply open the short URL in your browser or use curl:

```bash
# In browser, just visit:
http://localhost:8080/zbTYjcoL

# Or with curl (follow redirects):
curl -L http://localhost:8080/zbTYjcoL
```

This will redirect you to the original URL.

## 3. Get Statistics (Requires Authentication)

### Step 1: Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "password": "mypassword123"
  }'
```

### Step 2: Login to Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "password": "mypassword123"
  }'
```

### Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "myuser"
}
```

### Step 3: Get Statistics Using the Token

```bash
# Save the token from login response
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Get statistics
curl -X GET http://localhost:8080/api/stats/zbTYjcoL \
  -H "Authorization: Bearer $TOKEN"
```

### Response:
```json
{
  "shortCode": "zbTYjcoL",
  "originalUrl": "https://www.example.com",
  "shortUrl": "http://localhost:8080/zbTYjcoL",
  "clickCount": 5,
  "createdAt": "2024-11-13T10:30:00",
  "expiresAt": "2025-11-13T10:30:00"
}
```

## 4. Complete Example Workflow

```bash
# 1. Shorten a URL
SHORTEN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.google.com"}')

echo "Shortened URL: $SHORTEN_RESPONSE"

# Extract short code (requires jq or manual parsing)
SHORT_CODE=$(echo $SHORTEN_RESPONSE | grep -o '"shortCode":"[^"]*' | cut -d'"' -f4)
echo "Short Code: $SHORT_CODE"

# 2. Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}'

# 3. Login
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token: $TOKEN"

# 4. Get statistics
curl -X GET http://localhost:8080/api/stats/$SHORT_CODE \
  -H "Authorization: Bearer $TOKEN"

# 5. Access the shortened URL
curl -L http://localhost:8080/$SHORT_CODE
```

## 5. Using in Browser

### Shorten URL:
1. Open browser developer tools (F12)
2. Go to Console tab
3. Run:
```javascript
fetch('http://localhost:8080/api/shorten', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({url: 'https://www.example.com'})
})
.then(r => r.json())
.then(data => console.log(data));
```

### Access Shortened URL:
Just paste the short URL in the browser address bar:
```
http://localhost:8080/zbTYjcoL
```

## 6. Using with JavaScript/Node.js

```javascript
// Shorten URL
async function shortenUrl(longUrl) {
  const response = await fetch('http://localhost:8080/api/shorten', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({url: longUrl})
  });
  return await response.json();
}

// Usage
shortenUrl('https://www.example.com')
  .then(result => console.log('Short URL:', result.shortUrl));

// Login
async function login(username, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({username, password})
  });
  return await response.json();
}

// Get Statistics
async function getStats(shortCode, token) {
  const response = await fetch(`http://localhost:8080/api/stats/${shortCode}`, {
    headers: {'Authorization': `Bearer ${token}`}
  });
  return await response.json();
}
```

## 7. Using with Python

```python
import requests

# Shorten URL
def shorten_url(url):
    response = requests.post(
        'http://localhost:8080/api/shorten',
        json={'url': url}
    )
    return response.json()

# Register
def register(username, password):
    response = requests.post(
        'http://localhost:8080/api/auth/register',
        json={'username': username, 'password': password}
    )
    return response.json()

# Login
def login(username, password):
    response = requests.post(
        'http://localhost:8080/api/auth/login',
        json={'username': username, 'password': password}
    )
    return response.json()

# Get Statistics
def get_stats(short_code, token):
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get(
        f'http://localhost:8080/api/stats/{short_code}',
        headers=headers
    )
    return response.json()

# Example usage
result = shorten_url('https://www.example.com')
print(f"Short URL: {result['shortUrl']}")

login_result = login('myuser', 'mypassword')
token = login_result['token']

stats = get_stats(result['shortCode'], token)
print(f"Click count: {stats['clickCount']}")
```

## 8. API Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| GET | `/` | No | API information |
| POST | `/api/shorten` | No | Shorten a URL |
| GET | `/{shortCode}` | No | Redirect to original URL |
| GET | `/api/stats/{shortCode}` | Yes | Get URL statistics |
| POST | `/api/auth/register` | No | Register new user |
| POST | `/api/auth/login` | No | Login and get JWT token |

## 9. Error Handling

### Invalid URL:
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "invalid-url"}'
```
**Response**: `400 Bad Request` with error message

### Short Code Not Found:
```bash
curl http://localhost:8080/INVALID
```
**Response**: `404 Not Found`

### Unauthorized (Missing/Invalid Token):
```bash
curl http://localhost:8080/api/stats/abc123
```
**Response**: `401 Unauthorized`

## 10. Testing the Application

### Quick Test Script:
```bash
#!/bin/bash

# Test 1: Shorten URL
echo "1. Shortening URL..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.example.com"}')
echo "$RESPONSE"
echo ""

# Test 2: Register
echo "2. Registering user..."
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}'
echo ""
echo ""

# Test 3: Login
echo "3. Logging in..."
LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass123"}')
echo "$LOGIN"
echo ""

echo "✅ All tests completed!"
```

## Tips

1. **Save your JWT token** - It's valid for 24 hours
2. **Short codes are case-sensitive** - `ABC123` is different from `abc123`
3. **URLs expire after 1 year** by default
4. **Click count increments** every time someone accesses the short URL
5. **No authentication needed** to shorten or access URLs
6. **Authentication required** only for viewing statistics

## Need Help?

- Check the API info at: http://localhost:8080/
- View health status: http://localhost:8080/actuator/health
- Access H2 database console: http://localhost:8080/h2-console

