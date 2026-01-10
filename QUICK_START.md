# How to Build and Install the APK

## Quick Start (Easiest Method)

### Step 1: Open in Android Studio
1. Launch **Android Studio**
2. Click **"Open"** (or File → Open)
3. Navigate to and select: `c:\tictactoe`
4. Click **OK**

### Step 2: Wait for Gradle Sync
- Android Studio will automatically sync Gradle
- This downloads all dependencies (may take a few minutes first time)
- Wait for "Gradle sync finished" message in the status bar

### Step 3: Build the APK
1. Go to menu: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for build to complete (usually 1-2 minutes)
3. You'll see a notification: "APK(s) generated successfully"

### Step 4: Locate the APK
- Click **"locate"** in the notification, OR
- Navigate to: `c:\tictactoe\app\build\outputs\apk\debug\app-debug.apk`

### Step 5: Install on Android Device
1. **Transfer APK to your phone:**
   - Email it to yourself
   - Use USB cable and copy to phone
   - Upload to cloud storage and download on phone
   - Use ADB: `adb install app-debug.apk`

2. **Install on phone:**
   - Tap the APK file
   - Enable "Install from Unknown Sources" if prompted
   - Tap "Install"
   - Tap "Open" to launch the app

---

## Alternative: Build from Command Line

If you have Android SDK and Gradle installed:

```bash
cd c:\tictactoe
.\gradlew assembleDebug
```

APK will be at: `app\build\outputs\apk\debug\app-debug.apk`

---

## Building a Release APK (Signed)

For distribution outside of testing:

1. In Android Studio: **Build → Generate Signed Bundle / APK**
2. Select **APK**
3. Click **Create new...** to create a keystore
4. Fill in keystore details:
   - Key store path: Choose a location
   - Password: Create a strong password
   - Alias: `tictactoe`
   - Validity: 25 years (default)
   - Certificate info: Fill in your details
5. Click **Next**
6. Select **release** build variant
7. Check **V1 (Jar Signature)** and **V2 (Full APK Signature)**
8. Click **Finish**

The signed APK will be at: `app\release\app-release.apk`

**IMPORTANT:** Keep your keystore file and password safe! You'll need them for future updates.

---

## Troubleshooting

### "Gradle sync failed"
- Check internet connection
- Try: File → Invalidate Caches / Restart
- Ensure Android SDK is installed

### "SDK not found"
- Install Android SDK via Android Studio
- Go to: Tools → SDK Manager
- Install Android 14.0 (API 34)

### Build errors
- Make sure you have Java JDK installed
- Check that JAVA_HOME environment variable is set
- Try: Build → Clean Project, then rebuild

### APK won't install on phone
- Enable "Install from Unknown Sources" in Settings
- Minimum Android version: 7.0 (API 24)
- Try uninstalling any previous version first

---

## What You'll Get

**Debug APK:**
- File: `app-debug.apk`
- Size: ~5-10 MB
- Suitable for: Testing, development
- Signed with: Debug certificate

**Release APK:**
- File: `app-release.apk`  
- Size: ~3-5 MB (optimized)
- Suitable for: Distribution, production
- Signed with: Your keystore

---

## Next Steps After Installation

1. Open the app on your device
2. Choose game mode (vs AI or vs Player)
3. Select difficulty (for AI mode)
4. Choose your symbol (X or O)
5. Start playing!

The app will save your statistics and settings automatically.

---

## Need Help?

- See `README.md` for app features and details
- See `BUILD_INSTRUCTIONS.md` for more build options
- Check Android Studio's Build output for error details
