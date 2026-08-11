package com.example.connect4game.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.connect4game.model.game.core.BotDifficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.difficultyDataStore by preferencesDataStore(name = "difficulty_prefs")
class BotDifficultyManager(private val context: Context) {

    companion object {
        val BOT_DIFFICULTY_KEY: Preferences.Key<Int> = intPreferencesKey("bot_difficulty")
        val DEFAULT_BOT_DIFFICULTY: Int = BotDifficulty.MEDIUM.depth
    }

    val botDifficultyFlow: Flow<BotDifficulty> = context.difficultyDataStore.data.map { pref ->
        val savedDepth = pref[BOT_DIFFICULTY_KEY] ?: DEFAULT_BOT_DIFFICULTY
        BotDifficulty.entries.find { it.depth == savedDepth } ?: BotDifficulty.MEDIUM
    }

    suspend fun saveBotDifficulty(botDifficulty: BotDifficulty) {
        context.difficultyDataStore.edit { preferences ->
            preferences[BOT_DIFFICULTY_KEY] = botDifficulty.depth
        }
    }


}