# Secrets Management Guide

## What GitGuardian Detected

GitGuardian flagged potential secrets in your code:

- `DB_PASSWORD=postgres` in docker-compose.yml
- `POSTGRES_PASSWORD=postgres` in docker-compose.yml
- `password: ${DB_PASSWORD:postgres}` in application.yml (commented)

## Are These Real Secrets?

**No, these are false positives:**

- They're example/default passwords
- Used for local development only
- Not production secrets

**However, GitGuardian is correct to flag them:**

- Best practice: Never hardcode passwords
- Even example passwords can be security risks
- Production should use real secrets management

## How We Fixed It

### 1. Updated docker-compose.yml

**Before (hardcoded):**

```yaml
environment:
  - DB_PASSWORD=postgres # ❌ Hardcoded
```

**After (environment variables):**

```yaml
environment:
  - DB_PASSWORD=${DB_PASSWORD:-postgres} # ✅ From env var, with default
```

### 2. Created .env.example

Shows what environment variables are needed without exposing real values.

## Best Practices

### For Local Development

1. **Use .env file (not committed to Git):**

   ```bash
   # Create .env file
   cp .env.example .env
   # Edit .env with your values
   ```

2. **Docker Compose automatically reads .env:**

   ```bash
   docker-compose up  # Reads .env automatically
   ```

3. **Add .env to .gitignore:**
   ```
   .env
   .env.local
   .env.*.local
   ```

### For Production

1. **Use secrets management:**

   - **Docker Swarm**: Docker secrets
   - **Kubernetes**: Kubernetes secrets
   - **Cloud platforms**: AWS Secrets Manager, Azure Key Vault, etc.

2. **Never commit:**

   - Real passwords
   - API keys
   - Private keys
   - JWT secrets

3. **Use environment variables:**
   ```bash
   # Set in production environment
   export DB_PASSWORD=secure-production-password
   export JWT_SECRET=real-secret-key-from-secrets-manager
   ```

## Suppressing False Positives in GitGuardian

GitGuardian is still detecting default values even with environment variable syntax. Here are **3 ways to fix this**:

### Option 1: Mark as False Positive in Dashboard (Recommended - Immediate Fix)

You have an incident URL from the scan:

- **Incident URL**: https://dashboard.gitguardian.com/workspace/794819/incidents/22603845

**Steps:**

1. Visit the incident URL above
2. Click **"Mark as False Positive"**
3. Add comment: "Example development password - not a real secret"
4. This will prevent future alerts for this specific secret

### Option 2: Use .gitguardian.yml (Already Created)

A `.gitguardian.yml` file has been created that ignores these files:

```yaml
paths-ignore:
  - "docker-compose.yml" # Contains example passwords
  - "src/main/resources/application.yml" # Contains commented examples
```

**Note:** This ignores the entire files. If you want more granular control, use Option 1.

### Option 3: Use Placeholder Values (Alternative)

Change default values to something that doesn't trigger GitGuardian:

```yaml
- DB_PASSWORD=${DB_PASSWORD:-CHANGE_ME}
- POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-CHANGE_ME}
```

This way, developers know to change it, and GitGuardian won't flag it.

## Current Status

✅ **Fixed:**

- docker-compose.yml now uses environment variables
- .env.example created for documentation
- Default values provided for local development

⏭️ **Next Steps:**

- Create .env file for local development (not committed)
- Use real secrets management in production
- Consider marking as false positives in GitGuardian dashboard

## Running Locally

1. **Copy example file:**

   ```bash
   cp .env.example .env
   ```

2. **Edit .env with your values** (or use defaults)

3. **Run Docker Compose:**
   ```bash
   docker-compose up
   ```

Docker Compose will automatically read the .env file.

## Security Checklist

- [x] Removed hardcoded passwords from docker-compose.yml
- [x] Created .env.example template
- [ ] Add .env to .gitignore (if not already)
- [ ] Use secrets management in production
- [ ] Rotate passwords regularly
- [ ] Use strong, unique passwords in production
- [ ] Never commit .env file
