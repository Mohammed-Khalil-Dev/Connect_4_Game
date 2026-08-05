package com.example.connect4game.model


data class GameUiState(
    val currentPlayer: Piece = Piece.RED,
    val gameState: GameState = GameState.IN_PROGRESS,
    val clickedColIndex: Int? = null,
    val boardVersion: Int = 0,
    val winningCells: List<Pair<Int, Int>> = emptyList()
)