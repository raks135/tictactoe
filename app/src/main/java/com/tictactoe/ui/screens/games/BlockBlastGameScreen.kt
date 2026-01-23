package com.tictactoe.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.data.PreferencesManager
import com.tictactoe.ui.components.GameHelpDialog
import com.tictactoe.ui.utils.HapticManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

// Game Constants
private const val GRID_SIZE = 8
private const val CELL_SIZE_DP = 40

data class BlockShape(
    val id: Int,
    val pattern: List<Pair<Int, Int>>, // relative coordinates (row, col)
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockBlastGameScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val haptics = remember { HapticManager(context) }
    val scope = rememberCoroutineScope()
    
    // Game State
    var grid by remember { mutableStateOf(Array(GRID_SIZE) { Array(GRID_SIZE) { Color.Transparent } }) }
    var score by remember { mutableStateOf(0) }
    var highScore by remember { mutableStateOf(prefs.blockBlastHighScore) }
    var availableShapes by remember { mutableStateOf(generateShapes(3)) }
    var isGameOver by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    
    // Animation State
    var clearedCells by remember { mutableStateOf<Set<Pair<Int, Int>>>(emptySet()) }
    
    // Drag State
    var draggedShape by remember { mutableStateOf<BlockShape?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) } // Screen coordinates of finger
    
    // Grid measurements
    var gridTopLeft by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val cellSizePx = with(density) { CELL_SIZE_DP.dp.toPx() }

    fun checkGameOver() {
        if (availableShapes.isEmpty()) {
            availableShapes = generateShapes(3)
        }
        
        // Check if any available shape can fit
        val canMove = availableShapes.any { shape ->
            canShapeFitAnywhere(grid, shape)
        }
        
        if (!canMove) {
            isGameOver = true
            haptics.mediumFeedback()
            if (score > highScore) {
                highScore = score
                prefs.blockBlastHighScore = score
            }
        }
    }

    fun placeShape(shape: BlockShape, startRow: Int, startCol: Int) {
        val newGrid = grid.map { it.clone() }.toTypedArray()
        
        shape.pattern.forEach { (r, c) ->
            newGrid[startRow + r][startCol + c] = shape.color
        }
        
        // Haptic feedback for placement
        haptics.lightTap()
        
        // Check for cleared lines
        val rowsToClear = mutableListOf<Int>()
        for (r in 0 until GRID_SIZE) {
            if (newGrid[r].all { it != Color.Transparent }) {
                rowsToClear.add(r)
            }
        }
        
        val colsToClear = mutableListOf<Int>()
        for (c in 0 until GRID_SIZE) {
            var full = true
            for (r in 0 until GRID_SIZE) {
                if (newGrid[r][c] == Color.Transparent) {
                    full = false
                    break
                }
            }
            if (full) colsToClear.add(c)
        }
        
        if (rowsToClear.isNotEmpty() || colsToClear.isNotEmpty()) {
            // Trigger animation and sound
            val cleared = mutableSetOf<Pair<Int, Int>>()
            rowsToClear.forEach { r ->
                for (c in 0 until GRID_SIZE) cleared.add(r to c)
            }
            colsToClear.forEach { c ->
                for (r in 0 until GRID_SIZE) cleared.add(r to c)
            }
            clearedCells = cleared
            haptics.successPattern()
            
            // Delay clearing to show animation
            scope.launch {
                delay(300)
                // Clear lines
                rowsToClear.forEach { r ->
                    for (c in 0 until GRID_SIZE) newGrid[r][c] = Color.Transparent
                }
                colsToClear.forEach { c ->
                    for (r in 0 until GRID_SIZE) newGrid[r][c] = Color.Transparent
                }
                grid = newGrid
                clearedCells = emptySet()
                
                // Update score
                val points = shape.pattern.size + (rowsToClear.size + colsToClear.size) * 10
                score += points
                
                // Remove placed shape and check game over
                availableShapes = availableShapes.filter { it.id != shape.id }
                checkGameOver()
            }
        } else {
            grid = newGrid
            // Update score
            score += shape.pattern.size
            
            // Remove placed shape and check game over
            availableShapes = availableShapes.filter { it.id != shape.id }
            checkGameOver()
        }
    }
    
    fun onDrop(shape: BlockShape, dropPos: Offset) {
        // Calculate grid relative position
        val relativeX = dropPos.x - gridTopLeft.x
        val relativeY = dropPos.y - gridTopLeft.y
        
        // Convert to row/col
        // Adjust for touch point being roughly center of shape finger
        // Let's assume user grabs near center. We need top-left of shape to snap.
        // Simplified: Use touch point as top-left of shape
        // Better: Use touch point - (shapeWidth/2, shapeHeight/2)
        
        val col = (relativeX / cellSizePx).roundToInt()
        val row = (relativeY / cellSizePx).roundToInt()
        
        // Offset to center the drop a bit
        // For now, let's try direct mapping
        
        // Need to check if drop is valid
        if (canPlaceShape(grid, shape, row, col)) {
            placeShape(shape, row, col)
        }
    }

    if (showHelp) {
        GameHelpDialog(
            title = "Block Blast",
            lines = listOf(
                "Drag blocks onto the grid.",
                "Fill rows or columns to clear them.",
                "Game over if no blocks fit.",
                "Try to beat your High Score!"
            ),
            onDismiss = { showHelp = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Block Blast", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            "Score: $score   High: $highScore",
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
                        grid = Array(GRID_SIZE) { Array(GRID_SIZE) { Color.Transparent } }
                        score = 0
                        isGameOver = false
                        availableShapes = generateShapes(3)
                    }) {
                        Icon(Icons.Default.Refresh, "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFB8C00)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFFB8C00), Color(0xFFF57C00))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Game Grid
                Box(
                    modifier = Modifier
                        .size((CELL_SIZE_DP * GRID_SIZE).dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .onGloballyPositioned { coordinates ->
                            gridTopLeft = coordinates.positionInWindow()
                        }
                ) {
                    Column {
                        for (r in 0 until GRID_SIZE) {
                            Row {
                                for (c in 0 until GRID_SIZE) {
                                    val isCleared = clearedCells.contains(r to c)
                                    val cellColor by animateColorAsState(
                                        targetValue = if (isCleared) Color.White else (grid[r][c].takeIf { it != Color.Transparent } ?: Color.White.copy(alpha = 0.3f)),
                                        label = "cellColor"
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(CELL_SIZE_DP.dp)
                                            .padding(1.dp)
                                            .background(
                                                cellColor,
                                                RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Available Shapes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    availableShapes.forEach { shape ->
                        // If being dragged, show placeholder or nothing
                        if (draggedShape?.id != shape.id) {
                            DraggableShape(
                                shape = shape,
                                cellSize = CELL_SIZE_DP.dp / 2, // Smaller preview
                                onDragStart = { startPos ->
                                    draggedShape = shape
                                    dragPosition = startPos
                                },
                                onDragEnd = {
                                    if (draggedShape != null) {
                                        // Attempt drop
                                        // We need to account for the shape size to drop correctly from center/finger
                                        // Adjust dragPosition to be "top-left" of the shape visually
                                        // A heuristic: Shift up and left by 1 cell size equivalent
                                        val dropAdjust = Offset(-cellSizePx, -cellSizePx)
                                        onDrop(draggedShape!!, dragPosition + dropAdjust)
                                        draggedShape = null
                                    }
                                },
                                onDrag = { dragAmount ->
                                    dragPosition += dragAmount
                                }
                            )
                        } else {
                             Box(modifier = Modifier.size(60.dp)) // Placeholder
                        }
                    }
                }
            }
            
            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GAME OVER", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Final Score: $score", color = Color.White, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = {
                            grid = Array(GRID_SIZE) { Array(GRID_SIZE) { Color.Transparent } }
                            score = 0
                            isGameOver = false
                            availableShapes = generateShapes(3)
                        }) {
                            Text("Play Again")
                        }
                    }
                }
            }
        }
        
        // Dragged Shape Overlay (Full screen layer)
        if (draggedShape != null) {
            val shape = draggedShape!!
            // We render the shape at dragPosition
            // Since `offset` in Box is relative to parent top-left (0,0), and we have full screen Box
            // dragPosition is absolute screen coords.
            // We need to convert pixels to dp for offset
            
            val offsetX = with(density) { dragPosition.x.toDp() }
            val offsetY = with(density) { dragPosition.y.toDp() }
            
            // Center the shape on the finger roughly
            // Shape view is full size (CELL_SIZE_DP)
            val shapeWidth = shape.pattern.maxOf { it.second + 1 } * CELL_SIZE_DP
            val shapeHeight = shape.pattern.maxOf { it.first + 1 } * CELL_SIZE_DP
            
            Box(
                modifier = Modifier
                    .offset(
                        x = offsetX - (shapeWidth.dp / 2),
                        y = offsetY - (shapeHeight.dp / 2) // Center on finger
                    )
            ) {
                 ShapeView(shape, CELL_SIZE_DP.dp)
            }
        }
    }
}

// Helper Composable for Shapes
@Composable
fun DraggableShape(
    shape: BlockShape,
    cellSize: androidx.compose.ui.unit.Dp,
    onDragStart: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Offset) -> Unit
) {
    var position by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                position = coordinates.positionInWindow()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onDragStart(position + offset) },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                    onDrag = { change, dragAmount -> 
                        change.consume()
                        onDrag(dragAmount) 
                    }
                )
            }
    ) {
        ShapeView(shape, cellSize)
    }
}

@Composable
fun ShapeView(shape: BlockShape, cellSize: androidx.compose.ui.unit.Dp) {
    Column {
        val rows = shape.pattern.maxOf { it.first } + 1
        val cols = shape.pattern.maxOf { it.second } + 1
        
        for (r in 0 until rows) {
            Row {
                for (c in 0 until cols) {
                    val isPart = shape.pattern.contains(r to c)
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(1.dp)
                            .background(
                                if (isPart) shape.color else Color.Transparent,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

// Logic Helpers
private fun generateShapes(count: Int): List<BlockShape> {
    val shapes = mutableListOf<BlockShape>()
    for (i in 0 until count) {
        shapes.add(randomShape(i))
    }
    return shapes
}

private fun randomShape(id: Int): BlockShape {
    // Simple shapes: 1x1, 2x2, 3x1, L-shape, etc.
    val types = listOf(
        listOf(0 to 0), // Dot
        listOf(0 to 0, 0 to 1), // 2-horizontal
        listOf(0 to 0, 1 to 0), // 2-vertical
        listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1), // Square (2x2)
        listOf(0 to 0, 0 to 1, 0 to 2), // 3-horizontal
        listOf(0 to 0, 1 to 0, 2 to 0), // 3-vertical
        listOf(0 to 0, 1 to 0, 1 to 1), // L-shape
        listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3), // 4-horizontal
        listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0), // 4-vertical
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 1), // T-shape
        listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2), // Z-shape
        listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1), // S-shape
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 2 to 0), // Big L
        listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2)  // Big J
    )
    val colors = listOf(
        Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A), 
        Color(0xFFFFCA28), Color(0xFFAB47BC), Color(0xFF26C6DA),
        Color(0xFFFF7043), Color(0xFF5C6BC0)
    )
    
    return BlockShape(
        id = Random.nextInt(),
        pattern = types.random(),
        color = colors.random()
    )
}

private fun canShapeFitAnywhere(grid: Array<Array<Color>>, shape: BlockShape): Boolean {
    for (r in 0 until GRID_SIZE) {
        for (c in 0 until GRID_SIZE) {
            if (canPlaceShape(grid, shape, r, c)) return true
        }
    }
    return false
}

private fun canPlaceShape(grid: Array<Array<Color>>, shape: BlockShape, r: Int, c: Int): Boolean {
    return shape.pattern.all { (dr, dc) ->
        val nr = r + dr
        val nc = c + dc
        nr in 0 until GRID_SIZE && nc in 0 until GRID_SIZE && grid[nr][nc] == Color.Transparent
    }
}
