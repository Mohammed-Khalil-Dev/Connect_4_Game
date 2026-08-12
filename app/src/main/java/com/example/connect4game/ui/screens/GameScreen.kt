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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.connect4game.R
import com.example.connect4game.data.BotDifficultyManager
import com.example.connect4game.data.ScoreManager
import com.example.connect4game.model.game.core.BotDifficulty
import com.example.connect4game.model.game.core.GameMatrix
import com.example.connect4game.model.game.core.checkGameState
import com.example.connect4game.model.game.core.findBestMove
import com.example.connect4game.model.game.state.GameState
import com.example.connect4game.model.game.state.GameStateDetails
import com.example.connect4game.model.game.types.GameType
import com.example.connect4game.model.game.types.Piece
import com.example.connect4game.model.settings.audio.Sound
import com.example.connect4game.model.settings.audio.SoundManager
import com.example.connect4game.ui.components.BoardGrid
import com.example.connect4game.ui.viewmodels.GameScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun GameScreen(
    gameType: GameType,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    singlePlayerViewModel: GameScreenViewModel = viewModel(),
    twoPlayerViewModel: GameScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val scoreManager = remember { ScoreManager(context) }
    val botDifficultyManager = remember { BotDifficultyManager(context) }



    Column(modifier = Modifier
        .padding(paddingValues).padding(start = 10.dp, end = 10.dp)
        .fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        when(gameType) {
            GameType.SINGLE_PLAYER -> SinglePlayerGameScreen(soundManager = soundManager, viewModel = singlePlayerViewModel,
                scoreManager = scoreManager, botDifficultyManager = botDifficultyManager)
            GameType.TWO_PLAYER -> TwoPlayerGameScreen(soundManager = soundManager, viewModel = twoPlayerViewModel, scoreManager = scoreManager)
        }
    }
}
@Composable
fun SinglePlayerGameScreen(soundManager: SoundManager, viewModel: GameScreenViewModel = viewModel(), scoreManager: ScoreManager, botDifficultyManager: BotDifficultyManager) {

    val playerPiece: Piece = Piece.ORANGE
    val botPiece: Piece = Piece.RED

    val uiState by viewModel.uiState.collectAsState()
    val gameMatrix = viewModel.gameMatrix
    val scope = rememberCoroutineScope()
    val playerWins by scoreManager.getWinsFlow(GameType.SINGLE_PLAYER, piece = playerPiece).collectAsState(initial = 0)
    val botWins by scoreManager.getWinsFlow(GameType.SINGLE_PLAYER, piece = botPiece).collectAsState(initial = 0)
    val botDifficulty: BotDifficulty by botDifficultyManager.botDifficultyFlow.collectAsState(initial = BotDifficulty.MEDIUM)
    val isBotThinking = uiState.currentPlayer == botPiece &&
            uiState.gameStateDetails.gameState == GameState.IN_PROGRESS

    ScoreBoard(
        redWins = botWins,
        orangeWins = playerWins,
        playerOneLabelId = R.string.bot_wins_count,
        playerTwoLabelId = R.string.you_wins_count
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(horizontalArrangement = Arrangement.Center) {
        Text(text = stringResource(R.string.bot_difficulty) + " ", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = when (botDifficulty) {
                BotDifficulty.EASY -> stringResource(R.string.easy)
                BotDifficulty.MEDIUM -> stringResource(R.string.medium)
                BotDifficulty.HARD -> stringResource(R.string.hard)
            },
            color = when (botDifficulty) {
                BotDifficulty.EASY -> Color.Green.copy(alpha = 0.7f)
                BotDifficulty.MEDIUM -> Color.Yellow.copy(alpha = 0.7f)
                BotDifficulty.HARD -> Color.Red
            }
        )
    }




    val currentBoard = remember(uiState.boardVersion) {
        gameMatrix.getBoard().map { column -> column.toList() }
    }
    BoardGrid(pieces = currentBoard, selectedColumn = if (isBotThinking) null else uiState.clickedColIndex
        , winningCells = uiState.gameStateDetails.winningCells,
        isColumnClickable = !isBotThinking) { newSelectedCol ->
        if (uiState.gameStateDetails.gameState == GameState.IN_PROGRESS) {
            viewModel.updateUiState(newSelectedColumn = newSelectedCol)
        }
    }

    GameStatusMessage(
        gameStateDetails = uiState.gameStateDetails,
        currentPlayer = uiState.currentPlayer,
        orangePlayerTurnId = R.string.your_turn,
        redPlayerTurnId = R.string.bots_turn,
        orangePlayerWonId = R.string.you_win,
        redPlayerWonId = R.string.bot_wins
    )


    // Trigger the bot's logic only when the turn switches to RED and the game is active
    LaunchedEffect(key1 = uiState.currentPlayer, key2 = uiState.gameStateDetails.gameState) {
        if (uiState.currentPlayer == botPiece && uiState.gameStateDetails.gameState == GameState.IN_PROGRESS) {
            playBotTurn(
                gameMatrix = gameMatrix,
                botPiece = botPiece,
                playerPiece = playerPiece,
                scoreManager = scoreManager,
                soundManager = soundManager,
                viewModel = viewModel,
                maxDepth = botDifficulty.depth
            )

        }
    }

    val canPlayerPlay = uiState.clickedColIndex != null && currentBoard[uiState.clickedColIndex!!][0] == Piece.EMPTY
            && uiState.gameStateDetails.gameState == GameState.IN_PROGRESS && uiState.currentPlayer == playerPiece

    PlayTurnButton(currentPlayer = playerPiece, canPlay = canPlayerPlay) {
        val col = uiState.clickedColIndex ?: return@PlayTurnButton
        val landedRow = gameMatrix.dropPiece(col = col, piece = uiState.currentPlayer)


        val newGameStateDetails = if (landedRow != null) {
            val details: GameStateDetails = checkGameState(gameMatrix, uiState.currentPlayer,
                landedRow, col)

            if (details.gameState == GameState.ORANGE_WON) {
                scope.launch { scoreManager.incrementWins(GameType.SINGLE_PLAYER, playerPiece) }
            }
            details
        }
        else {
            uiState.gameStateDetails
        }

        val nextPlayer = if (newGameStateDetails.gameState == GameState.IN_PROGRESS) {
            if (uiState.currentPlayer == botPiece) playerPiece else botPiece
        }
        else {
            uiState.currentPlayer
        }

        soundManager.playSound(Sound.DROP_PIECE)
        viewModel.updateUiState(newGameStateDetails = newGameStateDetails, nextPlayer = nextPlayer)

    }


    ResetGameButton(canReset = !isBotThinking) { viewModel.resetGame(startingPlayer = playerPiece) }
}
@Composable
fun TwoPlayerGameScreen(soundManager: SoundManager, viewModel: GameScreenViewModel = viewModel(), scoreManager: ScoreManager) {

    val uiState by viewModel.uiState.collectAsState()
    val gameMatrix = viewModel.gameMatrix
    val scope = rememberCoroutineScope()
    val redWins by scoreManager.getWinsFlow(GameType.TWO_PLAYER, Piece.RED).collectAsState(initial = 0)
    val orangeWins by scoreManager.getWinsFlow(GameType.TWO_PLAYER, Piece.ORANGE).collectAsState(initial = 0)

    ScoreBoard(
        redWins = redWins,
        orangeWins = orangeWins,
        playerOneLabelId = R.string.red_player_wins_count,
        playerTwoLabelId = R.string.orange_player_wins_count,
    )

    val currentBoard = remember(uiState.boardVersion) {
        gameMatrix.getBoard().map { column -> column.toList() }
    }

    BoardGrid(pieces = currentBoard, selectedColumn = uiState.clickedColIndex, winningCells = uiState.gameStateDetails.winningCells) { newSelectedCol ->
        if (uiState.gameStateDetails.gameState == GameState.IN_PROGRESS) {
            viewModel.updateUiState(newSelectedColumn = newSelectedCol)
        }
    }
    GameStatusMessage(gameStateDetails = uiState.gameStateDetails,
        currentPlayer = uiState.currentPlayer,
        orangePlayerTurnId = R.string.orange_player_turn,
        redPlayerTurnId = R.string.red_player_turn,
        orangePlayerWonId = R.string.orange_player_wins,
        redPlayerWonId = R.string.red_player_wins)

    val canPlay = uiState.clickedColIndex != null && currentBoard[uiState.clickedColIndex!!][0] == Piece.EMPTY
            && uiState.gameStateDetails.gameState == GameState.IN_PROGRESS

    PlayTurnButton(currentPlayer = uiState.currentPlayer, canPlay = canPlay) {
        val col = uiState.clickedColIndex ?: return@PlayTurnButton
        val landedRow = gameMatrix.dropPiece(col = col, piece = uiState.currentPlayer)


        val newGameStateDetails = if (landedRow != null) {
            val details: GameStateDetails = checkGameState(gameMatrix, uiState.currentPlayer,
                landedRow, col)

            when(details.gameState) {
                GameState.RED_WON -> scope.launch { scoreManager.incrementWins(GameType.TWO_PLAYER, Piece.RED) }
                GameState.ORANGE_WON -> scope.launch { scoreManager.incrementWins(GameType.TWO_PLAYER, Piece.ORANGE) }
                else -> {}
            }
           details
        }
        else {
            uiState.gameStateDetails
        }


        val nextPlayer = if (newGameStateDetails.gameState == GameState.IN_PROGRESS) {
            if (uiState.currentPlayer == Piece.RED) Piece.ORANGE else Piece.RED
        }
        else {
            uiState.currentPlayer
        }

        soundManager.playSound(Sound.DROP_PIECE)
        viewModel.updateUiState(newGameStateDetails = newGameStateDetails, nextPlayer = nextPlayer)
    }

    ResetGameButton { viewModel.resetGame(startingPlayer = listOf(Piece.RED, Piece.ORANGE).random()) }
}

suspend fun playBotTurn(
    gameMatrix: GameMatrix,
    botPiece: Piece,
    playerPiece: Piece,
    scoreManager: ScoreManager,
    soundManager: SoundManager,
    viewModel: GameScreenViewModel,
    maxDepth: Int
) {


    val availableColumns = gameMatrix.getAvailableColumnsIndex()

    if (availableColumns.isNotEmpty()) {

        val bestColIndex = withContext(Dispatchers.Default) {
            val bestMoveIndex: Int = findBestMove(currentBoard = gameMatrix, maxDepth = maxDepth)
            delay(duration = 250.milliseconds)
            bestMoveIndex
        }
        val landedRow = gameMatrix.dropPiece(col = bestColIndex, piece = botPiece)

        val newGameStateDetails = if (landedRow != null) {
            val details: GameStateDetails = checkGameState(gameMatrix, botPiece, landedRow, bestColIndex)
            if (details.gameState == GameState.RED_WON) {
                scoreManager.incrementWins(GameType.SINGLE_PLAYER, botPiece)
            }
            details
        } else {
            GameStateDetails()
        }

        val nextPlayer = if (newGameStateDetails.gameState == GameState.IN_PROGRESS) playerPiece else botPiece

        soundManager.playSound(Sound.DROP_PIECE)
        viewModel.updateUiState(newGameStateDetails = newGameStateDetails, nextPlayer = nextPlayer)
    }
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
    gameStateDetails: GameStateDetails,
    currentPlayer: Piece,
    redPlayerTurnId: Int,
    orangePlayerTurnId: Int,
    redPlayerWonId: Int,
    orangePlayerWonId: Int
) {
    when (gameStateDetails.gameState) {
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




@Composable
@Preview(showBackground = true)
fun PreviewGameScreen() {
    GameScreen(GameType.SINGLE_PLAYER)
}

