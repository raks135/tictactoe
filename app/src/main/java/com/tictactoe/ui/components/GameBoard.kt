package com.tictactoe.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tictactoe.domain.GameState
import com.tictactoe.domain.Player
import com.tictactoe.ui.theme.BoardLineColor
import com.tictactoe.ui.theme.PlayerOColor
import com.tictactoe.ui.theme.PlayerXColor
import com.tictactoe.ui.theme.WinLineColor

@Composable
fun GameBoard(
    gameState: GameState,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..2) {
                        GameCell(
                            player = gameState.getCell(row, col),
                            isWinningCell = gameState.winningLine?.contains(Pair(row, col)) == true,
                            onClick = { onCellClick(row, col) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
        
        // Draw board grid lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / 3
            
            // Vertical lines
            for (i in 1..2) {
                drawLine(
                    color = BoardLineColor,
                    start = Offset(cellSize * i, 0f),
                    end = Offset(cellSize * i, size.height),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            
            // Horizontal lines
            for (i in 1..2) {
                drawLine(
                    color = BoardLineColor,
                    start = Offset(0f, cellSize * i),
                    end = Offset(size.width, cellSize * i),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        
        // Draw winning line
        gameState.winningLine?.let { winningCells ->
            if (winningCells.size == 3) {
                WinningLine(winningCells)
            }
        }
    }
}

@Composable
fun GameCell(
    player: Player?,
    isWinningCell: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation for cell appearance
    val scale by animateFloatAsState(
        targetValue = if (player != null) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cell_scale"
    )
    
    // Pulse animation for winning cells
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = player == null) { onClick() }
            .background(
                if (isWinningCell) 
                    WinLineColor.copy(alpha = 0.2f) 
                else 
                    Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .scale(if (isWinningCell) pulseScale else scale)
            ) {
                val strokeWidth = 8.dp.toPx()
                val padding = size.width * 0.15f
                
                when (player) {
                    Player.X -> {
                        // Draw X
                        drawLine(
                            color = PlayerXColor,
                            start = Offset(padding, padding),
                            end = Offset(size.width - padding, size.height - padding),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = PlayerXColor,
                            start = Offset(size.width - padding, padding),
                            end = Offset(padding, size.height - padding),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                    Player.O -> {
                        // Draw O
                        drawCircle(
                            color = PlayerOColor,
                            radius = (size.width - padding * 2) / 2,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WinningLine(winningCells: List<Pair<Int, Int>>) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "winning_line"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellSize = size.width / 3
        
        val start = winningCells.first()
        val end = winningCells.last()
        
        val startX = (start.second + 0.5f) * cellSize
        val startY = (start.first + 0.5f) * cellSize
        val endX = (end.second + 0.5f) * cellSize
        val endY = (end.first + 0.5f) * cellSize
        
        val currentEndX = startX + (endX - startX) * animationProgress
        val currentEndY = startY + (endY - startY) * animationProgress
        
        drawLine(
            color = WinLineColor,
            start = Offset(startX, startY),
            end = Offset(currentEndX, currentEndY),
            strokeWidth = 8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
