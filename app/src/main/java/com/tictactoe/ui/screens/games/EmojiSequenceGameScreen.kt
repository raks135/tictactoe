package com.tictactoe.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.data.PreferencesManager
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class EmojiPad(
    val id: Int,
    val emoji: String,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiSequenceGameScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }

    val pads = remember {
        listOf(
            EmojiPad(0, "🐶", "Dog"),
            EmojiPad(1, "🐱", "Cat"),
            EmojiPad(2, "🐼", "Panda"),
            EmojiPad(3, "🦊", "Fox"),
            EmojiPad(4, "🐸", "Frog"),
            EmojiPad(5, "🦁", "Lion")
        )
    }

    var bestLevel by remember { mutableIntStateOf(prefs.emojiBestLevel) }
    var level by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isShowingSequence by remember { mutableStateOf(false) }
    var highlightedPadId by remember { mutableStateOf<Int?>(null) }
    var isGameOver by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }

    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var userIndex by remember { mutableIntStateOf(0) }

    fun resetGame() {
        level = 0
        score = 0
        isGameOver = false
        userIndex = 0
        sequence = emptyList()
        highlightedPadId = null
    }

    fun startNextRound() {
        val next = Random.nextInt(0, pads.size)
        sequence = sequence + next
        level = sequence.size
        userIndex = 0
    }

    LaunchedEffect(Unit) {
        resetGame()
        delay(250)
        startNextRound()
    }

    LaunchedEffect(level, isGameOver) {
        if (isGameOver) return@LaunchedEffect
        if (sequence.isEmpty()) return@LaunchedEffect

        isShowingSequence = true
        delay(350)
        for (id in sequence) {
            highlightedPadId = id
            delay(420)
            highlightedPadId = null
            delay(140)
        }
        isShowingSequence = false
    }

    if (showHelp) {
        GameHelpDialog(
            title = "Emoji Sequence",
            lines = listOf(
                "Watch the emoji sequence.",
                "Tap the emojis in the same order.",
                "Each round adds one more emoji.",
                "Try to beat your Best Level."
            ),
            onDismiss = { showHelp = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Emoji Sequence", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            "Level: $level   Best: $bestLevel",
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
                    IconButton(onClick = {
                        resetGame()
                        startNextRound()
                    }) {
                        Icon(Icons.Default.Refresh, "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF06B6D4)
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
                        colors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isGameOver) "Oops! Try again" else if (isShowingSequence) "Watch" else "Your turn",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = "Score: $score",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    pads.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { pad ->
                                EmojiPadButton(
                                    pad = pad,
                                    isHighlighted = highlightedPadId == pad.id,
                                    enabled = !isShowingSequence && !isGameOver,
                                    onClick = {
                                        val expected = sequence.getOrNull(userIndex)
                                        if (expected == pad.id) {
                                            score += 1
                                            userIndex += 1
                                            if (userIndex >= sequence.size) {
                                                delay(250)
                                                startNextRound()
                                            }
                                        } else {
                                            isGameOver = true
                                            if (level > bestLevel) {
                                                bestLevel = level
                                                prefs.emojiBestLevel = level
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                if (isGameOver) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            resetGame()
                            startNextRound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Play Again")
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun EmojiPadButton(
    pad: EmojiPad,
    isHighlighted: Boolean,
    enabled: Boolean,
    onClick: suspend () -> Unit
) {
    val base = Color.White
    val highlight = Color(0xFFFFF9C4)
    val container by animateColorAsState(
        targetValue = if (isHighlighted) highlight else base,
        label = "emojiPad"
    )

    var isPressed by remember { mutableStateOf(false) }
    val pressedColor by animateColorAsState(
        targetValue = if (isPressed) highlight else container,
        label = "emojiPressed"
    )

    Card(
        modifier = Modifier
            .size(110.dp)
            .clickable(enabled = enabled) { isPressed = true },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = pressedColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = pad.emoji, fontSize = 34.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = pad.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            onClick()
            delay(120)
            isPressed = false
        }
    }
}

