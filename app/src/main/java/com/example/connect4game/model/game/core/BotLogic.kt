package com.example.connect4game.model.game.core

import android.util.Log
import com.example.connect4game.model.game.state.GameState
import com.example.connect4game.model.game.types.Piece

const val SCORE_WIN = 100_000
const val SCORE_THREE_IN_A_ROW = 100
const val SCORE_TWO_IN_A_ROW = 10
const val SCORE_CENTER_COLUMN = 3
const val PLAYER_SCORE_THREE_IN_A_ROW = 500
var maxDepth = BotDifficulty.MEDIUM.depth

val humanPiece = Piece.ORANGE
val botPiece = Piece.RED


fun miniMax(
    currentBoard: GameMatrix,
    depth: Int,
    alpha: Int,
    beta: Int,
    isMaximizingPlayer: Boolean
): Int {
    val availableColumns: List<Int> = currentBoard.getAvailableColumnsIndex()
    if (currentBoard.isBoardFull()) {
        return 0
    }

    // Depth limit reached: get the total score for this branch(from the leaf node)
    if (depth == 0) {
        return evaluateBoard(gameMatrix = currentBoard)
    }

    // Alpha: the best score for the bot in this branch so far
    var currentAlpha = alpha
    // Beta: the best score for the player in this branch so far
    var currentBeta = beta
    // Tracks the absolute best score found in all nodes(columns)
    var bestScore = if (isMaximizingPlayer) Int.MIN_VALUE else Int.MAX_VALUE


    if (isMaximizingPlayer) {
        // test dropping in all columns
        for (col in availableColumns) {

            val row = currentBoard.dropPiece(col, botPiece)!!
            val matchState = checkGameState(currentBoard, botPiece, row, col)
            if (matchState.gameState == GameState.RED_WON) {
                currentBoard.removePiece(row, col)
                return SCORE_WIN
            }

            // hand the board to the human to see how they respond
            val score = miniMax(currentBoard, depth - 1, currentAlpha, currentBeta, false)
            // after they respond
            // remove the Bot's test piece to reset the board
            currentBoard.removePiece(row, col)

            bestScore = maxOf(bestScore, score)
            currentAlpha = maxOf(currentAlpha, bestScore)

            if (currentAlpha >= currentBeta) {
                break // stop checking current branch.
            }
        }
        //final return from the fun
        return bestScore
    }
    else {
        for (col in availableColumns) {
            val row = currentBoard.dropPiece(col, humanPiece) ?: continue
            val matchState = checkGameState(currentBoard, humanPiece, row, col)
            if (matchState.gameState == GameState.ORANGE_WON) {
                currentBoard.removePiece(row, col)
                return -SCORE_WIN
            }


            val score = miniMax(currentBoard, depth - 1, currentAlpha, currentBeta, true)
            currentBoard.removePiece(row, col)

            bestScore = minOf(bestScore, score)
            currentBeta = minOf(currentBeta, bestScore)

            if (currentAlpha >= currentBeta) break
        }
        return bestScore
    }


}

fun findBestMove(currentBoard: GameMatrix): Int {
    var bestScore = Int.MIN_VALUE
    var bestColumn = BoardConfig.NUMBER_OF_COLUMNS / 2

    for (col in currentBoard.getAvailableColumnsIndex()) {
        val row = currentBoard.dropPiece(col, botPiece) ?: continue
        val matchState = checkGameState(currentBoard, botPiece, row, col)
        if (matchState.gameState == GameState.RED_WON) {
            currentBoard.removePiece(row, col)
            Log.d("Bot", "Instant win found! Choosing column ${col + 1}")
            return col
        }

        val score = miniMax(currentBoard, depth = maxDepth, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE, isMaximizingPlayer = false)
        Log.d("Bot", "result for column ${col + 1}: $score")

        currentBoard.removePiece(row, col)

        if (score > bestScore) {
            bestScore = score
            bestColumn = col
        }
    }
    Log.d("Bot", "best column this turn is ${bestColumn + 1} with score $bestScore")
    return bestColumn
}

fun evaluateBoard(gameMatrix: GameMatrix): Int {
    var totalScore = 0


    for (row in 0 until BoardConfig.NUMBER_OF_ROWS) {
        for (col in 0 until BoardConfig.NUMBER_OF_COLUMNS) {

            val rightWindow = getWindow(gameMatrix, startRow = row, startCol = col, Direction.RIGHT)
            if (rightWindow.isNotEmpty()) {
                totalScore += scoreWindow(rightWindow)
            }

            val downWindow = getWindow(gameMatrix, startRow = row, startCol = col, Direction.DOWN)
            if (downWindow.isNotEmpty()) {
                totalScore += scoreWindow(downWindow)
            }

            val downRightWindow = getWindow(gameMatrix, startRow = row, startCol = col, Direction.DOWN_RIGHT)
            if (downRightWindow.isNotEmpty()) {
                totalScore += scoreWindow(downRightWindow)
            }

            val downLeftWindow = getWindow(gameMatrix, startRow = row, startCol = col, Direction.DOWN_LEFT)
            if (downLeftWindow.isNotEmpty()) {
                totalScore += scoreWindow(downLeftWindow)
            }
        }
    }

    var centerPiecesCount = 0
    val centerColumnIndex = BoardConfig.NUMBER_OF_COLUMNS / 2


    for (row in 0 until BoardConfig.NUMBER_OF_ROWS) {
        if (gameMatrix.getPiece(row, centerColumnIndex) == botPiece) {
            centerPiecesCount++
        }
    }

    totalScore += centerPiecesCount * SCORE_CENTER_COLUMN

    return totalScore
}

fun scoreWindow(window: List<Piece>): Int {
    var score = 0


    val botCount = window.count { it == botPiece }
    val humanCount = window.count { it == humanPiece }
    val emptyCount = window.count { it == Piece.EMPTY }

    when (botCount) {
        4 -> score += SCORE_WIN
        3 -> {
            if (emptyCount == 1) score += SCORE_THREE_IN_A_ROW
        }
        2 -> {
            if (emptyCount == 2) score += SCORE_TWO_IN_A_ROW
        }
    }



    if (humanCount == 3 && emptyCount == 1) {
        score -= PLAYER_SCORE_THREE_IN_A_ROW
    }

    return score

}

fun getWindow(
    gameMatrix: GameMatrix,
    startRow: Int,
    startCol: Int,
    direction: Direction,
    maxConnections: Int = 4
): List<Piece> {
    val window = mutableListOf<Piece>()


    for (step in 0 until maxConnections) {
        val row = startRow + (direction.rowDelta * step)
        val col = startCol + (direction.colDelta * step)
        // return empty list unless there are <maxConnections> connected Pieces
        if (row in 0 until BoardConfig.NUMBER_OF_ROWS &&
            col in 0 until BoardConfig.NUMBER_OF_COLUMNS) {
            window.add(gameMatrix.getPiece(row, col))
        }
        else {
            return emptyList()
        }
    }
    return window.toList()
}