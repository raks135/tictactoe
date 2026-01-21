package com.tictactoe.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.domain.games.MemoryCard
import com.tictactoe.domain.games.MemoryGameEngine
import com.tictactoe.ui.components.CharacterFeedback
import kotlinx.coroutines.delay

@Composable
fun MemoryGameScreen(
    onBack: () -> Unit
) {
    val gameEngine = remember { MemoryGameEngine() }
    var cards by remember { mutableStateOf(gameEngine.generateCards()) }
    var flippedIndices by remember { mutableStateOf(emptyList<Int>()) }
    var matchedPairs by remember { mutableStateOf(0) }
    var moves by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf<Boolean?>(null) }
    
    // Check for match when 2 cards are flipped
    LaunchedEffect(flippedIndices) {
        if (flippedIndices.size == 2) {
            val idx1 = flippedIndices[0]
            val idx2 = flippedIndices[1]
            
            // Wait a bit so user can see the second card
            delay(1000)
            
            if (cards[idx1].icon == cards[idx2].icon) {
                // Match!
                cards = cards.mapIndexed { index, card ->
                    if (index == idx1 || index == idx2) {
                        card.copy(isMatched = true)
                    } else card
                }
                matchedPairs++
                showFeedback = true
            } else {
                // No match, flip back
                cards = cards.mapIndexed { index, card ->
                    if (index == idx1 || index == idx2) {
                        card.copy(isFaceUp = false)
                    } else card
                }
                showFeedback = false
            }
            flippedIndices = emptyList()
            moves++
            
            // Hide feedback after delay
            delay(1000)
            showFeedback = null
        }
    }
    
    // Reset game when all pairs matched
    LaunchedEffect(matchedPairs) {
        if (matchedPairs == cards.size / 2 && matchedPairs > 0) {
            delay(2000)
            // Start new game
            cards = gameEngine.generateCards()
            matchedPairs = 0
            moves = 0
            flippedIndices = emptyList()
            showFeedback = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Memory Match",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Pairs: $matchedPairs | Moves: $moves",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6366F1)
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
                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cards.size) { index ->
                        MemoryCardItem(
                            card = cards[index],
                            onClick = {
                                if (!cards[index].isMatched && !cards[index].isFaceUp && flippedIndices.size < 2) {
                                    cards = cards.mapIndexed { i, c ->
                                        if (i == index) c.copy(isFaceUp = true) else c
                                    }
                                    flippedIndices = flippedIndices + index
                                }
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                CharacterFeedback(
                    isCorrect = showFeedback ?: false,
                    isVisible = showFeedback != null
                )
            }
        }
    }
}

@Composable
fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        animationSpec = tween(500),
        label = "rotation"
    )

    val animateScale by animateFloatAsState(
        targetValue = if (card.isMatched) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(animateScale)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(enabled = !card.isMatched && !card.isFaceUp) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isMatched) Color(0xFF10B981) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Back of card
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(32.dp)
                )
            } else {
                // Front of card
                Icon(
                    imageVector = card.icon,
                    contentDescription = null,
                    tint = if (card.isMatched) Color.White else Color(0xFF6366F1),
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            rotationY = 180f
                        }
                )
            }
        }
    }
}
