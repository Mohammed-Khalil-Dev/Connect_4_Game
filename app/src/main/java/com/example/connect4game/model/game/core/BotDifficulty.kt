package com.example.connect4game.model.game.core

enum class BotDifficulty(val depth: Int, val remoteKey: String) {
    EASY(depth = 1, remoteKey = "bot_depth_easy"),
    MEDIUM(depth = 2, remoteKey = "bot_depth_medium"),
    HARD(depth = 4, remoteKey = "bot_depth_hard")
}