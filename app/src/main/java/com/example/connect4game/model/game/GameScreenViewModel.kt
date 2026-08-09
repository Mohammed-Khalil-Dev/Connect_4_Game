package com.example.connect4game.model.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameScreenViewModel : ViewModel() {

    val gameMatrix = GameMatrix()
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun updateUiState(
        newGameStateDetails: GameStateDetails? = null,
        nextPlayer: Piece? = null,
        newSelectedColumn: Int? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                gameStateDetails = newGameStateDetails ?: currentState.gameStateDetails,
                currentPlayer = nextPlayer ?: currentState.currentPlayer,
                boardVersion = currentState.boardVersion + 1,
                clickedColIndex = newSelectedColumn
            )
        }
    }

    fun resetGame() {
        gameMatrix.clearBoard()

        _uiState.update { currentState ->
            currentState.copy(
                gameStateDetails = GameStateDetails(),
                currentPlayer = listOf(Piece.RED, Piece.ORANGE).random(),
                boardVersion = 0,
                clickedColIndex = null
            )
        }
    }


}