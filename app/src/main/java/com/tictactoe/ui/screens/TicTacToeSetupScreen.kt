package com.tictactoe.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.domain.GameMode
import com.tictactoe.domain.Difficulty
import com.tictactoe.ui.components.GameHelpDialog

data class ModeOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val mode: GameMode,
    val color: Color
)

data class DifficultyOption(
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val color: Color
)

@Composable
fun TicTacToeSetupScreen(
    onStartGame: (GameMode, Difficulty) -> Unit,
    onBack: () -> Unit
) {
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var showDifficultySelection by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        GameHelpDialog(
            title = "Tic-Tac-Toe",
            lines = listOf(
                "Choose Player vs Player or Player vs AI.",
                "If you choose AI, pick the difficulty.",
                "Get 3 in a row to win."
            ),
            onDismiss = { showHelp = false }
        )
    }
    
    val modeOptions = remember {
        listOf(
            ModeOption(
                title = "Player vs Player",
                description = "Play with a friend",
                icon = Icons.Default.Person,
                mode = GameMode.TWO_PLAYER,
                color = Color(0xFF10B981)
            ),
            ModeOption(
                title = "Player vs AI",
                description = "Challenge the computer",
                icon = Icons.Default.SmartToy,
                mode = GameMode.SINGLE_PLAYER,
                color = Color(0xFF6366F1)
            )
        )
    }
    
    val difficultyOptions = remember {
        listOf(
            DifficultyOption(
                title = "Easy",
                description = "Perfect for beginners",
                difficulty = Difficulty.EASY,
                color = Color(0xFF10B981)
            ),
            DifficultyOption(
                title = "Medium",
                description = "A fair challenge",
                difficulty = Difficulty.MEDIUM,
                color = Color(0xFFF59E0B)
            ),
            DifficultyOption(
                title = "Hard",
                description = "Expert level!",
                difficulty = Difficulty.HARD,
                color = Color(0xFFEF4444)
            )
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tic-Tac-Toe Setup",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showHelp = true }) {
                        Icon(Icons.Default.Info, "Info")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6366F1)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (!showDifficultySelection) {
                    // Mode Selection
                    Text(
                        text = "Choose Game Mode",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    modeOptions.forEach { option ->
                        ModeCard(
                            option = option,
                            isSelected = selectedMode == option.mode,
                            onClick = {
                                selectedMode = option.mode
                                if (option.mode == GameMode.TWO_PLAYER) {
                                    // Start immediately for 2-player
                                    onStartGame(GameMode.TWO_PLAYER, Difficulty.MEDIUM)
                                } else {
                                    // Show difficulty selection for AI
                                    showDifficultySelection = true
                                }
                            }
                        )
                    }
                } else {
                    // Difficulty Selection
                    Text(
                        text = "Choose Difficulty",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    difficultyOptions.forEach { option ->
                        DifficultyCard(
                            option = option,
                            isSelected = selectedDifficulty == option.difficulty,
                            onClick = {
                                selectedDifficulty = option.difficulty
                                onStartGame(GameMode.SINGLE_PLAYER, option.difficulty)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeCard(
    option: ModeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) option.color else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = option.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.description,
                    fontSize = 14.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF6B7280)
                )
            }
            
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                modifier = Modifier.size(48.dp),
                tint = if (isSelected) Color.White.copy(alpha = 0.8f) else option.color
            )
        }
    }
}

@Composable
fun DifficultyCard(
    option: DifficultyOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) option.color else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = option.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = option.description,
                fontSize = 14.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF6B7280)
            )
        }
    }
}
