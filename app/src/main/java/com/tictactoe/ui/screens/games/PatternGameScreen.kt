package com.tictactoe.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.tictactoe.ui.components.CharacterFeedback
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay
import kotlin.random.Random

data class PatternQuestion(
    val sequence: List<Int>,
    val correctAnswer: Int,
    val options: List<Int>
)

@Composable
fun PatternGameScreen(
    onBack: () -> Unit
) {
    var currentQuestion by remember { mutableStateOf(generatePatternQuestion()) }
    var score by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf<Boolean?>(null) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        GameHelpDialog(
            title = "Number Patterns",
            lines = listOf(
                "Look at the number pattern.",
                "Guess which number comes next.",
                "Tap the correct answer."
            ),
            onDismiss = { showHelp = false }
        )
    }
    
    LaunchedEffect(showFeedback) {
        if (showFeedback != null) {
            delay(1500)
            currentQuestion = generatePatternQuestion()
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
                            "Number Patterns",
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
                    containerColor = Color(0xFFF59E0B)
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
                            Color(0xFFF59E0B),
                            Color(0xFFD97706)
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
                // Pattern Card
                PatternCard(
                    question = currentQuestion,
                    showFeedback = showFeedback
                )
                
                // Answer Options
                PatternAnswerGrid(
                    options = currentQuestion.options,
                    selectedAnswer = selectedAnswer,
                    correctAnswer = currentQuestion.correctAnswer,
                    showFeedback = showFeedback,
                    onAnswerSelected = { answer ->
                        if (showFeedback == null) {
                            selectedAnswer = answer
                            totalQuestions++
                            val isCorrect = answer == currentQuestion.correctAnswer
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
fun PatternCard(
    question: PatternQuestion,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "What comes next?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                question.sequence.forEach { number ->
                    NumberBox(number = number, isQuestion = false)
                }
                NumberBox(number = null, isQuestion = true)
            }
        }
    }
}

@Composable
fun NumberBox(number: Int?, isQuestion: Boolean) {
    Card(
        modifier = Modifier.size(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isQuestion) Color(0xFFFEF3C7) else Color(0xFFF59E0B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isQuestion) {
                Text(
                    text = "?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B)
                )
            } else {
                Text(
                    text = number.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PatternAnswerGrid(
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
                    PatternAnswerButton(
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
fun PatternAnswerButton(
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
fun PatternFeedbackMessage(isCorrect: Boolean) {
    val messages = if (isCorrect) {
        listOf("Brilliant! ⭐", "You got it! 🌟", "Fantastic! ✨")
    } else {
        listOf("Try again! 💪", "Keep trying! 🚀")
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

// Pattern generation logic
private fun generatePatternQuestion(): PatternQuestion {
    val patternType = Random.nextInt(3)
    
    return when (patternType) {
        0 -> generateCountingPattern()
        1 -> generateSkipCountingPattern()
        else -> generateIncreasingPattern()
    }
}

private fun generateCountingPattern(): PatternQuestion {
    val start = Random.nextInt(1, 10)
    val sequence = listOf(start, start + 1, start + 2)
    val correctAnswer = start + 3
    val options = generatePatternOptions(correctAnswer, increment = 1)
    
    return PatternQuestion(sequence, correctAnswer, options)
}

private fun generateSkipCountingPattern(): PatternQuestion {
    val skip = listOf(2, 5).random()
    val start = Random.nextInt(1, 6)
    val sequence = listOf(start, start + skip, start + skip * 2)
    val correctAnswer = start + skip * 3
    val options = generatePatternOptions(correctAnswer, increment = skip)
    
    return PatternQuestion(sequence, correctAnswer, options)
}

private fun generateIncreasingPattern(): PatternQuestion {
    val start = Random.nextInt(1, 5)
    val increment = Random.nextInt(2, 4)
    val sequence = listOf(start, start + increment, start + increment * 2)
    val correctAnswer = start + increment * 3
    val options = generatePatternOptions(correctAnswer, increment = increment)
    
    return PatternQuestion(sequence, correctAnswer, options)
}

private fun generatePatternOptions(correctAnswer: Int, increment: Int): List<Int> {
    val options = mutableListOf<Int>()
    
    // Always include the correct answer first
    options.add(correctAnswer)
    
    // Add the previous number in the pattern (one step back)
    val prevInPattern = correctAnswer - increment
    if (prevInPattern > 0 && !options.contains(prevInPattern)) {
        options.add(prevInPattern)
    }
    
    // Add the next number in the pattern (one step forward)
    val nextInPattern = correctAnswer + increment
    if (nextInPattern <= 50 && !options.contains(nextInPattern)) {
        options.add(nextInPattern)
    }
    
    // Add one number that's close but wrong (off by 1 or 2)
    val closeWrong = if (Random.nextBoolean()) correctAnswer - 1 else correctAnswer + 1
    if (closeWrong > 0 && closeWrong <= 50 && !options.contains(closeWrong)) {
        options.add(closeWrong)
    }
    
    // If we still don't have 4 options, add more nearby numbers
    var attempts = 0
    while (options.size < 4 && attempts < 10) {
        val offset = Random.nextInt(-3, 4)
        val candidate = correctAnswer + offset
        if (candidate > 0 && candidate <= 50 && !options.contains(candidate)) {
            options.add(candidate)
        }
        attempts++
    }
    
    // Ensure we have exactly 4 options
    while (options.size < 4) {
        val filler = Random.nextInt(1, 51)
        if (!options.contains(filler)) {
            options.add(filler)
        }
    }
    
    // Shuffle and return exactly 4 options
    return options.take(4).shuffled()
}


