package com.tictactoe.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.ui.components.CharacterFeedback
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ColorQuestion(
    val targetColor: GameColor,
    val options: List<GameColor>
)

enum class GameColor(val displayName: String, val color: Color) {
    RED("Red", Color(0xFFEF4444)),
    BLUE("Blue", Color(0xFF3B82F6)),
    GREEN("Green", Color(0xFF10B981)),
    YELLOW("Yellow", Color(0xFFFBBF24)),
    PURPLE("Purple", Color(0xFF8B5CF6)),
    ORANGE("Orange", Color(0xFFF97316)),
    PINK("Pink", Color(0xFFEC4899))
}

@Composable
fun ColorMatchingGameScreen(
    onBack: () -> Unit
) {
    var currentQuestion by remember { mutableStateOf(generateColorQuestion()) }
    var score by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf<Boolean?>(null) }
    var selectedColor by remember { mutableStateOf<GameColor?>(null) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        GameHelpDialog(
            title = "Color Matching",
            lines = listOf(
                "Look at the color shown in the big circle.",
                "Tap the matching color from the options.",
                "Try to get the highest score."
            ),
            onDismiss = { showHelp = false }
        )
    }
    
    LaunchedEffect(showFeedback) {
        if (showFeedback != null) {
            delay(1500)
            currentQuestion = generateColorQuestion()
            showFeedback = null
            selectedColor = null
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Color Matching",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (totalQuestions > 0) {
                            Text(
                                "Score: $score / $totalQuestions",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                    containerColor = Color(0xFFEC4899)
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
                            Color(0xFFEC4899),
                            Color(0xFFDB2777)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Question Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Find the color:",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Target color circle
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    currentQuestion.targetColor.color,
                                    CircleShape
                                )
                                .border(4.dp, Color.White, CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = currentQuestion.targetColor.displayName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentQuestion.targetColor.color
                        )
                    }
                }
                
                // Color Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    currentQuestion.options.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            row.forEach { color ->
                                ColorOptionButton(
                                    color = color,
                                    isSelected = color == selectedColor,
                                    isCorrect = color == currentQuestion.targetColor,
                                    showFeedback = showFeedback,
                                    onClick = {
                                        if (showFeedback == null) {
                                            selectedColor = color
                                            totalQuestions++
                                            val isCorrect = color == currentQuestion.targetColor
                                            if (isCorrect) score++
                                            showFeedback = isCorrect
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                
                // Feedback
                CharacterFeedback(
                    isCorrect = showFeedback ?: false,
                    isVisible = showFeedback != null
                )
            }
        }
    }
}

@Composable
fun ColorOptionButton(
    color: GameColor,
    isSelected: Boolean,
    isCorrect: Boolean,
    showFeedback: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val borderColor = when {
        showFeedback == true && isCorrect -> Color(0xFF10B981)
        showFeedback == false && isSelected -> Color(0xFFEF4444)
        isSelected -> Color.White
        else -> Color.White.copy(alpha = 0.5f)
    }
    
    Box(
        modifier = modifier
            .height(100.dp)
            .scale(scale)
            .background(color.color, RoundedCornerShape(16.dp))
            .border(4.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = showFeedback == null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = color.displayName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

private fun generateColorQuestion(): ColorQuestion {
    val allColors = GameColor.values().toList()
    val targetColor = allColors.random()
    
    // Get 3 other random colors
    val otherColors = allColors.filter { it != targetColor }.shuffled().take(3)
    val options = (listOf(targetColor) + otherColors).shuffled()
    
    return ColorQuestion(targetColor, options)
}
