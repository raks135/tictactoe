package com.tictactoe.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tictactoe.domain.GameStatus
import com.tictactoe.domain.Player
import com.tictactoe.ui.components.GameBoard
import com.tictactoe.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToMenu: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val showGameOverDialog by viewModel.showGameOverDialog.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tic-Tac-Toe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToMenu) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to menu")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Score display
            ScoreCard(viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Current player indicator
            CurrentPlayerIndicator(
                currentPlayer = gameState.currentPlayer,
                isAiThinking = isAiThinking,
                gameStatus = gameState.status
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Game board
            GameBoard(
                gameState = gameState,
                onCellClick = { row, col ->
                    viewModel.onCellClick(row, col)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { viewModel.undoMove() },
                    enabled = gameState.moveHistory.isNotEmpty() && !isAiThinking
                ) {
                    Icon(Icons.Default.Undo, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Undo")
                }
                
                Button(
                    onClick = { viewModel.newGame() },
                    enabled = !isAiThinking
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Game")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Game over dialog
    if (showGameOverDialog) {
        GameOverDialog(
            gameStatus = gameState.status,
            onDismiss = { viewModel.dismissGameOverDialog() },
            onNewGame = { viewModel.newGame() }
        )
    }
}

@Composable
fun ScoreCard(viewModel: GameViewModel) {
    val stats = remember { viewModel.getStatistics() }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreItem(label = "X Wins", value = stats["winsX"] ?: 0)
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
            )
            ScoreItem(label = "Draws", value = stats["draws"] ?: 0)
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
            )
            ScoreItem(label = "O Wins", value = stats["winsO"] ?: 0)
        }
    }
}

@Composable
fun ScoreItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CurrentPlayerIndicator(
    currentPlayer: Player,
    isAiThinking: Boolean,
    gameStatus: GameStatus
) {
    AnimatedVisibility(
        visible = gameStatus == GameStatus.IN_PROGRESS,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isAiThinking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "AI is thinking...",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = "Current Player: ${currentPlayer.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun GameOverDialog(
    gameStatus: GameStatus,
    onDismiss: () -> Unit,
    onNewGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = when (gameStatus) {
                    GameStatus.X_WON, GameStatus.O_WON -> Icons.Default.EmojiEvents
                    GameStatus.DRAW -> Icons.Default.Handshake
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = when (gameStatus) {
                    GameStatus.X_WON -> "Player X Wins!"
                    GameStatus.O_WON -> "Player O Wins!"
                    GameStatus.DRAW -> "It's a Draw!"
                    else -> "Game Over"
                },
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = when (gameStatus) {
                    GameStatus.X_WON -> "Congratulations! X is the winner!"
                    GameStatus.O_WON -> "Congratulations! O is the winner!"
                    GameStatus.DRAW -> "Well played! The game ended in a draw."
                    else -> ""
                }
            )
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                onNewGame()
            }) {
                Text("New Game")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
