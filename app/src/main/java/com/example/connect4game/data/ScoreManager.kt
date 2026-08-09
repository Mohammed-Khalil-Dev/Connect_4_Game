package com.example.connect4game.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.connect4game.model.game.types.GameType
import com.example.connect4game.model.game.types.Piece
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.scoreDataStore by preferencesDataStore(name = "scores_prefs")

class ScoreManager(private val context: Context) {


    private fun getPrefKey(gameType: GameType, piece: Piece): Preferences.Key<Int> {
        return intPreferencesKey("${gameType.name}_${piece.name}_wins")
    }


    fun getWinsFlow(gameType: GameType, piece: Piece): Flow<Int> {
        return context.scoreDataStore.data.map { preferences ->
            preferences[getPrefKey(gameType, piece)] ?: 0
        }
    }

    suspend fun incrementWins(gameType: GameType, piece: Piece) {
        context.scoreDataStore.edit { preferences ->
            val key = getPrefKey(gameType, piece)
            val currentWins = preferences[key] ?: 0
            preferences[key] = currentWins + 1
        }
    }

    suspend fun resetWins(gameType: GameType) {
        context.scoreDataStore.edit { preferences ->
            val keyOrangePiece = getPrefKey(gameType, Piece.ORANGE)
            val keyRedPiece = getPrefKey(gameType, Piece.RED)
            preferences.remove(keyOrangePiece)
            preferences.remove(keyRedPiece)
        }
    }
}