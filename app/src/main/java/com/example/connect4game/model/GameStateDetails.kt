package com.example.connect4game.model
data class GameStateDetails(
    val gameState: GameState,
    val winningCells: List<Pair<Int, Int>> = emptyList()
)
