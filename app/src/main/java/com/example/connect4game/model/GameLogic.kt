package com.example.connect4game.model

const val CONNECTED_PIECES_TO_WIN = 4

const val DIRECTION_UP = -1
const val DIRECTION_DOWN = 1
const val DIRECTION_LEFT = -1
const val DIRECTION_RIGHT = 1
const val DIRECTION_NONE = 0
fun checkGameState(currentBoard: GameMatrix, currentPiece: Piece, currentRow: Int, currentCol: Int): GameCheckResult {
    val upCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_UP, DIRECTION_NONE)
    val downCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_DOWN, DIRECTION_NONE)
    val verticalCells = upCells + downCells + Pair(currentRow, currentCol)

    val leftCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_NONE, DIRECTION_LEFT)
    val rightCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_NONE, DIRECTION_RIGHT)
    val horizontalCells = leftCells + rightCells + Pair(currentRow, currentCol)

    val diagUpLeft = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_UP, DIRECTION_LEFT)
    val diagDownRight = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_DOWN, DIRECTION_RIGHT)
    val diagonal1Cells = diagUpLeft + diagDownRight + Pair(currentRow, currentCol)

    val diagDownLeft = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_DOWN, DIRECTION_LEFT)
    val diagUpRight = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, DIRECTION_UP, DIRECTION_RIGHT)
    val diagonal2Cells = diagDownLeft + diagUpRight + Pair(currentRow, currentCol)

    val winningState = if (currentPiece == Piece.RED) GameState.RED_WON else GameState.YELLOW_WON

    if (verticalCells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameCheckResult(winningState, verticalCells)
    }
    if (horizontalCells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameCheckResult(winningState, horizontalCells)
    }
    if (diagonal1Cells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameCheckResult(winningState, diagonal1Cells)
    }
    if (diagonal2Cells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameCheckResult(winningState, diagonal2Cells)
    }

    var isBoardFull = true
    for (col in 0 until BoardConfig.NUMBER_OF_COLUMNS) {
        if (currentBoard.getPiece(0, col) == Piece.EMPTY) {
            isBoardFull = false
            break
        }
    }

    if (isBoardFull) {
        return GameCheckResult(GameState.DRAW, emptyList())
    }

    return GameCheckResult(GameState.IN_PROGRESS, emptyList())
}

/**
 * Collects consecutive matching coordinate pairs outward from a starting anchor position in a specified direction.
 *
 * @param currentBoard The game board matrix containing the grid state.
 * @param piece The specific piece type (color) being checked for a match.
 * @param startRow The starting row index where the piece was dropped.
 * @param startCol The starting column index where the piece was dropped.
 * @param rowDelta The row step direction (-1 for up, 0 for none, 1 for down).
 * @param colDelta The column step direction (-1 for left, 0 for none, 1 for right).
 * @return A list of row/column pairs for matching consecutive pieces found stepping in that direction.
 */
fun getCellsInDirection(
    currentBoard: GameMatrix,
    piece: Piece,
    startRow: Int,
    startCol: Int,
    rowDelta: Int = 0,
    colDelta: Int = 0
): List<Pair<Int, Int>> {
    val winningCells: MutableList<Pair<Int, Int>> = mutableListOf()
    // Check steps away from the starting piece
    for (step in 1 until CONNECTED_PIECES_TO_WIN) {
        val row = startRow + (rowDelta * step)
        val col = startCol + (colDelta * step)

        // Make sure we stay inside the board bounds
        if (row in 0 until BoardConfig.NUMBER_OF_ROWS &&
            col in 0 until BoardConfig.NUMBER_OF_COLUMNS &&
            currentBoard.getPiece(row, col) == piece) {
            winningCells.add(Pair(row, col))
        }
        else {
            break
        }
    }
    return winningCells.toList()
}



