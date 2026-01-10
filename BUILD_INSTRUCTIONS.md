# Build Instructions for Tic-Tac-Toe Android App

## Option 1: Build with Android Studio (Recommended)

1. **Open the project in Android Studio**
   - Launch Android Studio
   - Click "Open" and select the `c:\tictactoe` folder
   - Wait for Gradle sync to complete (this will download all dependencies)

2. **Build the APK**
   - Go to `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - Or use the menu: `Build` → `Generate Signed Bundle / APK` for a release version
   - Wait for the build to complete

3. **Locate the APK**
   - After build completes, click "locate" in the notification
   - Or find it at: `c:\tictactoe\app\build\outputs\apk\debug\app-debug.apk`

4. **Install the APK**
   - Transfer the APK to your Android device
   - Enable "Install from Unknown Sources" in device settings
   - Open the APK file on your device to install

---

## Option 2: Build with Command Line (Requires Android SDK)

If you have Android SDK installed, you can build from command line:

### Prerequisites
- Android SDK installed
- ANDROID_HOME environment variable set
- Java JDK 8 or higher

### Build Commands

**Debug APK (for testing):**
```bash
cd c:\tictactoe
.\gradlew assembleDebug
```

**Release APK (for distribution):**
```bash
cd c:\tictactoe
.\gradlew assembleRelease
```

### Output Location
- Debug APK: `app\build\outputs\apk\debug\app-debug.apk`
- Release APK: `app\build\outputs\apk\release\app-release-unsigned.apk`

---

## Option 3: Quick Build Script

I've created a build script for you. To use it:

1. Make sure Android Studio is installed
2. Run the build script:
   ```bash
   .\build-apk.bat
   ```

This will automatically:
- Find Android Studio's Gradle
- Build the debug APK
- Show you where the APK is located

---

## Troubleshooting

### Gradle Sync Issues
- Make sure you have internet connection (to download dependencies)
- Try `File` → `Invalidate Caches / Restart` in Android Studio
- Check that Android SDK is properly installed

### Build Errors
- Ensure you have Android SDK Platform 34 installed
- Check that build tools are installed
- Verify Java JDK is installed and JAVA_HOME is set

### APK Installation Issues
- Enable "Install from Unknown Sources" on your device
- Make sure the device is running Android 7.0 (API 24) or higher
- Try uninstalling any previous version first

---

## APK Signing (For Release)

For a production release, you should sign the APK:

1. In Android Studio: `Build` → `Generate Signed Bundle / APK`
2. Select `APK`
3. Create a new keystore or use an existing one
4. Fill in the keystore details
5. Select `release` build variant
6. Click `Finish`

The signed APK will be in `app\release\app-release.apk`

---

## Current Status

The project is ready to build! Just open it in Android Studio and follow Option 1 above.

**Project Location:** `c:\tictactoe`
**Expected APK Size:** ~5-10 MB (debug), ~3-5 MB (release)
**Minimum Android Version:** Android 7.0 (API 24)
**Target Android Version:** Android 14 (API 34)
