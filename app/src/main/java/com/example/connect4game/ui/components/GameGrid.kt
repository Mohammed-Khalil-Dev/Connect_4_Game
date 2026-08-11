package com.example.connect4game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.connect4game.model.game.core.BoardConfig
import com.example.connect4game.model.game.types.Piece

@Composable
fun BoardGrid(pieces: List<List<Piece>>,
              selectedColumn: Int?,
              winningCells: List<Pair<Int, Int>>,
              isColumnClickable: Boolean = true,
              onColumnClick: (Int) -> Unit

) {
    Row(Modifier.fillMaxWidth().background(Color(0xFF3A1C5E))) {

        pieces.forEachIndexed { colIndex, columnList ->


            val columnColor = if (colIndex == selectedColumn) {
                Color.White.copy(alpha = 0.3f)
            } else {
                Color.Transparent
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = isColumnClickable) {
                        onColumnClick(colIndex)
                    }
                    // Draw the slots first, then paint the highlight rectangle over them
                    .drawWithContent {
                        drawContent()
                        drawRect(color = columnColor)
                    }
            ) {
                columnList.forEachIndexed { rowIndex, piece ->
                    val isWinningCell = winningCells.contains(Pair(rowIndex, colIndex))

                    BoardSlot(piece = piece, isWinning = isWinningCell)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardGrid() {
    val mockBoardData = List(BoardConfig.NUMBER_OF_COLUMNS) { columnIndex ->
        List(BoardConfig.NUMBER_OF_ROWS) { rowIndex ->

            when (columnIndex) {
                0 if rowIndex == 5 -> Piece.RED
                1 if rowIndex == 5 -> Piece.ORANGE
                else -> Piece.EMPTY
            }
        }
    }

    val mockWinningCells = listOf(Pair(5, 0))

    BoardGrid(
        pieces = mockBoardData,
        selectedColumn = 2,
        winningCells = mockWinningCells,
        onColumnClick = {}
    )
}