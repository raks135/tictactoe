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
import com.tictactoe.domain.games.NumberGameEngine
import com.tictactoe.domain.games.NumberQuestion
import com.tictactoe.ui.components.CharacterFeedback
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay

@Composable
fun NumberSequenceGameScreen(
    onBack: () -> Unit
) {
    val gameEngine = remember { NumberGameEngine(maxNumber = 20) }
    var currentQuestion by remember { mutableStateOf(gameEngine.generateQuestion()) }
    var score by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf<Boolean?>(null) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        GameHelpDialog(
            title = "Number Sequence",
            lines = listOf(
                "Read the question (before/after/fill the gap).",
                "Tap the correct number from the options.",
                "Try to get the highest score."
            ),
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
                            "Number Sequence",
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
                    containerColor = Color(0xFF10B981)
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
                            Color(0xFF10B981),
                            Color(0xFF059669)
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
                QuestionCard(
                    question = currentQuestion,
                    showFeedback = showFeedback
                )
                
                // Answer Options
                AnswerGrid(
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
fun QuestionCard(
    question: NumberQuestion,
    showFeedback: Boolean?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
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
                text = question.displayText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AnswerGrid(
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
                    AnswerButton(
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
fun AnswerButton(
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
fun FeedbackMessage(isCorrect: Boolean) {
    val message = if (isCorrect) "Great job! ⭐" else "Try again! 💪"
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
