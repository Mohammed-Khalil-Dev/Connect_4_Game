package com.example.connect4game.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.connect4game.model.game.types.Piece
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.botPieceColorDataStore by preferencesDataStore(name = "bot_piece_color_prefs")


class BotPieceColorManager(private val context: Context) {
   companion object {
       val BOT_PIECE_COLOR_KEY: Preferences.Key<String> = stringPreferencesKey(name = "bot_piece_color")
       val DEFAULT_BOT_PIECE_COLOR: String = Piece.RED.name
   }

    val botPieceColorFlow: Flow<Piece> = context.botPieceColorDataStore.data.map { preferences ->
        val currentBotPieceColor: String = preferences[BOT_PIECE_COLOR_KEY] ?: DEFAULT_BOT_PIECE_COLOR
        return@map when (currentBotPieceColor) {
            Piece.ORANGE.name -> Piece.ORANGE
            else -> Piece.RED
        }

    }

    suspend fun saveBotPieceColor(piece: Piece) {
        context.botPieceColorDataStore.edit { preferences ->
            preferences[BOT_PIECE_COLOR_KEY] = if (piece == Piece.ORANGE)
                Piece.ORANGE.name else Piece.RED.name
        }
    }

}