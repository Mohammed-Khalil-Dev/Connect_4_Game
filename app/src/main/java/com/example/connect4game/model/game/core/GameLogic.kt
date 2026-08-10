package com.example.connect4game.model.game.core

import com.example.connect4game.model.game.state.GameState
import com.example.connect4game.model.game.state.GameStateDetails
import com.example.connect4game.model.game.types.Piece

const val CONNECTED_PIECES_TO_WIN = 4


fun checkGameState(currentBoard: GameMatrix, currentPiece: Piece, currentRow: Int, currentCol: Int): GameStateDetails {
    val upCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.UP)
    val downCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.DOWN)
    val verticalCells = upCells + downCells + Pair(currentRow, currentCol)

    val leftCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.LEFT)
    val rightCells = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.RIGHT)
    val horizontalCells = leftCells + rightCells + Pair(currentRow, currentCol)

    val diagUpLeft = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.UP_LEFT)
    val diagDownRight = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.DOWN_RIGHT)
    val diagonal1Cells = diagUpLeft + diagDownRight + Pair(currentRow, currentCol)

    val diagDownLeft = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.DOWN_LEFT)
    val diagUpRight = getCellsInDirection(currentBoard, currentPiece, currentRow, currentCol, direction = Direction.UP_RIGHT)
    val diagonal2Cells = diagDownLeft + diagUpRight + Pair(currentRow, currentCol)

    val winningState = if (currentPiece == Piece.RED) GameState.RED_WON else GameState.ORANGE_WON

    if (verticalCells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameStateDetails(winningState, verticalCells)
    }
    if (horizontalCells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameStateDetails(winningState, horizontalCells)
    }
    if (diagonal1Cells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameStateDetails(winningState, diagonal1Cells)
    }
    if (diagonal2Cells.size >= CONNECTED_PIECES_TO_WIN) {
        return GameStateDetails(winningState, diagonal2Cells)
    }



    if (currentBoard.isBoardFull()) {
        return GameStateDetails(GameState.DRAW, emptyList())
    }

    return GameStateDetails(GameState.IN_PROGRESS, emptyList())
}

/**
 * Collects consecutive matching coordinate pairs outward from a starting anchor position in a specified direction.
 *
 * @param currentBoard The game board matrix containing the grid state.
 * @param piece The specific piece type (color) being checked for a match.
 * @param startRow The starting row index where the piece was dropped.
 * @param startCol The starting column index where the piece was dropped.
 * @param maxConnections The maximum number of consecutive steps to check (defaults to CONNECTED_PIECES_TO_WIN).
 * @param direction The Direction enum dictating the row and column step adjustments.
 * @return A list of row/column pairs for matching consecutive pieces found stepping in that direction.
 */
fun getCellsInDirection(
    currentBoard: GameMatrix,
    piece: Piece,
    startRow: Int,
    startCol: Int,
    maxConnections: Int = CONNECTED_PIECES_TO_WIN,
    direction: Direction
): List<Pair<Int, Int>> {
    val winningCells: MutableList<Pair<Int, Int>> = mutableListOf()
    // Check steps away from the starting piece
    for (step in 1 until maxConnections) {
        val row = startRow + (direction.rowDelta * step)
        val col = startCol + (direction.colDelta * step)

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



