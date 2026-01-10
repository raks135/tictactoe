package com.tictactoe.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class FeedbackCharacter(val emoji: String, val displayName: String) {
    HULK("💪", "Hulk"),
    SPIDERMAN("🕷️", "Spider-Man"),
    KINGKONG("🦍", "King Kong"),
    SUPERHERO("🦸", "Hero"),
    STAR("⭐", "Star")
}

data class FeedbackMessage(
    val character: FeedbackCharacter,
    val message: String,
    val isCorrect: Boolean
)

@Composable
fun CharacterFeedback(
    isCorrect: Boolean,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val feedbackData = remember(isCorrect) {
        if (isCorrect) {
            getCorrectFeedback()
        } else {
            getIncorrectFeedback()
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        CharacterCard(feedbackData = feedbackData)
    }
}

@Composable
private fun CharacterCard(feedbackData: FeedbackMessage) {
    var bounce by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        while (true) {
            bounce = true
            delay(500)
            bounce = false
            delay(500)
        }
    }
    
    val scale by animateFloatAsState(
        targetValue = if (bounce) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce"
    )
    
    val rotation by rememberInfiniteTransition(label = "rotation").animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = if (feedbackData.isCorrect) 
                Color(0xFF10B981) 
            else 
                Color(0xFFF59E0B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Character Emoji
            Text(
                text = feedbackData.character.emoji,
                fontSize = 80.sp,
                modifier = Modifier
                    .scale(scale)
                    .rotate(if (feedbackData.isCorrect) rotation else 0f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Message
            Text(
                text = feedbackData.message,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            // Character name
            if (feedbackData.isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "- ${feedbackData.character.displayName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun getCorrectFeedback(): FeedbackMessage {
    val characters = listOf(
        FeedbackCharacter.HULK,
        FeedbackCharacter.SPIDERMAN,
        FeedbackCharacter.KINGKONG,
        FeedbackCharacter.SUPERHERO
    )
    
    val messages = listOf(
        "SMASH! Great job!",
        "Amazing work!",
        "Spectacular!",
        "You're a hero!",
        "Incredible!",
        "Fantastic!",
        "Awesome!",
        "Well done!",
        "Perfect!",
        "Excellent!"
    )
    
    return FeedbackMessage(
        character = characters.random(),
        message = messages.random(),
        isCorrect = true
    )
}

private fun getIncorrectFeedback(): FeedbackMessage {
    val messages = listOf(
        "Try again, hero!",
        "Keep going!",
        "You can do it!",
        "Almost there!",
        "Don't give up!",
        "One more try!"
    )
    
    return FeedbackMessage(
        character = FeedbackCharacter.STAR,
        message = messages.random(),
        isCorrect = false
    )
}
