package com.example.connect4game.model.game.state

import com.example.connect4game.data.BotPieceColorManager
import com.example.connect4game.model.game.types.Piece

data class GameUiState(
    val currentPlayer: Piece = if (BotPieceColorManager.DEFAULT_BOT_PIECE_COLOR == Piece.RED.name) Piece.ORANGE else Piece.RED,
    val gameStateDetails: GameStateDetails = GameStateDetails(
        GameState.IN_PROGRESS,
        winningCells = emptyList()
    ),
    val clickedColIndex: Int? = null,
    val boardVersion: Int = 0,
    )