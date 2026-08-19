package com.example.connect4game

import com.example.connect4game.data.BotPieceColorManager
import com.example.connect4game.model.game.core.BotDifficulty
import com.example.connect4game.model.game.core.GameMatrix
import com.example.connect4game.model.game.core.findBestMove
import com.example.connect4game.model.game.types.Piece
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MinimaxBotTest {
    val botPiece = Piece.valueOf(BotPieceColorManager.DEFAULT_BOT_PIECE_COLOR)
    val playerPiece = if (botPiece == Piece.RED) Piece.ORANGE else Piece.RED
    @Test
    fun botTakesWinningMove_Horizontal(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = botPiece)
        board.dropPiece(col = 1, piece = botPiece)
        board.dropPiece(col = 2, piece = botPiece)

        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.EASY,
            isTest = true, botPiece = botPiece)

        assertEquals( 3, selectedColumn)
    }
    @Test
    fun botTakesWinningMove_Vertical(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = botPiece)
        board.dropPiece(col = 0, piece = botPiece)
        board.dropPiece(col = 0, piece = botPiece)

        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.EASY,
            isTest = true, botPiece = botPiece)

        assertEquals( 0, selectedColumn)

    }
    @Test
    fun botBlocksPlayerWinningMove_Horizontal(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = playerPiece)
        board.dropPiece(col = 1, piece = playerPiece)
        board.dropPiece(col = 2, piece = playerPiece)

        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.EASY,
            isTest = true, botPiece = botPiece)

        assertEquals( 3, selectedColumn)


    }
    @Test
    fun botBlocksPlayerWinningMove_Vertical(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = playerPiece)
        board.dropPiece(col = 0, piece = playerPiece)
        board.dropPiece(col = 0, piece = playerPiece)

        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.EASY,
            isTest = true, botPiece = botPiece)

        assertEquals( 0, selectedColumn)

    }

    @Test
    fun botTakesWinInsteadOfBlockingPlayer(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 1, piece = botPiece)
        board.dropPiece(col = 1, piece = botPiece)
        board.dropPiece(col = 1, piece = botPiece)
        board.dropPiece(col = 0, piece = playerPiece)
        board.dropPiece(col = 0, piece = playerPiece)
        board.dropPiece(col = 0, piece = playerPiece)

        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.EASY,
            isTest = true, botPiece = botPiece)

        assertEquals( 1, selectedColumn)
    }
    @Test
    fun botMakesFork_HardDifficulty():  Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 3, piece = botPiece)
        board.dropPiece(col = 4, piece = botPiece)
        board.dropPiece(col = 4, piece = playerPiece)
        board.dropPiece(col = 4, piece = botPiece)
        board.dropPiece(col = 4, piece = botPiece)

        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.HARD,
            isTest = true, botPiece = botPiece)

        assertTrue(selectedColumn == 2 || selectedColumn == 5)
    }

    @Test
    fun botMakesFork_MediumDifficulty():  Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 3, piece = botPiece)
        board.dropPiece(col = 4, piece = botPiece)
        board.dropPiece(col = 4, piece = playerPiece)
        board.dropPiece(col = 4, piece = botPiece)
        board.dropPiece(col = 4, piece = botPiece)


        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.MEDIUM,
            isTest = true, botPiece = botPiece)

        assertTrue(selectedColumn == 2 || selectedColumn == 5)
    }

    @Test
    fun botMakesFork_EasyDifficulty():  Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 3, piece = botPiece)
        board.dropPiece(col = 4, piece = botPiece)
        board.dropPiece(col = 4, piece = playerPiece)
        board.dropPiece(col = 4, piece = botPiece)
        board.dropPiece(col = 4, piece = botPiece)


        val selectedColumn = findBestMove(currentBoard = board, botDifficulty = BotDifficulty.EASY,
            isTest = true, botPiece = botPiece)

        assertTrue(selectedColumn == 2 || selectedColumn == 5)
    }





}