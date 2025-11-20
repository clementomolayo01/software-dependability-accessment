# GitGuardian Secret Scanning Setup

## Issue Fixed

The workflow had an invalid parameter `exit_zero: false` which is not supported by the GitGuardian action. This has been removed.

## How to Fix the "Invalid GitGuardian API key" Error

### Option 1: Set Up GitGuardian API Key (Recommended)

1. **Create a GitGuardian Account:**
   - Go to https://dashboard.gitguardian.com
   - Sign up for a free account (free tier available)

2. **Generate an API Key:**
   - Log in to GitGuardian dashboard
   - Go to **Settings** → **API** → **Personal Access Tokens**
   - Click **Create Token**
   - Give it a name (e.g., "GitHub Actions")
   - Copy the token (you won't see it again!)

3. **Add to GitHub Secrets:**
   - Go to your GitHub repository
   - Navigate to: **Settings** → **Secrets and variables** → **Actions**
   - Click **New repository secret**
   - Name: `GITGUARDIAN_API_KEY`
   - Value: Paste your GitGuardian API token
   - Click **Add secret**

4. **Test the Workflow:**
   - Push a commit or create a pull request
   - Check the **Actions** tab
   - The GitGuardian scan should now work

### Option 2: Skip GitGuardian (For Development)

If you don't want to use GitGuardian right now, you can:

1. **Make the step optional:**
   The workflow is already configured to continue on error if the API key is missing.

2. **Or comment out the step:**
   Edit `.github/workflows/ci-cd.yml` and comment out the GitGuardian step:
   ```yaml
   # - name: GitGuardian Secret Scanning
   #   uses: GitGuardian/ggshield-action@master
   #   env:
   #     GITGUARDIAN_API_KEY: ${{ secrets.GITGUARDIAN_API_KEY }}
   #   continue-on-error: ${{ secrets.GITGUARDIAN_API_KEY == '' }}
   ```

## What GitGuardian Does

GitGuardian scans your code for:
- **Secrets**: API keys, passwords, tokens
- **Credentials**: Database passwords, AWS keys
- **Sensitive data**: Private keys, certificates

This helps prevent accidentally committing secrets to your repository.

## Free Tier Limits

GitGuardian offers a free tier with:
- 2,500 scans per month
- Public and private repositories
- Basic secret detection

## Verification

After setting up the API key, you should see in GitHub Actions:
```
✅ GitGuardian Secret Scanning
  No secrets detected
```

Or if secrets are found:
```
⚠️ GitGuardian Secret Scanning
  Found 2 secrets in commit
```

## Troubleshooting

### Error: "Invalid GitGuardian API key"
- ✅ Check the API key is correct in GitHub Secrets
- ✅ Verify the token hasn't expired
- ✅ Make sure there are no extra spaces when copying

### Error: "Unexpected input 'exit_zero'"
- ✅ Fixed! The invalid parameter has been removed

### Workflow continues even with errors
- ✅ This is intentional - the step has `continue-on-error` if API key is missing
- ✅ Once you add the API key, it will fail the workflow if secrets are found

## Alternative: Use GitHub's Built-in Secret Scanning

GitHub also has built-in secret scanning (free):
- Automatically scans for secrets in public repositories
- Works without additional setup
- Limited to known secret patterns

GitGuardian provides:
- More comprehensive scanning
- Works on private repositories
- Custom secret patterns
- Better reporting

