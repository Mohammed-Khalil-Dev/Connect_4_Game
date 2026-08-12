package com.example.connect4game.model.game.core

enum class BotDifficulty(val depth: Int, val remoteKey: String) {
    EASY(depth = 0, remoteKey = "bot_depth_easy"),
    MEDIUM(depth = 1, remoteKey = "bot_depth_medium"),
    HARD(depth = 3, remoteKey = "bot_depth_hard")
}