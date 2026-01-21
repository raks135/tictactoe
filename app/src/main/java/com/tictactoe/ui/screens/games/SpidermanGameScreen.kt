package com.tictactoe.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

// Game Constants
private const val LANE_COUNT = 3
private const val PLAYER_SIZE = 60f
private const val OBSTACLE_SIZE = 50f
private const val COIN_SIZE = 30f
private const val BASE_SPEED = 10f

data class GameObject(
    val id: Long,
    val lane: Int, // 0, 1, 2
    val y: Float, // 0.0 to 1.0 (screen height)
    val type: ObjectType
)

enum class ObjectType {
    OBSTACLE, COIN
}

@Composable
fun SpidermanGameScreen(
    onBack: () -> Unit
) {
    var playerLane by remember { mutableStateOf(1) } // Start in middle lane
    var gameObjects by remember { mutableStateOf(listOf<GameObject>()) }
    var score by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var gameSpeed by remember { mutableStateOf(BASE_SPEED) }
    var nextId by remember { mutableStateOf(0L) }
    
    // Game Loop
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            var lastSpawnTime = 0L
            val startTime = System.currentTimeMillis()
            
            while (true) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = 16L // ~60 FPS
                
                // Spawn objects
                if (currentTime - lastSpawnTime > 1500 - (score * 10).coerceAtMost(1000)) {
                    val lane = Random.nextInt(LANE_COUNT)
                    val type = if (Random.nextFloat() > 0.7f) ObjectType.COIN else ObjectType.OBSTACLE
                    
                    gameObjects = gameObjects + GameObject(
                        id = nextId++,
                        lane = lane,
                        y = -0.1f, // Start slightly above screen
                        type = type
                    )
                    lastSpawnTime = currentTime
                }
                
                // Update objects
                gameObjects = gameObjects.map { obj ->
                    obj.copy(y = obj.y + (gameSpeed / 2000f))
                }.filter { it.y < 1.2f } // Remove objects below screen
                
                // Collision Detection
                val playerY = 0.8f // Player is near bottom
                val collisionThreshold = 0.05f
                
                val collidedObject = gameObjects.find { obj ->
                    obj.lane == playerLane && abs(obj.y - playerY) < collisionThreshold
                }
                
                if (collidedObject != null) {
                    if (collidedObject.type == ObjectType.OBSTACLE) {
                        isGameOver = true
                    } else if (collidedObject.type == ObjectType.COIN) {
                        score += 10
                        gameObjects = gameObjects.filter { it.id != collidedObject.id }
                    }
                }
                
                // Increase difficulty
                gameSpeed = BASE_SPEED + (score / 50f)
                
                delay(deltaTime)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Spider Run",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            "Score: $score",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        // Reset Game
                        isGameOver = false
                        score = 0
                        gameObjects = emptyList()
                        playerLane = 1
                        gameSpeed = BASE_SPEED
                    }) {
                        Icon(Icons.Default.Refresh, "Reset", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFB71C1C)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF263238))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Simple tap controls: Tap left side -> move left, Tap right side -> move right
                        if (offset.x < size.width / 2) {
                            if (playerLane > 0) playerLane--
                        } else {
                            if (playerLane < LANE_COUNT - 1) playerLane++
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val laneWidth = size.width / LANE_COUNT
                val height = size.height
                
                // Draw Lanes
                for (i in 1 until LANE_COUNT) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(laneWidth * i, 0f),
                        end = Offset(laneWidth * i, height),
                        strokeWidth = 2f
                    )
                }
                
                // Draw Game Objects
                gameObjects.forEach { obj ->
                    val x = (obj.lane * laneWidth) + (laneWidth / 2)
                    val y = obj.y * height
                    
                    if (obj.type == ObjectType.OBSTACLE) {
                        // Draw Web Obstacle
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = OBSTACLE_SIZE / 2,
                            center = Offset(x, y),
                            style = Stroke(width = 3f)
                        )
                        // Draw web lines
                        drawLine(Color.White, Offset(x - 20, y - 20), Offset(x + 20, y + 20))
                        drawLine(Color.White, Offset(x + 20, y - 20), Offset(x - 20, y + 20))
                    } else {
                        // Draw Coin
                        drawCircle(
                            color = Color(0xFFFFD700),
                            radius = COIN_SIZE / 2,
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color(0xFFFFA000),
                            radius = COIN_SIZE / 2,
                            center = Offset(x, y),
                            style = Stroke(width = 4f)
                        )
                    }
                }
                
                // Draw Player (Spider)
                val playerX = (playerLane * laneWidth) + (laneWidth / 2)
                val playerY = 0.8f * height
                
                drawCircle(
                    color = Color(0xFFD32F2F), // Red body
                    radius = PLAYER_SIZE / 2,
                    center = Offset(playerX, playerY)
                )
                // Spider details
                drawCircle(
                    color = Color.Black,
                    radius = PLAYER_SIZE / 4,
                    center = Offset(playerX, playerY)
                )
                // Legs
                drawLine(Color.Black, Offset(playerX - 25, playerY - 10), Offset(playerX - 40, playerY - 25), strokeWidth = 4f)
                drawLine(Color.Black, Offset(playerX + 25, playerY - 10), Offset(playerX + 40, playerY - 25), strokeWidth = 4f)
                drawLine(Color.Black, Offset(playerX - 25, playerY + 10), Offset(playerX - 40, playerY + 25), strokeWidth = 4f)
                drawLine(Color.Black, Offset(playerX + 25, playerY + 10), Offset(playerX + 40, playerY + 25), strokeWidth = 4f)
            }
            
            // Game Over Overlay
            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "GAME OVER",
                            color = Color.Red,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Final Score: $score",
                            color = Color.White,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                isGameOver = false
                                score = 0
                                gameObjects = emptyList()
                                playerLane = 1
                                gameSpeed = BASE_SPEED
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Play Again")
                        }
                    }
                }
            }
            
            // Tutorial Overlay (only at start)
            if (!isGameOver && score == 0 && gameObjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        "Tap Left or Right to Move!",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
