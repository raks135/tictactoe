package com.tictactoe

import com.tictactoe.domain.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for GameEngine
 */
class GameEngineTest {
    
    private val engine = GameEngine()
    
    @Test
    fun testInitialState() {
        val state = GameState()
        assertEquals(Player.X, state.currentPlayer)
        assertEquals(GameStatus.IN_PROGRESS, state.status)
        assertTrue(state.moveHistory.isEmpty())
    }
    
    @Test
    fun testMakeMove() {
        val state = GameState()
        val newState = engine.makeMove(state, 0, 0)
        
        assertEquals(Player.X, newState.getCell(0, 0))
        assertEquals(Player.O, newState.currentPlayer)
        assertEquals(1, newState.moveHistory.size)
    }
    
    @Test
    fun testInvalidMove() {
        var state = GameState()
        state = engine.makeMove(state, 0, 0)
        val newState = engine.makeMove(state, 0, 0) // Same cell
        
        // State should not change
        assertEquals(state, newState)
    }
    
    @Test
    fun testWinDetectionRow() {
        var state = GameState()
        state = engine.makeMove(state, 0, 0) // X
        state = engine.makeMove(state, 1, 0) // O
        state = engine.makeMove(state, 0, 1) // X
        state = engine.makeMove(state, 1, 1) // O
        state = engine.makeMove(state, 0, 2) // X wins
        
        assertEquals(GameStatus.X_WON, state.status)
        assertNotNull(state.winningLine)
        assertEquals(3, state.winningLine?.size)
    }
    
    @Test
    fun testWinDetectionColumn() {
        var state = GameState()
        state = engine.makeMove(state, 0, 0) // X
        state = engine.makeMove(state, 0, 1) // O
        state = engine.makeMove(state, 1, 0) // X
        state = engine.makeMove(state, 1, 1) // O
        state = engine.makeMove(state, 2, 0) // X wins
        
        assertEquals(GameStatus.X_WON, state.status)
        assertNotNull(state.winningLine)
    }
    
    @Test
    fun testWinDetectionDiagonal() {
        var state = GameState()
        state = engine.makeMove(state, 0, 0) // X
        state = engine.makeMove(state, 0, 1) // O
        state = engine.makeMove(state, 1, 1) // X
        state = engine.makeMove(state, 0, 2) // O
        state = engine.makeMove(state, 2, 2) // X wins
        
        assertEquals(GameStatus.X_WON, state.status)
        assertNotNull(state.winningLine)
    }
    
    @Test
    fun testDrawDetection() {
        var state = GameState()
        // Create a draw scenario
        state = engine.makeMove(state, 0, 0) // X
        state = engine.makeMove(state, 0, 1) // O
        state = engine.makeMove(state, 0, 2) // X
        state = engine.makeMove(state, 1, 1) // O
        state = engine.makeMove(state, 1, 0) // X
        state = engine.makeMove(state, 2, 0) // O
        state = engine.makeMove(state, 1, 2) // X
        state = engine.makeMove(state, 2, 2) // O
        state = engine.makeMove(state, 2, 1) // X
        
        assertEquals(GameStatus.DRAW, state.status)
    }
    
    @Test
    fun testUndoMove() {
        var state = GameState()
        state = engine.makeMove(state, 0, 0)
        state = engine.makeMove(state, 1, 1)
        
        val undoneState = engine.undoMove(state)
        
        assertEquals(1, undoneState.moveHistory.size)
        assertEquals(Player.O, undoneState.currentPlayer)
        assertNull(undoneState.getCell(1, 1))
    }
    
    @Test
    fun testResetGame() {
        var state = GameState()
        state = engine.makeMove(state, 0, 0)
        state = engine.makeMove(state, 1, 1)
        
        val resetState = engine.resetGame()
        
        assertEquals(Player.X, resetState.currentPlayer)
        assertEquals(GameStatus.IN_PROGRESS, resetState.status)
        assertTrue(resetState.moveHistory.isEmpty())
    }
}
