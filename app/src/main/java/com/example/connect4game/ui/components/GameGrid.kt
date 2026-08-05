package com.example.connect4game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.connect4game.model.BoardConfig
import com.example.connect4game.model.Piece

@Composable
fun BoardGrid(pieces: List<List<Piece>>) {

    Row(Modifier.fillMaxWidth().background(Color.Blue)) {

        pieces.forEach { columnList ->
            // all columns set to weight 1 so they take 1 / 7 of row
            Column(modifier = Modifier.weight(1f)) {
                columnList.forEach { piece ->
                    BoardSlot(piece)
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
                1 if rowIndex == 5 -> Piece.YELLOW
                else -> Piece.EMPTY
            }
        }
    }
    BoardGrid(mockBoardData)
}