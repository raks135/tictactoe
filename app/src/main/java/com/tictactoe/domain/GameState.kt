package com.tictactoe.domain

/**
 * Represents a player in the game
 */
enum class Player {
    X, O;
    
    fun other(): Player = if (this == X) O else X
}

/**
 * Represents the current status of the game
 */
enum class GameStatus {
    IN_PROGRESS,
    X_WON,
    O_WON,
    DRAW
}

/**
 * Represents the game mode
 */
enum class GameMode {
    SINGLE_PLAYER,  // Player vs AI
    TWO_PLAYER      // Player vs Player
}

/**
 * Represents AI difficulty level
 */
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

/**
 * Represents a move on the board
 */
data class Move(val row: Int, val col: Int, val player: Player)

/**
 * Represents the complete game state
 */
data class GameState(
    val board: List<List<Player?>> = List(3) { List(3) { null } },
    val currentPlayer: Player = Player.X,
    val status: GameStatus = GameStatus.IN_PROGRESS,
    val moveHistory: List<Move> = emptyList(),
    val winningLine: List<Pair<Int, Int>>? = null
) {
    /**
     * Get the cell value at the specified position
     */
    fun getCell(row: Int, col: Int): Player? = board[row][col]
    
    /**
     * Check if a cell is empty
     */
    fun isCellEmpty(row: Int, col: Int): Boolean = board[row][col] == null
    
    /**
     * Get all empty cells
     */
    fun getEmptyCells(): List<Pair<Int, Int>> {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (row in 0..2) {
            for (col in 0..2) {
                if (isCellEmpty(row, col)) {
                    emptyCells.add(Pair(row, col))
                }
            }
        }
        return emptyCells
    }
    
    /**
     * Check if the board is full
     */
    fun isBoardFull(): Boolean = board.all { row -> row.all { it != null } }
}
