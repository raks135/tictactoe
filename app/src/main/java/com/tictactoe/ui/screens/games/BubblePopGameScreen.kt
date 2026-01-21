package com.tictactoe.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Bubble(
    val id: Long,
    val xPercent: Float, // 0.0 to 1.0
    val size: Float, // dp
    val color: Color,
    val speed: Float, // duration multiplier
    var isPopped: Boolean = false
)

@Composable
fun BubblePopGameScreen(
    onBack: () -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var bubbles by remember { mutableStateOf(listOf<Bubble>()) }
    var nextBubbleId by remember { mutableStateOf(0L) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        GameHelpDialog(
            title = "Bubble Pop",
            lines = listOf(
                "Tap bubbles to pop them.",
                "Each pop adds +1 to your score.",
                "Try to pop as many as you can."
            ),
            onDismiss = { showHelp = false }
        )
    }
    
    // Game loop for spawning bubbles
    LaunchedEffect(Unit) {
        while (true) {
            delay(800) // Spawn rate
            val newBubble = Bubble(
                id = nextBubbleId++,
                xPercent = Random.nextFloat() * 0.8f + 0.1f, // Keep away from extreme edges
                size = Random.nextFloat() * 40f + 40f, // 40-80dp
                color = listOf(
                    Color(0xFFFF5252), Color(0xFF448AFF), Color(0xFF69F0AE),
                    Color(0xFFFFD740), Color(0xFFE040FB)
                ).random(),
                speed = Random.nextFloat() * 0.5f + 0.8f // 0.8 - 1.3 speed factor
            )
            bubbles = bubbles + newBubble
            
            // Cleanup old bubbles (simple check, in a real game we'd remove when off screen)
            if (bubbles.size > 15) {
                bubbles = bubbles.drop(1)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Bubble Pop",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Score: $score",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    containerColor = Color(0xFF4FC3F7)
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
                        colors = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1))
                    )
                )
        ) {
            // Render bubbles
            bubbles.forEach { bubble ->
                if (!bubble.isPopped) {
                    key(bubble.id) {
                        BubbleItem(
                            bubble = bubble,
                            onPop = {
                                score++
                                bubbles = bubbles.map { if (it.id == bubble.id) it.copy(isPopped = true) else it }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BubbleItem(
    bubble: Bubble,
    onPop: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    // Animate Y position from bottom to top
    val infiniteTransition = rememberInfiniteTransition(label = "bubble")
    val yProgress by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = -0.2f, // Go slightly above screen
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (4000 * bubble.speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "yPos"
    )

    // Calculate actual offset
    val yOffset = screenHeight * yProgress
    
    Box(
        modifier = Modifier
            .offset(
                x = (configuration.screenWidthDp.dp * bubble.xPercent) - (bubble.size.dp / 2),
                y = yOffset
            )
            .size(bubble.size.dp)
            .background(bubble.color.copy(alpha = 0.8f), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onPop() }
    ) {
        // Shine effect
        Box(
            modifier = Modifier
                .offset(x = bubble.size.dp * 0.2f, y = bubble.size.dp * 0.2f)
                .size(bubble.size.dp * 0.25f)
                .background(Color.White.copy(alpha = 0.6f), CircleShape)
        )
    }
}
