package com.example.connect4game.model.game.state

import com.example.connect4game.model.game.types.Piece

data class GameUiState(
    val currentPlayer: Piece = Piece.ORANGE,
    val gameStateDetails: GameStateDetails = GameStateDetails(
        GameState.IN_PROGRESS,
        winningCells = emptyList()
    ),
    val clickedColIndex: Int? = null,
    val boardVersion: Int = 0,
    )