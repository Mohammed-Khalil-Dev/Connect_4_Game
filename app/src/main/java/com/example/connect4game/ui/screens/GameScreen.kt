package com.example.connect4game.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.connect4game.model.GameMatrix
import com.example.connect4game.model.GameType
import com.example.connect4game.ui.components.BoardGrid

@Composable
fun GameScreen(gameType: GameType,
               paddingValues: PaddingValues = PaddingValues(0.dp)) {
    Column(modifier = Modifier.padding(paddingValues)) {
        when(gameType) {
            GameType.SINGLE_PLAYER -> SinglePlayerGameScreen()
            GameType.TWO_PLAYER -> TwoPlayerGameScreen()
        }
    }


}
@Composable
fun SinglePlayerGameScreen() {
    val gameMatrix = remember { GameMatrix() }

    var boardVersion by remember { mutableIntStateOf(0) }
    Row {
        Text("Player 1 wins: 0")
        Spacer(modifier = Modifier.width(16.dp))
        Text("Player 2 wins: 0")
    }
    Spacer(modifier = Modifier.height(24.dp))
    val currentBoard = remember(boardVersion) { gameMatrix.getBoard() }
    BoardGrid(pieces = currentBoard) {clickedColIndex ->

    }


}
@Composable
fun TwoPlayerGameScreen() {

}

@Composable
@Preview(showBackground = true)
fun PreviewGameScreen() {
    GameScreen(GameType.SINGLE_PLAYER)
}

