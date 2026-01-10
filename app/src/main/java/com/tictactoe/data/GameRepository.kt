package com.tictactoe.data

import com.tictactoe.domain.GameStatus

/**
 * Repository for managing game data and statistics
 */
class GameRepository(private val prefsManager: PreferencesManager) {
    
    /**
     * Record a game result
     */
    fun recordGameResult(status: GameStatus) {
        when (status) {
            GameStatus.X_WON -> prefsManager.winsX++
            GameStatus.O_WON -> prefsManager.winsO++
            GameStatus.DRAW -> prefsManager.draws++
            GameStatus.IN_PROGRESS -> return
        }
        prefsManager.gamesPlayed++
    }
    
    /**
     * Get total wins for X
     */
    fun getWinsX(): Int = prefsManager.winsX
    
    /**
     * Get total wins for O
     */
    fun getWinsO(): Int = prefsManager.winsO
    
    /**
     * Get total draws
     */
    fun getDraws(): Int = prefsManager.draws
    
    /**
     * Get total games played
     */
    fun getGamesPlayed(): Int = prefsManager.gamesPlayed
    
    /**
     * Reset all statistics
     */
    fun resetStatistics() {
        prefsManager.resetStatistics()
    }
}
