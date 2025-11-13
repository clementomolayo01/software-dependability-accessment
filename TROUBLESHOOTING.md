# Troubleshooting URL Redirect Issues

## Issue: Short URL Not Redirecting

### Check 1: Verify the Short Code Exists

```bash
# Test with curl (should return 302 redirect)
curl -v http://localhost:8080/CWV56jDd

# Follow redirects automatically
curl -L http://localhost:8080/CWV56jDd
```

### Check 2: Check if URL is Expired

URLs expire after 1 year. Check expiration:

```bash
# Get statistics (requires authentication)
curl -X GET http://localhost:8080/api/stats/CWV56jDd \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Check 3: Browser Issues

**Problem**: Browser might not follow redirects or show an error page.

**Solutions**:

1. **Clear browser cache** - Old redirects might be cached
2. **Try incognito/private mode** - Rules out cache issues
3. **Check browser console** - Look for JavaScript errors (F12)
4. **Try different browser** - Chrome, Firefox, Safari

### Check 4: Network/Security Issues

**Problem**: CORS, security policies, or network blocking.

**Solutions**:

1. **Check browser console** for CORS errors
2. **Disable browser extensions** that might block redirects
3. **Check firewall/antivirus** settings

### Check 5: Application Logs

Check if the application is running and see logs:

```bash
# If running with Maven
# Check terminal where you ran: mvn spring-boot:run

# Check if app is running
curl http://localhost:8080/actuator/health
```

## Common Error Messages

### "404 Not Found"

- **Cause**: Short code doesn't exist or URL expired
- **Solution**: Verify the short code is correct, check expiration

### "403 Forbidden"

- **Cause**: Security configuration blocking access
- **Solution**: Check SecurityConfig.java - redirect endpoint should be permitted

### "500 Internal Server Error"

- **Cause**: Application error
- **Solution**: Check application logs, verify database connection

### Browser Shows Error Page

- **Cause**: Browser security or redirect loop
- **Solution**: Try curl first, then check browser settings

## Testing Steps

1. **Test with curl first**:

   ```bash
   curl -v http://localhost:8080/CWV56jDd
   ```

   Should return: `HTTP/1.1 302` with `Location:` header

2. **Follow redirect**:

   ```bash
   curl -L http://localhost:8080/CWV56jDd
   ```

   Should show the final destination

3. **Check in browser**:

   - Open: `http://localhost:8080/CWV56jDd`
   - Should automatically redirect

4. **Verify short code exists**:

   ```bash
   # Create a new short URL to test
   curl -X POST http://localhost:8080/api/shorten \
     -H "Content-Type: application/json" \
     -d '{"url": "https://www.example.com"}'

   # Use the returned shortCode to test
   ```

## Quick Fixes

### If redirect doesn't work in browser:

1. Try incognito mode
2. Clear browser cache
3. Check browser console (F12) for errors
4. Try a different browser

### If you get 404:

1. Verify the short code is correct (case-sensitive)
2. Check if URL expired (1 year default)
3. Create a new short URL and test

### If you get 500 error:

1. Check application logs
2. Restart the application
3. Verify database is running
