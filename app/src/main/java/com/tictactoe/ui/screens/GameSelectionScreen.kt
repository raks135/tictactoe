package com.tictactoe.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
                description = "Strategy game",
                icon = Icons.Default.Close,
                gradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                route = "tictactoe"
            ),
            GameCard(
                title = "Memory Match",
                description = "Find the pairs",
                icon = Icons.Default.Star,
                gradient = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
                route = "memory_match"
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
                description = "Complete sequence",
                icon = Icons.Default.LinearScale,
                gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                route = "patterns"
            ),
            GameCard(
                title = "Bubble Pop",
                description = "Pop bubbles!",
                icon = Icons.Default.BubbleChart,
                gradient = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)),
                route = "bubble_pop"
            ),
            GameCard(
                title = "Addition",
                description = "Add numbers",
                icon = Icons.Default.Add,
                gradient = listOf(Color(0xFFEC4899), Color(0xFFDB2777)),
                route = "addition"
            ),
            GameCard(
                title = "Subtraction",
                description = "Subtract numbers",
                icon = Icons.Default.Remove,
                gradient = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
                route = "subtraction"
            ),
            GameCard(
                title = "Color Matching",
                description = "Learn colors",
                icon = Icons.Default.Palette,
                gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                route = "colors"
            ),
            GameCard(
                title = "Shape Recognition",
                description = "Identify shapes",
                icon = Icons.Default.Category,
                gradient = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)),
                route = "shapes"
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games) { game ->
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
            .height(140.dp)
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
                    Brush.verticalGradient(game.gradient)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = game.icon,
                    contentDescription = game.title,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = game.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = game.description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
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
