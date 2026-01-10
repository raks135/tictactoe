package com.tictactoe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tictactoe.data.GameRepository
import com.tictactoe.data.PreferencesManager
import com.tictactoe.ui.viewmodels.GameViewModel

/**
 * Factory for creating GameViewModel with dependencies
 */
class GameViewModelFactory(
    private val gameRepository: GameRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(gameRepository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
