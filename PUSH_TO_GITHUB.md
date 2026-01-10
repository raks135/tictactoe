# Push to GitHub - Quick Commands

## Option 1: Using Git Command Line

### First Time Setup (One Time Only):

```bash
# Navigate to your project
cd c:\tictactoe

# Initialize git repository
git init

# Add all files
git add .

# Commit files
git commit -m "Initial commit - Tic-Tac-Toe Android app"

# Add your GitHub repository as remote
# Replace YOUR_USERNAME and YOUR_REPO with your actual GitHub username and repo name
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git

# Push to GitHub
git push -u origin main
```

If you get an error about "main" branch, try:
```bash
git branch -M main
git push -u origin main
```

---

## Option 2: Using GitHub Desktop (Easiest)

### Step 1: Download GitHub Desktop
- Go to: https://desktop.github.com
- Download and install

### Step 2: Sign In
- Open GitHub Desktop
- Sign in with your GitHub account

### Step 3: Add Repository
- Click "File" → "Add local repository"
- Choose: `c:\tictactoe`
- If it says "not a git repository", click "Create repository"

### Step 4: Publish
- Click "Publish repository" button
- Choose name: `tictactoe-android`
- Uncheck "Keep this code private" (for free Actions)
- Click "Publish repository"

**Done!** Your code is now on GitHub.

---

## Option 3: Web Upload (No Git Required)

### Step 1: Create Repository
- Go to https://github.com
- Click "+" → "New repository"
- Name: `tictactoe-android`
- Public ✓
- Click "Create repository"

### Step 2: Upload Files
- Click "uploading an existing file"
- Drag the ENTIRE `c:\tictactoe` folder
- Scroll down
- Click "Commit changes"

**Important:** Make sure to upload the whole folder, including hidden folders like `.github`

---

## Verify Upload

After pushing, check your GitHub repository has:
- ✓ `.github/workflows/build-apk.yml`
- ✓ `app/` folder
- ✓ `gradle/` folder
- ✓ `gradlew` and `gradlew.bat`
- ✓ `build.gradle.kts`

---

## After Pushing

1. Go to your repository on GitHub
2. Click "Actions" tab
3. Build should start automatically
4. Wait 3-5 minutes
5. Click on the workflow run
6. Scroll to bottom
7. Download APK from "Artifacts"

---

## Troubleshooting

### "Permission denied"
```bash
# Set your Git credentials
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### "Authentication failed"
- Use GitHub Desktop (easier)
- Or create a Personal Access Token:
  - GitHub → Settings → Developer settings → Personal access tokens
  - Use token as password when pushing

### "Repository not found"
- Make sure you created the repository on GitHub first
- Check the repository URL is correct

---

## Quick Commands Reference

```bash
# Check status
git status

# Add all files
git add .

# Commit changes
git commit -m "Your message"

# Push to GitHub
git push

# View remote URL
git remote -v
```
