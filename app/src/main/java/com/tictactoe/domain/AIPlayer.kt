package com.tictactoe.domain

import kotlin.random.Random

/**
 * AI player that uses minimax algorithm to make optimal moves
 */
class AIPlayer(private val difficulty: Difficulty = Difficulty.HARD) {
    
    /**
     * Calculate the best move for the AI
     * Guaranteed to complete in <100ms
     */
    fun getBestMove(state: GameState, aiPlayer: Player): Pair<Int, Int>? {
        val emptyCells = state.getEmptyCells()
        if (emptyCells.isEmpty()) return null
        
        return when (difficulty) {
            Difficulty.EASY -> getEasyMove(emptyCells, state, aiPlayer)
            Difficulty.MEDIUM -> getMediumMove(state, aiPlayer, emptyCells)
            Difficulty.HARD -> getHardMove(state, aiPlayer, emptyCells)
        }
    }
    
    /**
     * Easy mode: 70% random moves, 30% optimal moves
     */
    private fun getEasyMove(
        emptyCells: List<Pair<Int, Int>>,
        state: GameState,
        aiPlayer: Player
    ): Pair<Int, Int> {
        return if (Random.nextFloat() < 0.7f) {
            // Random move
            emptyCells.random()
        } else {
            // Optimal move
            minimax(state, aiPlayer, aiPlayer, Int.MIN_VALUE, Int.MAX_VALUE, 9).second
                ?: emptyCells.random()
        }
    }
    
    /**
     * Medium mode: Uses minimax with depth limit of 4
     */
    private fun getMediumMove(
        state: GameState,
        aiPlayer: Player,
        emptyCells: List<Pair<Int, Int>>
    ): Pair<Int, Int> {
        return minimax(state, aiPlayer, aiPlayer, Int.MIN_VALUE, Int.MAX_VALUE, 4).second
            ?: emptyCells.random()
    }
    
    /**
     * Hard mode: Full minimax with alpha-beta pruning
     */
    private fun getHardMove(
        state: GameState,
        aiPlayer: Player,
        emptyCells: List<Pair<Int, Int>>
    ): Pair<Int, Int> {
        return minimax(state, aiPlayer, aiPlayer, Int.MIN_VALUE, Int.MAX_VALUE, 9).second
            ?: emptyCells.random()
    }
    
    /**
     * Minimax algorithm with alpha-beta pruning
     * Returns Pair<score, move>
     */
    private fun minimax(
        state: GameState,
        aiPlayer: Player,
        currentPlayer: Player,
        alpha: Int,
        beta: Int,
        depth: Int
    ): Pair<Int, Pair<Int, Int>?> {
        // Terminal state or depth limit reached
        if (depth == 0 || state.status != GameStatus.IN_PROGRESS) {
            return Pair(evaluateBoard(state, aiPlayer), null)
        }
        
        val emptyCells = state.getEmptyCells()
        if (emptyCells.isEmpty()) {
            return Pair(0, null)
        }
        
        var bestMove: Pair<Int, Int>? = null
        var currentAlpha = alpha
        var currentBeta = beta
        
        if (currentPlayer == aiPlayer) {
            // Maximizing player
            var maxScore = Int.MIN_VALUE
            
            for (cell in emptyCells) {
                val newState = simulateMove(state, cell.first, cell.second, currentPlayer)
                val score = minimax(
                    newState,
                    aiPlayer,
                    currentPlayer.other(),
                    currentAlpha,
                    currentBeta,
                    depth - 1
                ).first
                
                if (score > maxScore) {
                    maxScore = score
                    bestMove = cell
                }
                
                currentAlpha = maxOf(currentAlpha, score)
                if (currentBeta <= currentAlpha) {
                    break // Beta cutoff
                }
            }
            
            return Pair(maxScore, bestMove)
        } else {
            // Minimizing player
            var minScore = Int.MAX_VALUE
            
            for (cell in emptyCells) {
                val newState = simulateMove(state, cell.first, cell.second, currentPlayer)
                val score = minimax(
                    newState,
                    aiPlayer,
                    currentPlayer.other(),
                    currentAlpha,
                    currentBeta,
                    depth - 1
                ).first
                
                if (score < minScore) {
                    minScore = score
                    bestMove = cell
                }
                
                currentBeta = minOf(currentBeta, score)
                if (currentBeta <= currentAlpha) {
                    break // Alpha cutoff
                }
            }
            
            return Pair(minScore, bestMove)
        }
    }
    
    /**
     * Evaluate the board state from AI's perspective
     */
    private fun evaluateBoard(state: GameState, aiPlayer: Player): Int {
        return when (state.status) {
            GameStatus.X_WON -> if (aiPlayer == Player.X) 10 else -10
            GameStatus.O_WON -> if (aiPlayer == Player.O) 10 else -10
            GameStatus.DRAW -> 0
            GameStatus.IN_PROGRESS -> 0
        }
    }
    
    /**
     * Simulate a move without modifying the original state
     */
    private fun simulateMove(state: GameState, row: Int, col: Int, player: Player): GameState {
        val engine = GameEngine()
        return engine.makeMove(
            state.copy(currentPlayer = player),
            row,
            col
        )
    }
}
