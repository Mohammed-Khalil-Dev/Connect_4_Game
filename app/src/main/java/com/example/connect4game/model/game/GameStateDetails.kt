package com.example.connect4game.model.game

data class GameStateDetails(
    val gameState: GameState = GameState.IN_PROGRESS,
    val winningCells: List<Pair<Int, Int>> = emptyList()
)