package com.tictactoe.domain.games

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MemoryCard(
    val id: Int,
    val icon: ImageVector,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

class MemoryGameEngine {
    private val icons = listOf(
        Icons.Default.Star,
        Icons.Default.Favorite,
        Icons.Default.Face,
        Icons.Default.Home,
        Icons.Default.Person,
        Icons.Default.Settings,
        Icons.Default.ShoppingCart,
        Icons.Default.ThumbUp
    )

    fun generateCards(pairCount: Int = 6): List<MemoryCard> {
        // Select random icons for pairs
        val selectedIcons = icons.shuffled().take(pairCount)
        
        // Create pairs
        val cards = ArrayList<MemoryCard>()
        selectedIcons.forEachIndexed { index, icon ->
            cards.add(MemoryCard(id = index * 2, icon = icon))
            cards.add(MemoryCard(id = index * 2 + 1, icon = icon))
        }
        
        return cards.shuffled()
    }
}
