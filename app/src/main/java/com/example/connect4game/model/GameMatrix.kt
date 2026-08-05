package com.example.connect4game.model

class GameMatrix {

    private val grid: List<List<Piece>> = List(BoardConfig.NUMBER_OF_COLUMNS) {
        List(size = BoardConfig.NUMBER_OF_ROWS) { rowIndex ->
            Piece.EMPTY
        }
    }


    fun getPiece(row: Int, col: Int): Piece {
        return grid[col][row]
    }

    // Easy function to handle a player's move
    fun dropPiece(col: Int, piece: Piece) {
        // logic to find the lowest empty slot in this specific column
    }
}