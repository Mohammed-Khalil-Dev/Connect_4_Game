package com.example.connect4game.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connect4game.data.BotDifficultyManager
import com.example.connect4game.data.BotPieceColorManager
import com.example.connect4game.data.ScoreManager
import com.example.connect4game.model.game.core.GameMatrix
import com.example.connect4game.model.game.core.checkGameState
import com.example.connect4game.model.game.core.findBestMove
import com.example.connect4game.model.game.state.BoardEvaluationResult
import com.example.connect4game.model.game.state.GameState
import com.example.connect4game.model.game.state.GameUiState
import com.example.connect4game.model.game.types.GameType
import com.example.connect4game.model.game.types.Piece
import com.example.connect4game.model.settings.audio.Sound
import com.example.connect4game.model.settings.audio.SoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class GameScreenViewModel(
    private val gameType: GameType,
    private val scoreManager: ScoreManager,
    private val soundManager: SoundManager,
    private val botDifficultyManager: BotDifficultyManager,
    private val botPieceColorManager: BotPieceColorManager
) : ViewModel() {

    val gameMatrix = GameMatrix()
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            botDifficultyManager.botDifficultyFlow.collect { difficulty ->
                _uiState.update { currentState ->
                    currentState.copy(currentBotDifficulty = difficulty)

                }
            }
        }

        viewModelScope.launch {
            botPieceColorManager.botPieceColorFlow.collect { color ->
                _uiState.update { currentState ->
                    currentState.copy(botPieceColor = color)
                }
            }
        }
        viewModelScope.launch {
            scoreManager.getWinsFlow(gameType = gameType, piece = Piece.RED).collect { redWins ->
                _uiState.update { currentState ->
                    currentState.copy(redWins = redWins)
                }
            }
        }

        viewModelScope.launch {
            scoreManager.getWinsFlow(gameType = gameType, piece = Piece.ORANGE).collect { orangeWins ->
                _uiState.update { currentState ->
                    currentState.copy(orangeWins = orangeWins)
                }
            }
        }


    }

    fun updateUiState(
        newBoardEvaluationResult: BoardEvaluationResult? = null,
        nextPlayer: Piece? = null,
        newSelectedColumn: Int? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                boardEvaluationResult = newBoardEvaluationResult ?: currentState.boardEvaluationResult,
                currentPlayer = nextPlayer ?: currentState.currentPlayer,
                boardVersion = currentState.boardVersion + 1,
                clickedColIndex = newSelectedColumn
            )
        }
    }

    fun resetGame(startingPlayer: Piece) {
        gameMatrix.clearBoard()

        _uiState.update { currentState ->
            currentState.copy(
                boardEvaluationResult = BoardEvaluationResult(),
                currentPlayer = startingPlayer,
                boardVersion = currentState.boardVersion + 1,
                clickedColIndex = null
            )
        }
    }

    suspend fun playBotTurn() {
        val botPiece = uiState.value.botPieceColor
        val playerPiece = if (botPiece == Piece.RED) Piece.ORANGE else Piece.RED
        val availableColumns = gameMatrix.getAvailableColumnsIndex()

        if (availableColumns.isNotEmpty()) {

            val bestColIndex = withContext(Dispatchers.Default) {
                val bestMoveIndex: Int = findBestMove(currentBoard = gameMatrix, botDifficulty = uiState.value.currentBotDifficulty,
                    botPiece = botPiece)
                delay(duration = 250.milliseconds)
                bestMoveIndex
            }
            val landedRow = gameMatrix.dropPiece(col = bestColIndex, piece = botPiece)

            val newBoardEvaluationResult = if (landedRow != null) {
                val evaluationResult: BoardEvaluationResult = checkGameState(currentBoard = gameMatrix, currentPiece = botPiece,
                    currentRow = landedRow, currentCol = bestColIndex)
                val botWinState = if (botPiece == Piece.RED) GameState.RED_WON else GameState.ORANGE_WON
                if (evaluationResult.gameState == botWinState) {
                    scoreManager.incrementWins(GameType.SINGLE_PLAYER, botPiece)
                }
                evaluationResult
            } else {
                BoardEvaluationResult()
            }

            val nextPlayer = if (newBoardEvaluationResult.gameState == GameState.IN_PROGRESS) playerPiece else botPiece

            soundManager.playSound(Sound.DROP_PIECE)
            this.updateUiState(newBoardEvaluationResult = newBoardEvaluationResult, nextPlayer = nextPlayer)
        }
    }


    fun onPieceDrop(col: Int) {
        val currentPiece = uiState.value.currentPlayer
        val landedRow = gameMatrix.dropPiece(col = col, piece = currentPiece)

        val newGameStateDetails = if (landedRow != null) {
            val evaluationResult: BoardEvaluationResult = checkGameState(currentBoard = gameMatrix, currentPiece = currentPiece,
                currentRow = landedRow,
                currentCol = col)


            if (evaluationResult.gameState == GameState.RED_WON) {
                viewModelScope.launch { scoreManager.incrementWins(gameType, Piece.RED) }
            } else if (evaluationResult.gameState == GameState.ORANGE_WON) {
                viewModelScope.launch { scoreManager.incrementWins(gameType, Piece.ORANGE) }
            }
            evaluationResult
        } else {
            uiState.value.boardEvaluationResult
        }

        val nextPlayer = if (newGameStateDetails.gameState == GameState.IN_PROGRESS) {
            if (currentPiece == Piece.RED) Piece.ORANGE else Piece.RED
        } else {
            currentPiece
        }

        soundManager.playSound(Sound.DROP_PIECE)
        updateUiState(newBoardEvaluationResult = newGameStateDetails, nextPlayer = nextPlayer)
        if (uiState.value.currentPlayer == uiState.value.botPieceColor && uiState.value.boardEvaluationResult.gameState == GameState.IN_PROGRESS &&
            gameType == GameType.SINGLE_PLAYER
        ) {
            viewModelScope.launch {
                playBotTurn()
            }

        }



    }


}