package com.example.connect4game.model.game.state

data class BoardEvaluationResult(
    val gameState: GameState = GameState.IN_PROGRESS,
    val winningCells: List<Pair<Int, Int>> = emptyList()
)