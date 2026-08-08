package com.example.connect4game.ui.screens

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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.connect4game.model.game.GameMatrix
import com.example.connect4game.model.game.GameState
import com.example.connect4game.model.game.GameStateDetails
import com.example.connect4game.model.game.GameType
import com.example.connect4game.model.game.GameUiState
import com.example.connect4game.model.game.Piece
import com.example.connect4game.model.settings.audio.Sound
import com.example.connect4game.model.settings.audio.SoundManager
import com.example.connect4game.model.game.checkGameState
import com.example.connect4game.ui.components.BoardGrid


@Composable
fun GameScreen(gameType: GameType,
               paddingValues: PaddingValues = PaddingValues(0.dp)) {
    val context = LocalContext.current

    val soundManager = remember { SoundManager(context) }
    Column(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        when(gameType) {
            GameType.SINGLE_PLAYER -> SinglePlayerGameScreen(soundManager = soundManager)
            GameType.TWO_PLAYER -> TwoPlayerGameScreen(soundManager = soundManager)
        }
    }


}
@Composable
fun SinglePlayerGameScreen(soundManager: SoundManager) {
    //todo: implement single player screen


}
@Composable
fun TwoPlayerGameScreen(soundManager: SoundManager) {

    var uiState by remember { mutableStateOf(GameUiState()) }
    val gameMatrix = remember { GameMatrix() }
    var redWins by remember { mutableIntStateOf(0) }
    var orangeWins by remember { mutableIntStateOf(0) }

    ScoreBoard(redWins = redWins, orangeWins = orangeWins)

    val currentBoard = remember(uiState.boardVersion) {
        gameMatrix.getBoard().map { column -> column.toList() }
    }

    BoardGrid(pieces = currentBoard, selectedColumn = uiState.clickedColIndex, winningCells = uiState.gameStateDetails.winningCells) { newSelectedCol ->
        if (uiState.gameStateDetails.gameState == GameState.IN_PROGRESS) {
            uiState = uiState.copy(clickedColIndex = newSelectedCol)
        }
    }
    GameStatusMessage(gameStateDetails = uiState.gameStateDetails, currentPlayer = uiState.currentPlayer)

    val canPlay = uiState.clickedColIndex != null && currentBoard[uiState.clickedColIndex!!][0] == Piece.EMPTY
            && uiState.gameStateDetails.gameState == GameState.IN_PROGRESS

    PlayTurnButton(currentPlayer = uiState.currentPlayer, canPlay = canPlay) {
        val col = uiState.clickedColIndex ?: return@PlayTurnButton
        val landedRow = gameMatrix.dropPiece(col = col, piece = uiState.currentPlayer)


        val newGameStateDetails = if (landedRow != null) {
            val details: GameStateDetails = checkGameState(gameMatrix, uiState.currentPlayer,
                landedRow, col)

            when(details.gameState) {
                GameState.RED_WON -> redWins++
                GameState.ORANGE_WON -> orangeWins++
                else -> {}
            }
           details
        }
        else {
            uiState.gameStateDetails
        }


        val nextPlayer = if (newGameStateDetails.gameState == GameState.IN_PROGRESS) {
            if (uiState.currentPlayer == Piece.RED) Piece.ORANGE else Piece.RED
        } else {
            uiState.currentPlayer
        }

        soundManager.playSound(Sound.DROP_PIECE)

        uiState = uiState.copy(
            gameStateDetails = newGameStateDetails,
            currentPlayer = nextPlayer,
            boardVersion = uiState.boardVersion + 1,
            clickedColIndex = null
        )
    }

    ResetGameButton {
        gameMatrix.clearBoard()
        uiState = GameUiState()
        uiState = uiState.copy(boardVersion = uiState.boardVersion + 1)
    }
}

@Composable
fun ScoreBoard(redWins: Int, orangeWins: Int) {
    Column {
        Text(stringResource(R.string.red_player_wins_count, redWins), color = Color.Red)
        Text(stringResource(R.string.orange_player_wins_count, orangeWins), color = colorResource(R.color.orange))
    }
}

@Composable
fun GameStatusMessage(gameStateDetails: GameStateDetails, currentPlayer: Piece) {
    when (gameStateDetails.gameState) {
        GameState.ORANGE_WON -> Text(stringResource(R.string.orange_player_wins), color = colorResource(R.color.orange))
        GameState.RED_WON -> Text(stringResource(R.string.red_player_wins), color = Color.Red)
        GameState.DRAW -> Text(stringResource(R.string.it_is_a_draw))
        GameState.IN_PROGRESS -> {
            if (currentPlayer == Piece.RED) {
                Text(stringResource(R.string.red_player_turn), color = Color.Red)
            }
            if (currentPlayer == Piece.ORANGE) {
                Text(stringResource(R.string.orange_player_turn), color = colorResource(R.color.orange))
            }
        }
    }
}

@Composable
fun PlayTurnButton(
    currentPlayer: Piece,
    canPlay: Boolean,
    onPlayTurn: () -> Unit
) {
    Button(
        colors = buttonColors(
            containerColor = if (currentPlayer == Piece.ORANGE) {
                colorResource(R.color.orange)
            } else {
                Color.Red
            }
        ),
        enabled = canPlay,
        onClick = onPlayTurn
    ) {
        Text(stringResource(R.string.play_turn))
    }
}

@Composable
fun ResetGameButton(onReset: () -> Unit) {
    Button(onClick = onReset) {
        Text(stringResource(R.string.reset_game))
    }
}




@Composable
@Preview(showBackground = true)
fun PreviewGameScreen() {
    GameScreen(GameType.SINGLE_PLAYER)
}

