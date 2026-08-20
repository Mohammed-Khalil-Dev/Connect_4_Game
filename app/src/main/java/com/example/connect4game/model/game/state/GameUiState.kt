package com.example.connect4game.model.game.state

import com.example.connect4game.data.BotDifficultyManager
import com.example.connect4game.data.BotPieceColorManager
import com.example.connect4game.model.game.core.BotDifficulty
import com.example.connect4game.model.game.types.Piece

data class GameUiState(
    val currentPlayer: Piece = if (BotPieceColorManager.DEFAULT_BOT_PIECE_COLOR == Piece.RED.name) Piece.ORANGE else Piece.RED,
    val boardEvaluationResult: BoardEvaluationResult = BoardEvaluationResult(
        GameState.IN_PROGRESS,
        winningCells = emptyList()
    ),
    val clickedColIndex: Int? = null,
    val boardVersion: Int = 0,
    val currentBotDifficulty: BotDifficulty = BotDifficultyManager.DEFAULT_BOT_DIFFICULTY,
    val botPieceColor: Piece = Piece.valueOf(BotPieceColorManager.DEFAULT_BOT_PIECE_COLOR),
    val redWins: Int = 0,
    val orangeWins: Int = 0
)