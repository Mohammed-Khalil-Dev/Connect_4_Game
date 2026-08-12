package com.example.connect4game.model.game.core

enum class BotDifficulty(val depth: Int) {
    EASY(depth = 1),
    MEDIUM(depth = 2),
    HARD(depth = 4)
}