package com.tictactoe.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tictactoe.domain.Difficulty
import com.tictactoe.domain.GameMode
import com.tictactoe.domain.Player
import com.tictactoe.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showPlayerSelectionDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tic-Tac-Toe") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Tic-Tac-Toe",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Choose your game mode",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Statistics card
            val stats = remember { viewModel.getStatistics() }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Games", stats["total"] ?: 0)
                        StatItem("X Wins", stats["winsX"] ?: 0)
                        StatItem("O Wins", stats["winsO"] ?: 0)
                        StatItem("Draws", stats["draws"] ?: 0)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Game mode buttons
            Button(
                onClick = {
                    showDifficultyDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Play vs AI", style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = {
                    viewModel.setGameMode(GameMode.TWO_PLAYER)
                    onStartGame()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.People, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Play vs Player", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
    
    // Difficulty selection dialog
    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            onDismiss = { showDifficultyDialog = false },
            onDifficultySelected = { difficulty ->
                viewModel.setDifficulty(difficulty)
                viewModel.setGameMode(GameMode.SINGLE_PLAYER)
                showDifficultyDialog = false
                showPlayerSelectionDialog = true
            }
        )
    }
    
    // Player selection dialog
    if (showPlayerSelectionDialog) {
        PlayerSelectionDialog(
            onDismiss = { showPlayerSelectionDialog = false },
            onPlayerSelected = { player ->
                viewModel.setAiPlayer(if (player == Player.X) Player.O else Player.X)
                showPlayerSelectionDialog = false
                onStartGame()
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DifficultySelectionDialog(
    onDismiss: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Speed, contentDescription = null)
        },
        title = {
            Text("Select Difficulty")
        },
        text = {
            Column {
                DifficultyOption(
                    difficulty = Difficulty.EASY,
                    description = "AI makes occasional mistakes",
                    onClick = { onDifficultySelected(Difficulty.EASY) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DifficultyOption(
                    difficulty = Difficulty.MEDIUM,
                    description = "Challenging but beatable",
                    onClick = { onDifficultySelected(Difficulty.MEDIUM) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DifficultyOption(
                    difficulty = Difficulty.HARD,
                    description = "Nearly unbeatable",
                    onClick = { onDifficultySelected(Difficulty.HARD) }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DifficultyOption(
    difficulty: Difficulty,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = difficulty.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun PlayerSelectionDialog(
    onDismiss: () -> Unit,
    onPlayerSelected: (Player) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Person, contentDescription = null)
        },
        title = {
            Text("Choose Your Symbol")
        },
        text = {
            Column {
                Text("Select which symbol you want to play as:")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { onPlayerSelected(Player.X) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("X", style = MaterialTheme.typography.headlineMedium)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = { onPlayerSelected(Player.O) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("O", style = MaterialTheme.typography.headlineMedium)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note: X always goes first",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
