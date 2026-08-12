package com.example.connect4game.model.game.core

import android.util.Log
import com.example.connect4game.data.BotDifficultyManager
import com.example.connect4game.model.game.state.GameState
import com.example.connect4game.model.game.types.Piece
import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

const val SCORE_WIN = 100_000
const val SCORE_THREE_IN_A_ROW = 100
const val SCORE_TWO_IN_A_ROW = 10
const val SCORE_CENTER_COLUMN = 3
const val PLAYER_SCORE_THREE_IN_A_ROW = 500


val humanPiece = Piece.ORANGE
val botPiece = Piece.RED


suspend fun miniMax(
    currentBoard: GameMatrix,
    depth: Int,
    alpha: Int,
    beta: Int,
    isMaximizingPlayer: Boolean
): Int {
    currentCoroutineContext().ensureActive()
    // ideal order (Center first, edges last)
    val preferredOrder = listOf(3, 4, 2, 5, 1, 6, 0)
    val availableColumns: List<Int> = currentBoard.getAvailableColumnsIndex().sortedBy { preferredOrder.indexOf(it) }
    if (currentBoard.isBoardFull()) {
        return 0
    }

    // Depth limit reached: get the total score for this branch(from the leaf node)
    if (depth <= 0) {
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

            val row = currentBoard.dropPiece(col, botPiece) ?: continue
            try {
                val matchState = checkGameState(currentBoard, botPiece, row, col)
                if (matchState.gameState == GameState.RED_WON) {
                    // Add depth so a faster win (higher depth number) is worth more
                    return SCORE_WIN + depth
                }

                // hand the board to the human to see how they respond
                val score = miniMax(currentBoard, depth - 1, currentAlpha, currentBeta, false)
                // after they respond


                bestScore = maxOf(bestScore, score)
                currentAlpha = maxOf(currentAlpha, bestScore)

                if (currentAlpha >= currentBeta) {
                    break // stop checking current branch.
                }
            }
            finally {
                // remove the Bot's test piece to reset the board
                currentBoard.removePiece(row, col)
            }
        }
        //final return from the fun
        return bestScore
    }
    else {
        for (col in availableColumns) {
            val row = currentBoard.dropPiece(col, humanPiece) ?: continue
            try {
                val matchState = checkGameState(currentBoard, humanPiece, row, col)
                if (matchState.gameState == GameState.ORANGE_WON) {
                    // Subtract depth so a faster loss is penalized more heavily
                    return -(SCORE_WIN + depth)
                }


                val score = miniMax(currentBoard, depth - 1, currentAlpha, currentBeta, true)

                bestScore = minOf(bestScore, score)
                currentBeta = minOf(currentBeta, bestScore)

                if (currentAlpha >= currentBeta) break
            }
            finally {
                currentBoard.removePiece(row, col)
            }
        }
        return bestScore
    }


}

suspend fun findBestMove(currentBoard: GameMatrix, botDifficulty: BotDifficulty, isTest: Boolean = false): Int {
    currentCoroutineContext().ensureActive()

    var botTrace: com.google.firebase.perf.metrics.Trace? = null
    if (!isTest) {
        botTrace = Firebase.performance.newTrace("bot_thinking_time")
        botTrace.putAttribute("bot_difficulty", botDifficulty.name)
        botTrace.start()
    }

    val activeDepth = if (!isTest)  BotDifficultyManager.getDepthForDifficulty(botDifficulty = botDifficulty)
    else botDifficulty.depth

    try {
        var bestScore = Int.MIN_VALUE
        var bestColumn = BoardConfig.NUMBER_OF_COLUMNS / 2

        val preferredOrder = listOf(3, 4, 2, 5, 1, 6, 0)
        val availableColumns = currentBoard.getAvailableColumnsIndex().sortedBy { preferredOrder.indexOf(it) }
        for (col in availableColumns) {
            val row = currentBoard.dropPiece(col, botPiece) ?: continue
            try {
                val matchState = checkGameState(currentBoard, botPiece, row, col)
                if (matchState.gameState == GameState.RED_WON) {
                    Log.d("Bot", "Instant win found! Choosing column ${col + 1}")
                    return col
                }

                val score = miniMax(currentBoard, depth = activeDepth, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE, isMaximizingPlayer = false)
                Log.d("Bot", "result for column ${col + 1}: $score")

                if (score > bestScore) {
                    bestScore = score
                    bestColumn = col
                }
            }
            finally {
                currentBoard.removePiece(row, col)
            }
        }
        Log.d("Bot", "best column this turn is ${bestColumn + 1} with score $bestScore")
        return bestColumn
    }
    finally {
        botTrace?.stop()
    }
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