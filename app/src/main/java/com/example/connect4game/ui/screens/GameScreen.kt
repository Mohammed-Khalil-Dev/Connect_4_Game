package com.example.connect4game.ui.screens

import android.media.SoundPool
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.connect4game.R
import com.example.connect4game.model.GameMatrix
import com.example.connect4game.model.GameState
import com.example.connect4game.model.GameType
import com.example.connect4game.model.GameUiState
import com.example.connect4game.model.Piece
import com.example.connect4game.model.checkGameState
import com.example.connect4game.ui.components.BoardGrid

@Composable
fun GameScreen(gameType: GameType,
               paddingValues: PaddingValues = PaddingValues(0.dp)) {
    Column(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        when(gameType) {
            GameType.SINGLE_PLAYER -> SinglePlayerGameScreen()
            GameType.TWO_PLAYER -> TwoPlayerGameScreen()
        }
    }


}
@Composable
fun SinglePlayerGameScreen() {



}
@Composable
fun TwoPlayerGameScreen() {
    val context = LocalContext.current
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(2)
            .build()
    }
    val dropSoundId = remember {
        soundPool.load(context, R.raw.piece_drop_sound, 1)
    }
    var uiState by remember { mutableStateOf(GameUiState()) }
    val gameMatrix = remember { GameMatrix() }
    var redWins by remember { mutableStateOf(0) }
    var orangeWins by remember { mutableStateOf(0) }


    Column {
        Text(stringResource(R.string.red_player_wins_count, redWins), color = Color.Red)
        Text(stringResource(R.string.orange_player_wins_count, orangeWins), color = colorResource(R.color.orange))
    }

    val currentBoard = remember(uiState.boardVersion) {
        gameMatrix.getBoard().map { column -> column.toList() }
    }

    BoardGrid(pieces = currentBoard, selectedColumn = uiState.clickedColIndex, winningCells = uiState.gameStateDetails.winningCells) { newSelectedCol ->
        if (uiState.gameStateDetails.gameState == GameState.IN_PROGRESS) {
            uiState = uiState.copy(clickedColIndex = newSelectedCol)
        }
    }
    when (uiState.gameStateDetails.gameState) {
        GameState.ORANGE_WON ->  Text(stringResource(R.string.orange_player_wins), color = colorResource(R.color.orange))
        GameState.RED_WON -> Text(stringResource(R.string.red_player_wins),  color = Color.Red)
        GameState.DRAW ->  Text(stringResource(R.string.it_is_a_draw))
        GameState.IN_PROGRESS -> {
            if (uiState.currentPlayer == Piece.RED) {
                Text(stringResource(R.string.red_player_turn),  color = Color.Red)
            }
            if (uiState.currentPlayer == Piece.ORANGE) {
                Text(stringResource(R.string.orange_player_turn),  color = colorResource(R.color.orange))
            }
        }
    }









    val canPlay = uiState.clickedColIndex != null && currentBoard[uiState.clickedColIndex!!][0] == Piece.EMPTY
            && uiState.gameStateDetails.gameState == GameState.IN_PROGRESS

    Button(colors = buttonColors(
        containerColor = if (uiState.currentPlayer == Piece.ORANGE) {
            colorResource(R.color.orange)
        } else {
            Color.Red
        }
    ), enabled = canPlay, onClick = {
        val col = uiState.clickedColIndex ?: return@Button
        val landedRow = gameMatrix.dropPiece(col = col, piece = uiState.currentPlayer)


        val newGameStateDetails = if (landedRow != null) {
            val details = checkGameState(gameMatrix, uiState.currentPlayer, landedRow, col)

            if (details.gameState == GameState.RED_WON) {
                redWins++
            }
            else if (details.gameState == GameState.ORANGE_WON) {
                orangeWins++
            }
            details
        } else {
            uiState.gameStateDetails
        }

        soundPool.play(dropSoundId, 1f, 1f, 0, 0, 1f)

        val nextPlayer = if (newGameStateDetails.gameState == GameState.IN_PROGRESS) {
            if (uiState.currentPlayer == Piece.RED) Piece.ORANGE else Piece.RED
        } else {
            uiState.currentPlayer
        }


        uiState = uiState.copy(
            gameStateDetails = newGameStateDetails,
            currentPlayer = nextPlayer,
            boardVersion = uiState.boardVersion + 1,
            clickedColIndex = null
        )
    }) {
        Text(stringResource(R.string.play_turn))
    }

    Button(onClick = {

        gameMatrix.clearBoard()
        uiState = GameUiState()
        uiState = uiState.copy(boardVersion = uiState.boardVersion + 1)
    }) {
        Text(stringResource(R.string.reset_game))
    }
}


@Composable
@Preview(showBackground = true)
fun PreviewGameScreen() {
    GameScreen(GameType.TWO_PLAYER)
}

