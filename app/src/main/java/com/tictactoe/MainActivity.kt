package com.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tictactoe.data.GameRepository
import com.tictactoe.data.PreferencesManager
import com.tictactoe.ui.screens.GameScreen
import com.tictactoe.ui.screens.GameSelectionScreen
import com.tictactoe.ui.screens.MenuScreen
import com.tictactoe.ui.screens.SettingsScreen
import com.tictactoe.ui.screens.TicTacToeSetupScreen
import com.tictactoe.ui.screens.games.AdditionGameScreen
import com.tictactoe.ui.screens.games.NumberSequenceGameScreen
import com.tictactoe.ui.screens.games.PatternGameScreen
import com.tictactoe.ui.screens.games.SubtractionGameScreen
import com.tictactoe.ui.theme.TicTacToeTheme
import com.tictactoe.ui.utils.HapticManager
import com.tictactoe.ui.utils.SoundManager
import com.tictactoe.ui.viewmodels.GameViewModel

class MainActivity : ComponentActivity() {
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var gameRepository: GameRepository
    private lateinit var soundManager: SoundManager
    private lateinit var hapticManager: HapticManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize managers
        prefsManager = PreferencesManager(this)
        gameRepository = GameRepository(prefsManager)
        soundManager = SoundManager(this)
        hapticManager = HapticManager(this)
        
        // Apply user preferences
        soundManager.setEnabled(prefsManager.soundEnabled)
        hapticManager.setEnabled(prefsManager.hapticEnabled)
        
        setContent {
            TicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicTacToeApp(
                        prefsManager = prefsManager,
                        gameRepository = gameRepository,
                        soundManager = soundManager,
                        hapticManager = hapticManager
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun TicTacToeApp(
    prefsManager: PreferencesManager,
    gameRepository: GameRepository,
    soundManager: SoundManager,
    hapticManager: HapticManager
) {
    val navController = rememberNavController()
    
    // Create ViewModel with dependencies
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(gameRepository, prefsManager)
    )
    
    NavHost(
        navController = navController,
        startDestination = "game_selection"
    ) {
        composable("menu") {
            MenuScreen(
                viewModel = viewModel,
                onStartGame = {
                    navController.navigate("game_selection")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("game_selection") {
            GameSelectionScreen(
                onNavigateToGame = { route ->
                    when (route) {
                        "tictactoe" -> navController.navigate("tictactoe_setup")
                        else -> navController.navigate(route)
                    }
                }
            )
        }
        
        composable("tictactoe_setup") {
            TicTacToeSetupScreen(
                onStartGame = { mode, difficulty ->
                    viewModel.setGameMode(mode)
                    viewModel.setDifficulty(difficulty)
                    navController.navigate("game")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("game") {
            GameScreen(
                viewModel = viewModel,
                onNavigateToMenu = {
                    navController.popBackStack()
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("number_sequence") {
            NumberSequenceGameScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("patterns") {
            PatternGameScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("addition") {
            AdditionGameScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("subtraction") {
            SubtractionGameScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                prefsManager = prefsManager,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
