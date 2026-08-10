package com.example.connect4game.model.game.core

import com.example.connect4game.model.game.types.Piece

class GameMatrix {
    private val grid: MutableList<MutableList<Piece>> = MutableList(BoardConfig.NUMBER_OF_COLUMNS) {
        MutableList(BoardConfig.NUMBER_OF_ROWS) { Piece.EMPTY }
    }


    fun getPiece(row: Int, col: Int): Piece {
        return grid[col][row]
    }


    fun clearBoard() {
        for (col in grid.indices) {
            for (row in grid[col].indices) {
                grid[col][row] = Piece.EMPTY
            }
        }
    }


    fun dropPiece(col: Int, piece: Piece): Int?{
        for (i in grid[col].lastIndex downTo 0) {
            if (grid[col][i] == Piece.EMPTY) {
                grid[col][i] = piece
                return i
            }

        }
        return null
    }

    fun isBoardFull(): Boolean {
        return grid.indices.none { colIndex -> getPiece(row = 0, colIndex) == Piece.EMPTY }
    }

    fun getAvailableColumnsIndex(): List<Int> {
        val availableColumns = grid.indices.filter { colIndex ->
            getPiece(row = 0, col = colIndex) == Piece.EMPTY
        }
        return availableColumns
    }

    fun removePiece(row: Int, col: Int) {
        grid[col][row] = Piece.EMPTY
    }

    fun getBoard(): List<List<Piece>> {
        return grid
    }
}