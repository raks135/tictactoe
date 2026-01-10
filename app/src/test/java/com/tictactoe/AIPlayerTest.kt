package com.tictactoe

import com.tictactoe.domain.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for AIPlayer
 */
class AIPlayerTest {
    
    @Test
    fun testAIBlocksWinningMove() {
        // Setup: X is about to win
        var state = GameState()
        val engine = GameEngine()
        state = engine.makeMove(state, 0, 0) // X
        state = engine.makeMove(state, 1, 0) // O
        state = engine.makeMove(state, 0, 1) // X
        // X has two in a row, AI should block at (0, 2)
        
        val ai = AIPlayer(Difficulty.HARD)
        val move = ai.getBestMove(state, Player.O)
        
        assertNotNull(move)
        assertEquals(Pair(0, 2), move)
    }
    
    @Test
    fun testAITakesWinningMove() {
        // Setup: O can win
        var state = GameState()
        val engine = GameEngine()
        state = engine.makeMove(state, 0, 0) // X
        state = engine.makeMove(state, 1, 0) // O
        state = engine.makeMove(state, 2, 2) // X
        state = engine.makeMove(state, 1, 1) // O
        // O has two in a row, should take winning move at (1, 2)
        
        val ai = AIPlayer(Difficulty.HARD)
        val move = ai.getBestMove(state, Player.O)
        
        assertNotNull(move)
        assertEquals(Pair(1, 2), move)
    }
    
    @Test
    fun testAIReturnsValidMove() {
        val state = GameState()
        val ai = AIPlayer(Difficulty.HARD)
        val move = ai.getBestMove(state, Player.O)
        
        assertNotNull(move)
        assertTrue(move!!.first in 0..2)
        assertTrue(move.second in 0..2)
    }
    
    @Test
    fun testAIReturnsNullForFullBoard() {
        // Create a full board
        var state = GameState()
        val engine = GameEngine()
        state = engine.makeMove(state, 0, 0)
        state = engine.makeMove(state, 0, 1)
        state = engine.makeMove(state, 0, 2)
        state = engine.makeMove(state, 1, 1)
        state = engine.makeMove(state, 1, 0)
        state = engine.makeMove(state, 2, 0)
        state = engine.makeMove(state, 1, 2)
        state = engine.makeMove(state, 2, 2)
        state = engine.makeMove(state, 2, 1)
        
        val ai = AIPlayer(Difficulty.HARD)
        val move = ai.getBestMove(state, Player.O)
        
        assertNull(move)
    }
    
    @Test
    fun testAIPerformanceUnder100ms() {
        val state = GameState()
        val ai = AIPlayer(Difficulty.HARD)
        
        val startTime = System.currentTimeMillis()
        ai.getBestMove(state, Player.O)
        val endTime = System.currentTimeMillis()
        
        val duration = endTime - startTime
        assertTrue("AI took ${duration}ms, should be under 100ms", duration < 100)
    }
    
    @Test
    fun testDifferentDifficulties() {
        val state = GameState()
        
        val easyAI = AIPlayer(Difficulty.EASY)
        val mediumAI = AIPlayer(Difficulty.MEDIUM)
        val hardAI = AIPlayer(Difficulty.HARD)
        
        assertNotNull(easyAI.getBestMove(state, Player.O))
        assertNotNull(mediumAI.getBestMove(state, Player.O))
        assertNotNull(hardAI.getBestMove(state, Player.O))
    }
}
