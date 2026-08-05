package com.example.connect4game.model
data class GameCheckResult(
    val gameState: GameState,
    val winningCells: List<Pair<Int, Int>> = emptyList()
)
