package com.example.connect4game.data

import android.content.Context
import android.os.Bundle
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.connect4game.model.game.core.BotDifficulty
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.difficultyDataStore by preferencesDataStore(name = "difficulty_prefs")
class BotDifficultyManager(private val context: Context) {

    companion object {
        val BOT_DIFFICULTY_KEY: Preferences.Key<Int> = intPreferencesKey("bot_difficulty")
        val DEFAULT_BOT_DIFFICULTY: BotDifficulty = BotDifficulty.MEDIUM

        fun getDepthForDifficulty(botDifficulty: BotDifficulty): Int {
            val remoteConfig = Firebase.remoteConfig
            val remoteValue = remoteConfig.getLong(botDifficulty.remoteKey)

            val activeDepth = if (remoteValue > 0) remoteValue.toInt() else botDifficulty.depth

            // Log an analytics event to track which depth was chosen and whether it was overridden by Remote Config
            try {
                Firebase.analytics.logEvent("bot_difficulty_evaluated", Bundle().apply {
                    putString("difficulty_level", botDifficulty.name)
                    putLong("resolved_depth", activeDepth.toLong())
                    putBoolean("is_remote_override", remoteValue > 0)
                })
            }
            catch (_: Exception) {
            }

            return activeDepth
        }

    }

    val botDifficultyFlow: Flow<BotDifficulty> = context.difficultyDataStore.data.map { pref ->
        val savedDepth: Int = pref[BOT_DIFFICULTY_KEY] ?: DEFAULT_BOT_DIFFICULTY.depth
        BotDifficulty.entries.find { it.depth == savedDepth } ?: BotDifficulty.MEDIUM
    }

    suspend fun saveBotDifficulty(botDifficulty: BotDifficulty) {
        context.difficultyDataStore.edit { preferences ->
            preferences[BOT_DIFFICULTY_KEY] = botDifficulty.depth
        }
    }





}