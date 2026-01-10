package com.tictactoe.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

data class GameCard(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val route: String
)

@Composable
fun GameSelectionScreen(
    onNavigateToGame: (String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val games = remember {
        listOf(
            GameCard(
                title = "Tic-Tac-Toe",
                description = "Classic game for two players",
                icon = Icons.Default.Close,
                gradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                route = "tictactoe"
            ),
            GameCard(
                title = "Number Sequence",
                description = "What comes next?",
                icon = Icons.Default.List,
                gradient = listOf(Color(0xFF10B981), Color(0xFF059669)),
                route = "number_sequence"
            ),
            GameCard(
                title = "Number Patterns",
                description = "Find the pattern!",
                icon = Icons.Default.Star,
                gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                route = "patterns"
            ),
            GameCard(
                title = "Addition",
                description = "Practice adding numbers",
                icon = Icons.Default.Add,
                gradient = listOf(Color(0xFFEC4899), Color(0xFFDB2777)),
                route = "addition"
            ),
            GameCard(
                title = "Subtraction",
                description = "Practice subtracting",
                icon = Icons.Default.Remove,
                gradient = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
                route = "subtraction"
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Kids Learning Games",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            games.forEach { game ->
                GameCardItem(
                    game = game,
                    onClick = { onNavigateToGame(game.route) }
                )
            }
        }
    }
}

@Composable
fun GameCardItem(
    game: GameCard,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(game.gradient)
                )
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
                        text = game.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.description,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Icon(
                    imageVector = game.icon,
                    contentDescription = game.title,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}
