package com.tictactoe.domain

/**
 * Core game engine that handles game logic and rules
 */
class GameEngine {
    
    /**
     * Make a move on the board
     */
    fun makeMove(state: GameState, row: Int, col: Int): GameState {
        // Validate move
        if (!isValidMove(state, row, col)) {
            return state
        }
        
        // Create new board with the move
        val newBoard = state.board.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) state.currentPlayer else cell
            }
        }
        
        // Add move to history
        val newHistory = state.moveHistory + Move(row, col, state.currentPlayer)
        
        // Check game status
        val (status, winningLine) = checkGameStatus(newBoard, state.currentPlayer)
        
        return GameState(
            board = newBoard,
            currentPlayer = if (status == GameStatus.IN_PROGRESS) state.currentPlayer.other() else state.currentPlayer,
            status = status,
            moveHistory = newHistory,
            winningLine = winningLine
        )
    }
    
    /**
     * Undo the last move
     */
    fun undoMove(state: GameState): GameState {
        if (state.moveHistory.isEmpty()) {
            return state
        }
        
        // Remove last move from history
        val newHistory = state.moveHistory.dropLast(1)
        
        // Rebuild board from history
        val newBoard = MutableList(3) { MutableList<Player?>(3) { null } }
        newHistory.forEach { move ->
            newBoard[move.row][move.col] = move.player
        }
        
        // Determine current player
        val currentPlayer = if (newHistory.size % 2 == 0) Player.X else Player.O
        
        return GameState(
            board = newBoard.map { it.toList() },
            currentPlayer = currentPlayer,
            status = GameStatus.IN_PROGRESS,
            moveHistory = newHistory,
            winningLine = null
        )
    }
    
    /**
     * Reset the game to initial state
     */
    fun resetGame(): GameState = GameState()
    
    /**
     * Check if a move is valid
     */
    private fun isValidMove(state: GameState, row: Int, col: Int): Boolean {
        return row in 0..2 && 
               col in 0..2 && 
               state.isCellEmpty(row, col) && 
               state.status == GameStatus.IN_PROGRESS
    }
    
    /**
     * Check the current game status and return winning line if applicable
     */
    private fun checkGameStatus(board: List<List<Player?>>, lastPlayer: Player): Pair<GameStatus, List<Pair<Int, Int>>?> {
        // Check rows
        for (row in 0..2) {
            if (board[row][0] == lastPlayer && 
                board[row][1] == lastPlayer && 
                board[row][2] == lastPlayer) {
                val winningLine = listOf(Pair(row, 0), Pair(row, 1), Pair(row, 2))
                val status = if (lastPlayer == Player.X) GameStatus.X_WON else GameStatus.O_WON
                return Pair(status, winningLine)
            }
        }
        
        // Check columns
        for (col in 0..2) {
            if (board[0][col] == lastPlayer && 
                board[1][col] == lastPlayer && 
                board[2][col] == lastPlayer) {
                val winningLine = listOf(Pair(0, col), Pair(1, col), Pair(2, col))
                val status = if (lastPlayer == Player.X) GameStatus.X_WON else GameStatus.O_WON
                return Pair(status, winningLine)
            }
        }
        
        // Check diagonal (top-left to bottom-right)
        if (board[0][0] == lastPlayer && 
            board[1][1] == lastPlayer && 
            board[2][2] == lastPlayer) {
            val winningLine = listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
            val status = if (lastPlayer == Player.X) GameStatus.X_WON else GameStatus.O_WON
            return Pair(status, winningLine)
        }
        
        // Check diagonal (top-right to bottom-left)
        if (board[0][2] == lastPlayer && 
            board[1][1] == lastPlayer && 
            board[2][0] == lastPlayer) {
            val winningLine = listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
            val status = if (lastPlayer == Player.X) GameStatus.X_WON else GameStatus.O_WON
            return Pair(status, winningLine)
        }
        
        // Check for draw
        if (board.all { row -> row.all { it != null } }) {
            return Pair(GameStatus.DRAW, null)
        }
        
        // Game still in progress
        return Pair(GameStatus.IN_PROGRESS, null)
    }
}
