package com.example.connect4game.model


data class GameUiState(
    val currentPlayer: Piece = listOf(Piece.RED, Piece.ORANGE).random(),
    val gameStateDetails: GameStateDetails = GameStateDetails(GameState.IN_PROGRESS, winningCells = emptyList()),
    val clickedColIndex: Int? = null,
    val boardVersion: Int = 0,


    )