package com.example.connect4game.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.connect4game.R
import com.example.connect4game.model.game.core.BotDifficulty
import com.example.connect4game.model.game.state.BoardEvaluationResult
import com.example.connect4game.model.game.state.GameState
import com.example.connect4game.model.game.types.GameType
import com.example.connect4game.model.game.types.Piece
import com.example.connect4game.ui.components.BoardGrid
import com.example.connect4game.ui.viewmodels.GameScreenViewModel


@Composable
fun GameScreen(
    gameType: GameType,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: GameScreenViewModel
) {

    Column(modifier = Modifier
        .padding(paddingValues).padding(start = 10.dp, end = 10.dp)
        .fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        when(gameType) {
            GameType.SINGLE_PLAYER -> SinglePlayerGameScreen(
                viewModel = viewModel
            )
            GameType.TWO_PLAYER -> TwoPlayerGameScreen(viewModel = viewModel)
        }
    }
}
@Composable
fun SinglePlayerGameScreen(
    viewModel: GameScreenViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val botPiece: Piece = uiState.botPieceColor
    val playerPiece: Piece = if (botPiece == Piece.RED) Piece.ORANGE else Piece.RED

    val gameMatrix = viewModel.gameMatrix
    val playerWins = if (playerPiece == Piece.RED) uiState.redWins else uiState.orangeWins
    val botWins = if (botPiece == Piece.RED) uiState.redWins else uiState.orangeWins
    val currentBotDifficulty: BotDifficulty = uiState.currentBotDifficulty
    val isBotThinking = uiState.currentPlayer == botPiece &&
            uiState.boardEvaluationResult.gameState == GameState.IN_PROGRESS

    LaunchedEffect(key1 = playerPiece) {
        viewModel.resetGame(startingPlayer = playerPiece)
    }

    ScoreBoard(
        redWins = if (botPiece == Piece.RED) botWins else playerWins,
        orangeWins = if (botPiece == Piece.ORANGE) botWins else playerWins,
        playerOneLabelId = if (botPiece == Piece.RED) R.string.bot_wins_count else R.string.you_wins_count,
        playerTwoLabelId = if (botPiece == Piece.ORANGE) R.string.bot_wins_count else R.string.you_wins_count
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(horizontalArrangement = Arrangement.Center) {
        Text(text = stringResource(R.string.bot_difficulty) + " ", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = when (currentBotDifficulty) {
                BotDifficulty.EASY -> stringResource(R.string.easy)
                BotDifficulty.MEDIUM -> stringResource(R.string.medium)
                BotDifficulty.HARD -> stringResource(R.string.hard)
            },
            color = when (currentBotDifficulty) {
                BotDifficulty.EASY -> Color.Green.copy(alpha = 0.7f)
                BotDifficulty.MEDIUM -> Color.Yellow.copy(alpha = 0.7f)
                BotDifficulty.HARD -> Color.Red
            }
        )
    }




    val currentBoard = remember(key1 = uiState.boardVersion) {
        gameMatrix.getBoard().map { column -> column.toList() }
    }
    BoardGrid(pieces = currentBoard, selectedColumn = if (isBotThinking) null else uiState.clickedColIndex
        , winningCells = uiState.boardEvaluationResult.winningCells,
        isColumnClickable = !isBotThinking) { newSelectedCol ->
        if (uiState.boardEvaluationResult.gameState == GameState.IN_PROGRESS) {
            viewModel.updateUiState(newSelectedColumn = newSelectedCol)
        }
    }

    GameStatusMessage(
        boardEvaluationResult = uiState.boardEvaluationResult,
        currentPlayer = uiState.currentPlayer,
        orangePlayerTurnId = if (playerPiece == Piece.ORANGE) R.string.your_turn else R.string.bots_turn,
        redPlayerTurnId = if (playerPiece == Piece.RED) R.string.your_turn else R.string.bots_turn,
        orangePlayerWonId = if (playerPiece == Piece.ORANGE) R.string.you_win else R.string.bot_wins,
        redPlayerWonId = if (playerPiece == Piece.RED) R.string.you_win else R.string.bot_wins
    )



    val canPlayerPlay = uiState.clickedColIndex != null && currentBoard[uiState.clickedColIndex!!][0] == Piece.EMPTY
            && uiState.boardEvaluationResult.gameState == GameState.IN_PROGRESS && uiState.currentPlayer == playerPiece

    PlayTurnButton(currentPlayer = playerPiece, canPlay = canPlayerPlay) {
        val col = uiState.clickedColIndex ?: return@PlayTurnButton
        viewModel.onPieceDrop(col = col)
    }

    Spacer(modifier = Modifier.height(40.dp))
    ResetGameButton(canReset = !isBotThinking) { viewModel.resetGame(startingPlayer = playerPiece) }
}
@Composable
fun TwoPlayerGameScreen(viewModel: GameScreenViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val gameMatrix = viewModel.gameMatrix
    val redWins = uiState.redWins
    val orangeWins = uiState.orangeWins

    ScoreBoard(
        redWins = redWins,
        orangeWins = orangeWins,
        playerOneLabelId = R.string.red_player_wins_count,
        playerTwoLabelId = R.string.orange_player_wins_count,
    )

    val currentBoard = remember(uiState.boardVersion) {
        gameMatrix.getBoard().map { column -> column.toList() }
    }

    BoardGrid(pieces = currentBoard, selectedColumn = uiState.clickedColIndex, winningCells = uiState.boardEvaluationResult.winningCells) { newSelectedCol ->
        if (uiState.boardEvaluationResult.gameState == GameState.IN_PROGRESS) {
            viewModel.updateUiState(newSelectedColumn = newSelectedCol)
        }
    }
    GameStatusMessage(boardEvaluationResult = uiState.boardEvaluationResult,
        currentPlayer = uiState.currentPlayer,
        orangePlayerTurnId = R.string.orange_player_turn,
        redPlayerTurnId = R.string.red_player_turn,
        orangePlayerWonId = R.string.orange_player_wins,
        redPlayerWonId = R.string.red_player_wins)

    val canPlay = uiState.clickedColIndex != null && currentBoard[uiState.clickedColIndex!!][0] == Piece.EMPTY
            && uiState.boardEvaluationResult.gameState == GameState.IN_PROGRESS

    PlayTurnButton(currentPlayer = uiState.currentPlayer, canPlay = canPlay) {
        val col = uiState.clickedColIndex ?: return@PlayTurnButton
        viewModel.onPieceDrop(col)
    }
    Spacer(modifier = Modifier.height(40.dp))
    ResetGameButton { viewModel.resetGame(startingPlayer = listOf(Piece.RED, Piece.ORANGE).random()) }
}

@Composable
fun ScoreBoard(redWins: Int, orangeWins: Int, playerOneLabelId: Int, playerTwoLabelId: Int) {
    Column {
        Text(stringResource(playerOneLabelId, redWins), color = Color.Red)
        Text(stringResource(playerTwoLabelId, orangeWins), color = colorResource(R.color.orange))
    }
}

@Composable
fun GameStatusMessage(
    boardEvaluationResult: BoardEvaluationResult,
    currentPlayer: Piece,
    redPlayerTurnId: Int,
    orangePlayerTurnId: Int,
    redPlayerWonId: Int,
    orangePlayerWonId: Int
) {
    when (boardEvaluationResult.gameState) {
        GameState.ORANGE_WON -> Text(stringResource(orangePlayerWonId), color = colorResource(R.color.orange))
        GameState.RED_WON -> Text(stringResource(redPlayerWonId), color = Color.Red)
        GameState.DRAW -> Text(stringResource(R.string.it_is_a_draw))
        GameState.IN_PROGRESS -> {
            if (currentPlayer == Piece.RED) {
                Text(stringResource(redPlayerTurnId), color = Color.Red)
            }
            if (currentPlayer == Piece.ORANGE) {
                Text(stringResource(orangePlayerTurnId), color = colorResource(R.color.orange))
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
fun ResetGameButton(canReset: Boolean = true, onReset: () -> Unit ) {
    Button(onClick = onReset, enabled = canReset) {
        Text(stringResource(R.string.reset_game))
    }
}






