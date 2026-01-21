package com.tictactoe.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.ui.components.CharacterFeedback
import com.tictactoe.ui.components.GameHelpDialog
import kotlinx.coroutines.delay

enum class AnimalCategory(val title: String) {
    PETS("Pets"),
    WATER("Water Animals"),
    BIRDS("Birds"),
    MIXED("Mixed Animals")
}

data class AnimalItem(
    val id: Int,
    val emoji: String,
    val name: String,
    val category: AnimalCategory
)

@Composable
fun AnimalMatchGameScreen(
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(AnimalCategory.MIXED) }
    var gameItems by remember { mutableStateOf(generateAnimalGame(selectedCategory)) }
    
    // Game State
    var selectedLeftId by remember { mutableStateOf<Int?>(null) }
    var matchedIds by remember { mutableStateOf(setOf<Int>()) }
    var showFeedback by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableStateOf(0) }
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        GameHelpDialog(
            title = "Animal Match",
            lines = listOf(
                "Pick an animal picture on the left.",
                "Tap the matching name on the right.",
                "Use tabs to switch Pets/Water Animals/Birds."
            ),
            onDismiss = { showHelp = false }
        )
    }
    
    // Reset when all matched
    LaunchedEffect(matchedIds) {
        if (matchedIds.size == gameItems.leftItems.size && matchedIds.isNotEmpty()) {
            delay(1500)
            gameItems = generateAnimalGame(selectedCategory)
            matchedIds = emptySet()
            selectedLeftId = null
            showFeedback = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Animal Match",
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
                    IconButton(onClick = { 
                        gameItems = generateAnimalGame(selectedCategory)
                        matchedIds = emptySet()
                        selectedLeftId = null
                    }) {
                        Icon(Icons.Default.Refresh, "Reset")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF81C784)
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
                        colors = listOf(Color(0xFF81C784), Color(0xFF4CAF50))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Category Selector
                ScrollableTabRow(
                    selectedTabIndex = AnimalCategory.values().indexOf(selectedCategory),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = {}
                ) {
                    AnimalCategory.values().forEach { category ->
                        FilterChip(
                            selected = category == selectedCategory,
                            onClick = { 
                                selectedCategory = category
                                gameItems = generateAnimalGame(category)
                                matchedIds = emptySet()
                                selectedLeftId = null
                            },
                            label = { Text(category.title) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White,
                                selectedLabelColor = Color(0xFF2E7D32)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Match the animal to its name!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Game Area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column (Images)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        gameItems.leftItems.forEach { animal ->
                            AnimalImageCard(
                                animal = animal,
                                isSelected = selectedLeftId == animal.id,
                                isMatched = matchedIds.contains(animal.id),
                                onClick = {
                                    if (!matchedIds.contains(animal.id)) {
                                        selectedLeftId = animal.id
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Right Column (Names)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        gameItems.rightItems.forEach { animal ->
                            AnimalNameCard(
                                animal = animal,
                                isMatched = matchedIds.contains(animal.id),
                                onClick = {
                                    if (!matchedIds.contains(animal.id) && selectedLeftId != null) {
                                        if (selectedLeftId == animal.id) {
                                            // Correct match
                                            matchedIds = matchedIds + animal.id
                                            selectedLeftId = null
                                            score++
                                            showFeedback = true
                                        } else {
                                            // Wrong match
                                            selectedLeftId = null
                                            showFeedback = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                
                // Feedback
                CharacterFeedback(
                    isCorrect = showFeedback ?: false,
                    isVisible = showFeedback != null
                )
            }
        }
    }
}

@Composable
fun AnimalImageCard(
    animal: AnimalItem,
    isSelected: Boolean,
    isMatched: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isMatched -> Color(0xFF10B981) // Green
            isSelected -> Color(0xFFFFD740) // Yellow
            else -> Color.Transparent
        },
        label = "border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(4.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !isMatched) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMatched) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = animal.emoji,
                fontSize = 40.sp
            )
            if (isMatched) {
                // Checkmark overlay
                Text(
                    text = "✓",
                    fontSize = 30.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun AnimalNameCard(
    animal: AnimalItem,
    isMatched: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isMatched) Color(0xFF10B981) else Color.White,
        label = "color"
    )
    
    val textColor = if (isMatched) Color.White else Color(0xFF1F2937)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(enabled = !isMatched) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = animal.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

// Data and Generation Logic

data class GameState(
    val leftItems: List<AnimalItem>,
    val rightItems: List<AnimalItem>
)

private val allAnimals = listOf(
    // Pets
    AnimalItem(1, "🐶", "Dog", AnimalCategory.PETS),
    AnimalItem(2, "🐱", "Cat", AnimalCategory.PETS),
    AnimalItem(3, "🐰", "Rabbit", AnimalCategory.PETS),
    AnimalItem(4, "🐹", "Hamster", AnimalCategory.PETS),
    AnimalItem(5, "🐕", "Puppy", AnimalCategory.PETS),
    
    // Water Animals
    AnimalItem(6, "🐟", "Fish", AnimalCategory.WATER),
    AnimalItem(7, "🐙", "Octopus", AnimalCategory.WATER),
    AnimalItem(8, "🐬", "Dolphin", AnimalCategory.WATER),
    AnimalItem(9, "🐳", "Whale", AnimalCategory.WATER),
    AnimalItem(10, "🦀", "Crab", AnimalCategory.WATER),
    AnimalItem(11, "🦈", "Shark", AnimalCategory.WATER),
    
    // Birds
    AnimalItem(12, "🦜", "Parrot", AnimalCategory.BIRDS),
    AnimalItem(13, "🦅", "Eagle", AnimalCategory.BIRDS),
    AnimalItem(14, "🦆", "Duck", AnimalCategory.BIRDS),
    AnimalItem(15, "🦉", "Owl", AnimalCategory.BIRDS),
    AnimalItem(16, "🐓", "Rooster", AnimalCategory.BIRDS),
    AnimalItem(17, "🦚", "Peacock", AnimalCategory.BIRDS)
)

private fun generateAnimalGame(category: AnimalCategory): GameState {
    val candidates = if (category == AnimalCategory.MIXED) {
        allAnimals
    } else {
        allAnimals.filter { it.category == category }
    }
    
    // Take 4 random items
    val selected = candidates.shuffled().take(4)
    
    return GameState(
        leftItems = selected, // Keep original order or shuffle? Let's keep one consistent and shuffle the other
        rightItems = selected.shuffled()
    )
}
