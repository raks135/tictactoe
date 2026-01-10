# Tic-Tac-Toe Android App

A production-ready Tic-Tac-Toe mobile application built with modern Android development practices.

## Features

### Game Modes
- **VS AI**: Play against an intelligent AI opponent with three difficulty levels
- **VS Player**: Local two-player mode

### AI Opponent
- **Easy**: AI makes occasional mistakes for a casual experience
- **Medium**: Challenging gameplay with limited search depth
- **Hard**: Nearly unbeatable using full minimax algorithm with alpha-beta pruning
- **Performance**: AI responds in under 100ms

### User Experience
- Modern Material Design 3 UI
- Smooth animations for moves and win states
- Dark and light theme support (system-based)
- Haptic feedback for tactile responses
- Sound effects for game events
- Intuitive touch controls

### Features
- Undo move functionality
- Game statistics tracking (wins, losses, draws)
- Persistent settings and statistics
- Responsive design for all screen sizes

## Technical Stack

### Architecture
- **Clean Architecture**: Separation of concerns with domain, data, and UI layers
- **MVVM Pattern**: ViewModel for state management
- **Jetpack Compose**: Modern declarative UI framework

### Technologies
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **State Management**: StateFlow and Compose State
- **Persistence**: SharedPreferences
- **Navigation**: Jetpack Navigation Compose
- **Testing**: JUnit for unit tests

### Key Components

#### Domain Layer
- `GameState`: Immutable game state representation
- `GameEngine`: Core game logic and rules
- `AIPlayer`: Minimax algorithm with alpha-beta pruning

#### Data Layer
- `PreferencesManager`: Settings and preferences persistence
- `GameRepository`: Statistics management

#### UI Layer
- `GameViewModel`: State management and business logic coordination
- `GameScreen`: Main game interface
- `MenuScreen`: Game mode selection
- `SettingsScreen`: User preferences
- `GameBoard`: Animated game board component

## Building the App

### Prerequisites
- Android Studio Hedgehog or later
- JDK 8 or higher
- Android SDK 24+ (Android 7.0+)

### Build Instructions

1. Clone or open the project in Android Studio
2. Sync Gradle files
3. Build the project:
   ```bash
   ./gradlew build
   ```
4. Run tests:
   ```bash
   ./gradlew test
   ```
5. Install on device/emulator:
   ```bash
   ./gradlew installDebug
   ```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/tictactoe/
│   │   │   ├── data/           # Data layer
│   │   │   │   ├── PreferencesManager.kt
│   │   │   │   └── GameRepository.kt
│   │   │   ├── domain/         # Business logic
│   │   │   │   ├── GameState.kt
│   │   │   │   ├── GameEngine.kt
│   │   │   │   └── AIPlayer.kt
│   │   │   ├── ui/             # Presentation layer
│   │   │   │   ├── components/
│   │   │   │   ├── screens/
│   │   │   │   ├── theme/
│   │   │   │   ├── utils/
│   │   │   │   └── viewmodels/
│   │   │   └── MainActivity.kt
│   │   ├── res/                # Resources
│   │   └── AndroidManifest.xml
│   └── test/                   # Unit tests
└── build.gradle.kts
```

## Testing

The app includes comprehensive unit tests:

### GameEngine Tests
- Move validation
- Win detection (rows, columns, diagonals)
- Draw detection
- Undo functionality
- Board state management

### AI Player Tests
- Blocking opponent winning moves
- Taking winning moves
- Performance validation (<100ms)
- Difficulty level variations
- Valid move generation

Run tests with:
```bash
./gradlew test
```

## Design Principles

### User Experience
- **Instant Feedback**: All interactions provide immediate visual and haptic feedback
- **Smooth Animations**: Spring-based animations for natural feel
- **Clear State**: Always show current player and game status
- **Error Prevention**: Invalid moves are prevented, not just rejected

### Code Quality
- **Clean Architecture**: Clear separation of concerns
- **Immutability**: Game state is immutable for predictability
- **Testability**: Core logic is fully unit tested
- **Performance**: AI guaranteed to respond in <100ms

## Future Enhancements

Potential improvements for future versions:
- Online multiplayer
- Game replay functionality
- Custom board sizes (4x4, 5x5)
- Tournament mode
- Achievement system
- Cloud statistics sync

## License

This is a demonstration project for portfolio purposes.

## Version

**1.0** - Initial release
