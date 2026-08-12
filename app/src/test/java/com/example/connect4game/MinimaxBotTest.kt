package com.example.connect4game

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

    @Test
    fun botTakesWinningMove_Horizontal(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = Piece.RED)
        board.dropPiece(col = 1, piece = Piece.RED)
        board.dropPiece(col = 2, piece = Piece.RED)

        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.HARD.depth)

        assertEquals( 3, selectedColumn)
    }
    @Test
    fun botTakesWinningMove_Vertical(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = Piece.RED)
        board.dropPiece(col = 0, piece = Piece.RED)
        board.dropPiece(col = 0, piece = Piece.RED)

        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.HARD.depth)

        assertEquals( 0, selectedColumn)

    }
    @Test
    fun botBlocksPlayerWinningMove_Horizontal(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = Piece.ORANGE)
        board.dropPiece(col = 1, piece = Piece.ORANGE)
        board.dropPiece(col = 2, piece = Piece.ORANGE)

        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.HARD.depth)

        assertEquals( 3, selectedColumn)


    }
    @Test
    fun botBlocksPlayerWinningMove_Vertical(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 0, piece = Piece.ORANGE)
        board.dropPiece(col = 0, piece = Piece.ORANGE)
        board.dropPiece(col = 0, piece = Piece.ORANGE)

        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.HARD.depth)

        assertEquals( 0, selectedColumn)

    }

    @Test
    fun botTakesWinInsteadOfBlockingPlayer(): Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 1, piece = Piece.RED)
        board.dropPiece(col = 1, piece = Piece.RED)
        board.dropPiece(col = 1, piece = Piece.RED)
        board.dropPiece(col = 0, piece = Piece.ORANGE)
        board.dropPiece(col = 0, piece = Piece.ORANGE)
        board.dropPiece(col = 0, piece = Piece.ORANGE)

        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.HARD.depth)

        assertEquals( 1, selectedColumn)
    }
    @Test
    fun botMakesFork_HardDifficulty():  Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 3, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.ORANGE)
        board.dropPiece(col = 4, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.RED)


        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.HARD.depth)

        assertTrue(selectedColumn == 2 || selectedColumn == 5)
    }

    @Test
    fun botMakesFork_MediumDifficulty():  Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 3, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.ORANGE)
        board.dropPiece(col = 4, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.RED)


        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.MEDIUM.depth)

        assertTrue(selectedColumn == 2 || selectedColumn == 5)
    }

    @Test
    fun botMakesFork_EasyDifficulty():  Unit = runBlocking {
        val board = GameMatrix()

        board.dropPiece(col = 3, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.ORANGE)
        board.dropPiece(col = 4, piece = Piece.RED)
        board.dropPiece(col = 4, piece = Piece.RED)


        val selectedColumn = findBestMove(currentBoard = board, maxDepth = BotDifficulty.EASY.depth)

        assertTrue(selectedColumn == 2 || selectedColumn == 5)
    }





}