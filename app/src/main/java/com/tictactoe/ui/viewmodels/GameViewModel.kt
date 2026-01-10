package com.tictactoe.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tictactoe.data.GameRepository
import com.tictactoe.data.PreferencesManager
import com.tictactoe.domain.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing game state and logic
 */
class GameViewModel(
    private val gameRepository: GameRepository,
    private val prefsManager: PreferencesManager
) : ViewModel() {
    
    private val gameEngine = GameEngine()
    private var aiPlayer: AIPlayer = AIPlayer(prefsManager.difficulty)
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _gameMode = MutableStateFlow(GameMode.SINGLE_PLAYER)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()
    
    private val _aiPlayer = MutableStateFlow(Player.O)
    val aiPlayerSide: StateFlow<Player> = _aiPlayer.asStateFlow()
    
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()
    
    private val _showGameOverDialog = MutableStateFlow(false)
    val showGameOverDialog: StateFlow<Boolean> = _showGameOverDialog.asStateFlow()
    
    /**
     * Handle player move
     */
    fun onCellClick(row: Int, col: Int) {
        val currentState = _gameState.value
        
        // Don't allow moves if game is over or AI is thinking
        if (currentState.status != GameStatus.IN_PROGRESS || _isAiThinking.value) {
            return
        }
        
        // Don't allow moves if it's AI's turn in SINGLE_PLAYER mode
        if (_gameMode.value == GameMode.SINGLE_PLAYER && currentState.currentPlayer == _aiPlayer.value) {
            return
        }
        
        // Make the move
        val newState = gameEngine.makeMove(currentState, row, col)
        _gameState.value = newState
        
        // Check if game is over
        if (newState.status != GameStatus.IN_PROGRESS) {
            handleGameOver(newState.status)
        } else if (_gameMode.value == GameMode.SINGLE_PLAYER) {
            // Trigger AI move
            makeAiMove()
        }
    }
    
    /**
     * Make AI move
     */
    private fun makeAiMove() {
        viewModelScope.launch {
            _isAiThinking.value = true
            
            // Small delay to make it feel more natural
            delay(300)
            
            val currentState = _gameState.value
            val move = aiPlayer.getBestMove(currentState, _aiPlayer.value)
            
            if (move != null) {
                val newState = gameEngine.makeMove(currentState, move.first, move.second)
                _gameState.value = newState
                
                if (newState.status != GameStatus.IN_PROGRESS) {
                    handleGameOver(newState.status)
                }
            }
            
            _isAiThinking.value = false
        }
    }
    
    /**
     * Handle game over
     */
    private fun handleGameOver(status: GameStatus) {
        gameRepository.recordGameResult(status)
        _showGameOverDialog.value = true
    }
    
    /**
     * Undo last move
     */
    fun undoMove() {
        if (_isAiThinking.value) return
        
        var newState = gameEngine.undoMove(_gameState.value)
        
        // In SINGLE_PLAYER mode, undo two moves to get back to player's turn
        if (_gameMode.value == GameMode.SINGLE_PLAYER && newState.moveHistory.isNotEmpty()) {
            newState = gameEngine.undoMove(newState)
        }
        
        _gameState.value = newState
        _showGameOverDialog.value = false
    }
    
    /**
     * Start new game
     */
    fun newGame() {
        _gameState.value = gameEngine.resetGame()
        _showGameOverDialog.value = false
        
        // If AI goes first, make its move
        if (_gameMode.value == GameMode.SINGLE_PLAYER && _aiPlayer.value == Player.X) {
            makeAiMove()
        }
    }
    
    /**
     * Set game mode
     */
    fun setGameMode(mode: GameMode) {
        _gameMode.value = mode
        newGame()
    }
    
    /**
     * Set AI difficulty
     */
    fun setDifficulty(difficulty: Difficulty) {
        prefsManager.difficulty = difficulty
        aiPlayer = AIPlayer(difficulty)
    }
    
    /**
     * Set which player the AI controls
     */
    fun setAiPlayer(player: Player) {
        _aiPlayer.value = player
        newGame()
    }
    
    /**
     * Dismiss game over dialog
     */
    fun dismissGameOverDialog() {
        _showGameOverDialog.value = false
    }
    
    /**
     * Get statistics
     */
    fun getStatistics(): Map<String, Int> {
        return mapOf(
            "winsX" to gameRepository.getWinsX(),
            "winsO" to gameRepository.getWinsO(),
            "draws" to gameRepository.getDraws(),
            "total" to gameRepository.getGamesPlayed()
        )
    }
    
    /**
     * Reset statistics
     */
    fun resetStatistics() {
        gameRepository.resetStatistics()
    }
}
