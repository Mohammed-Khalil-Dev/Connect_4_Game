package com.example.connect4game.model

class GameMatrix {
    private val grid: MutableList<MutableList<Piece>> = MutableList(BoardConfig.NUMBER_OF_COLUMNS) {
        MutableList(BoardConfig.NUMBER_OF_ROWS) { Piece.EMPTY }
    }


    fun getPiece(row: Int, col: Int): Piece {
        return grid[col][row]
    }


    fun dropPiece(col: Int, piece: Piece) {
        for (i in grid[col].lastIndex downTo 0) {
            if (grid[col][i] == Piece.EMPTY) {
                grid[col][i] = piece
                break
            }

        }
    }

    fun getBoard(): List<List<Piece>> {
        return grid
    }
}