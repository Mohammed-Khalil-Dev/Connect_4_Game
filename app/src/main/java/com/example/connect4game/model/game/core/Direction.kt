package com.example.connect4game.model.game.core

enum class Direction(val rowDelta: Int, val colDelta: Int) {
    UP(rowDelta = -1, colDelta = 0),
    DOWN(rowDelta = 1, colDelta = 0),
    LEFT(rowDelta = 0, colDelta = -1),
    RIGHT(rowDelta = 0, colDelta = 1),
    UP_LEFT(rowDelta = -1, colDelta = -1),
    UP_RIGHT(rowDelta = -1, colDelta = 1),
    DOWN_LEFT(rowDelta = 1, colDelta = -1),
    DOWN_RIGHT(rowDelta = 1, colDelta = 1)
}