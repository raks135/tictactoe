package com.tictactoe.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.domain.games.MathGameEngine
import com.tictactoe.domain.games.MathQuestion
import com.tictactoe.ui.components.CharacterFeedback
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay

@Composable
fun AdditionGameScreen(
    onBack: () -> Unit
) {
    MathGameScreen(
        title = "Addition Practice",
        gameEngine = MathGameEngine(maxNumber = 10, isAddition = true),
        gradientColors = listOf(Color(0xFFEC4899), Color(0xFFDB2777)),
        onBack = onBack
    )
}

@Composable
fun SubtractionGameScreen(
    onBack: () -> Unit
) {
    MathGameScreen(
        title = "Subtraction Practice",
        gameEngine = MathGameEngine(maxNumber = 10, isAddition = false),
        gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
        onBack = onBack
    )
}

@Composable
private fun MathGameScreen(
    title: String,
    gameEngine: MathGameEngine,
    gradientColors: List<Color>,
    onBack: () -> Unit
) {
    var currentQuestion by remember { mutableStateOf(gameEngine.generateQuestion()) }
    var score by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf<Boolean?>(null) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        val lines = if (title.contains("Addition", ignoreCase = true)) {
            listOf(
                "Look at the numbers and the + sign.",
                "Count the dots if you need help.",
                "Tap the correct answer."
            )
        } else {
            listOf(
                "Look at the numbers and the − sign.",
                "Count the dots if you need help.",
                "Tap the correct answer."
            )
        }
        GameHelpDialog(
            title = title,
            lines = lines,
            onDismiss = { showHelp = false }
        )
    }
    
    LaunchedEffect(showFeedback) {
        if (showFeedback != null) {
            delay(1500)
            currentQuestion = gameEngine.generateQuestion()
            showFeedback = null
            selectedAnswer = null
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            title,
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
                    containerColor = gradientColors[0]
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(gradientColors)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Question Card with Visual Dots
                MathQuestionCard(
                    question = currentQuestion,
                    showFeedback = showFeedback
                )
                
                // Answer Options
                MathAnswerGrid(
                    options = currentQuestion.options,
                    selectedAnswer = selectedAnswer,
                    correctAnswer = currentQuestion.correctAnswer,
                    showFeedback = showFeedback,
                    onAnswerSelected = { answer ->
                        if (showFeedback == null) {
                            selectedAnswer = answer
                            totalQuestions++
                            val isCorrect = gameEngine.checkAnswer(currentQuestion, answer)
                            if (isCorrect) score++
                            showFeedback = isCorrect
                        }
                    }
                )
                
                // Feedback with Character
                CharacterFeedback(
                    isCorrect = showFeedback ?: false,
                    isVisible = showFeedback != null
                )
            }
        }
    }
}

@Composable
fun MathQuestionCard(
    question: MathQuestion,
    showFeedback: Boolean?
) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Number boxes equation
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First number box
                NumberBoxLarge(
                    number = question.num1,
                    color = Color(0xFFEC4899)
                )
                
                // Operator
                OperatorBox(operator = question.operator)
                
                // Second number box
                NumberBoxLarge(
                    number = question.num2,
                    color = Color(0xFF3B82F6)
                )
                
                // Equals sign
                Text(
                    text = "=",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280)
                )
                
                // Question mark box
                QuestionMarkBox()
            }
            
            // Visual representation with dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotGroup(count = question.num1, color = Color(0xFFEC4899))
                Text(
                    text = question.operator,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280)
                )
                DotGroup(count = question.num2, color = Color(0xFF3B82F6))
            }
        }
    }
}

@Composable
fun NumberBoxLarge(number: Int, color: Color) {
    Card(
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun OperatorBox(operator: String) {
    Card(
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF59E0B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = operator,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun QuestionMarkBox() {
    Card(
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF3C7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFEF3C7),
                            Color(0xFFFDE68A)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B)
            )
        }
    }
}

@Composable
fun DotGroup(count: Int, color: Color) {
    val rows = (count + 4) / 5
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(rows) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val dotsInRow = minOf(5, count - row * 5)
                repeat(dotsInRow) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun MathAnswerGrid(
    options: List<Int>,
    selectedAnswer: Int?,
    correctAnswer: Int,
    showFeedback: Boolean?,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        options.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { option ->
                    MathAnswerButton(
                        answer = option,
                        isSelected = option == selectedAnswer,
                        isCorrect = option == correctAnswer,
                        showFeedback = showFeedback,
                        onClick = { onAnswerSelected(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MathAnswerButton(
    answer: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    showFeedback: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        showFeedback == true && isCorrect -> Color(0xFF10B981)
        showFeedback == false && isSelected -> Color(0xFFEF4444)
        isSelected -> Color(0xFF3B82F6)
        else -> Color.White
    }
    
    val textColor = if (isSelected || (showFeedback != null && isCorrect)) {
        Color.White
    } else {
        Color(0xFF1F2937)
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    Card(
        modifier = modifier
            .height(100.dp)
            .scale(scale)
            .clickable(enabled = showFeedback == null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = answer.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            if (showFeedback != null && isCorrect) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correct",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
            
            if (showFeedback == false && isSelected) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Wrong",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MathFeedbackMessage(isCorrect: Boolean) {
    val messages = if (isCorrect) {
        listOf("Excellent! ⭐", "Great job! 🌟", "Perfect! ✨", "Amazing! 🎉")
    } else {
        listOf("Try again! 💪", "Keep going! 🚀", "Almost there! 💫")
    }
    val message = messages.random()
    val color = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
