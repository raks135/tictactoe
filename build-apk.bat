@echo off
echo ========================================
echo Tic-Tac-Toe APK Build Script
echo ========================================
echo.

echo This project requires Android Studio to build.
echo.
echo QUICK START:
echo 1. Open Android Studio
echo 2. Click "Open" and select: c:\tictactoe
echo 3. Wait for Gradle sync to complete
echo 4. Go to: Build -^> Build Bundle(s) / APK(s) -^> Build APK(s)
echo 5. After build completes, find APK at:
echo    c:\tictactoe\app\build\outputs\apk\debug\app-debug.apk
echo.
echo See BUILD_INSTRUCTIONS.md for detailed steps.
echo.
pause
    ) else (
        echo.
        echo ========================================
        echo BUILD FAILED!
        echo ========================================
        echo.
        echo Please open the project in Android Studio and build from there.
        echo See BUILD_INSTRUCTIONS.md for detailed steps.
        echo.
        pause
    )
) else (
    echo Android Studio Gradle not found at expected location.
    echo.
    echo Please use one of these methods:
    echo.
    echo 1. Open the project in Android Studio
    echo    - File ^> Open ^> Select c:\tictactoe
    echo    - Build ^> Build Bundle(s) / APK(s) ^> Build APK(s)
    echo.
    echo 2. Install Gradle and run:
    echo    gradle assembleDebug
    echo.
    echo See BUILD_INSTRUCTIONS.md for detailed steps.
    echo.
    pause
)
