package com.tictactoe.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.ui.components.CharacterFeedback
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ShapeQuestion(
    val targetShape: GameShape,
    val options: List<GameShape>
)

enum class GameShape(val displayName: String) {
    CIRCLE("Circle"),
    SQUARE("Square"),
    TRIANGLE("Triangle"),
    STAR("Star"),
    HEART("Heart")
}

@Composable
fun ShapeGameScreen(
    onBack: () -> Unit
) {
    var currentQuestion by remember { mutableStateOf(generateShapeQuestion()) }
    var score by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf<Boolean?>(null) }
    var selectedShape by remember { mutableStateOf<GameShape?>(null) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        GameHelpDialog(
            title = "Shape Recognition",
            lines = listOf(
                "Look at the shape shown on the card.",
                "Tap the same shape from the options.",
                "Try to get the highest score."
            ),
            onDismiss = { showHelp = false }
        )
    }
    
    LaunchedEffect(showFeedback) {
        if (showFeedback != null) {
            delay(1500)
            currentQuestion = generateShapeQuestion()
            showFeedback = null
            selectedShape = null
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Shape Recognition",
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
                    containerColor = Color(0xFF8B5CF6)
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
                            Color(0xFF8B5CF6),
                            Color(0xFF7C3AED)
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
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Find the shape:",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Target shape
                        ShapeDrawing(
                            shape = currentQuestion.targetShape,
                            size = 120.dp,
                            color = Color(0xFF8B5CF6)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = currentQuestion.targetShape.displayName,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5CF6)
                        )
                    }
                }
                
                // Shape Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    currentQuestion.options.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            row.forEach { shape ->
                                ShapeOptionButton(
                                    shape = shape,
                                    isSelected = shape == selectedShape,
                                    isCorrect = shape == currentQuestion.targetShape,
                                    showFeedback = showFeedback,
                                    onClick = {
                                        if (showFeedback == null) {
                                            selectedShape = shape
                                            totalQuestions++
                                            val isCorrect = shape == currentQuestion.targetShape
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
fun ShapeDrawing(
    shape: GameShape,
    size: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Canvas(modifier = Modifier.size(size)) {
        val canvasSize = this.size
        when (shape) {
            GameShape.CIRCLE -> {
                drawCircle(
                    color = color,
                    radius = canvasSize.minDimension / 2,
                    style = Stroke(width = 8f)
                )
            }
            GameShape.SQUARE -> {
                drawRect(
                    color = color,
                    size = Size(canvasSize.width, canvasSize.height),
                    style = Stroke(width = 8f)
                )
            }
            GameShape.TRIANGLE -> {
                val path = Path().apply {
                    moveTo(canvasSize.width / 2, 0f)
                    lineTo(canvasSize.width, canvasSize.height)
                    lineTo(0f, canvasSize.height)
                    close()
                }
                drawPath(path, color, style = Stroke(width = 8f))
            }
            GameShape.STAR -> {
                val path = createStarPath(canvasSize)
                drawPath(path, color, style = Stroke(width = 8f))
            }
            GameShape.HEART -> {
                val path = createHeartPath(canvasSize)
                drawPath(path, color, style = Stroke(width = 8f))
            }
        }
    }
}

private fun createStarPath(size: Size): Path {
    val path = Path()
    val centerX = size.width / 2
    val centerY = size.height / 2
    val outerRadius = size.minDimension / 2
    val innerRadius = outerRadius * 0.4f
    
    for (i in 0 until 10) {
        val angle = (i * 36 - 90) * Math.PI / 180
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = (centerX + radius * Math.cos(angle)).toFloat()
        val y = (centerY + radius * Math.sin(angle)).toFloat()
        
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun createHeartPath(size: Size): Path {
    val path = Path()
    val width = size.width
    val height = size.height
    
    path.moveTo(width / 2, height * 0.3f)
    path.cubicTo(
        width * 0.2f, 0f,
        0f, height * 0.3f,
        width / 2, height
    )
    path.moveTo(width / 2, height * 0.3f)
    path.cubicTo(
        width * 0.8f, 0f,
        width, height * 0.3f,
        width / 2, height
    )
    return path
}

@Composable
fun ShapeOptionButton(
    shape: GameShape,
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
    
    val backgroundColor = when {
        showFeedback == true && isCorrect -> Color(0xFF10B981)
        showFeedback == false && isSelected -> Color(0xFFEF4444)
        isSelected -> Color(0xFF3B82F6)
        else -> Color.White
    }
    
    Card(
        modifier = modifier
            .height(120.dp)
            .scale(scale)
            .clickable(enabled = showFeedback == null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ShapeDrawing(
                shape = shape,
                size = 60.dp,
                color = if (isSelected || (showFeedback != null && isCorrect)) 
                    Color.White 
                else 
                    Color(0xFF8B5CF6)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = shape.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected || (showFeedback != null && isCorrect)) 
                    Color.White 
                else 
                    Color(0xFF1F2937)
            )
        }
    }
}

private fun generateShapeQuestion(): ShapeQuestion {
    val allShapes = GameShape.values().toList()
    val targetShape = allShapes.random()
    
    val otherShapes = allShapes.filter { it != targetShape }.shuffled().take(3)
    val options = (listOf(targetShape) + otherShapes).shuffled()
    
    return ShapeQuestion(targetShape, options)
}
