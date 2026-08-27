package com.example.connect4game

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.connect4game.model.game.core.BotDifficulty
import com.example.connect4game.ui.Connect4Game
import com.example.connect4game.ui.theme.Connect4GameTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            initializeRemoteConfig()
        }
        catch (e: Exception) {
            Firebase.crashlytics.log("initializeRemoteConfig-error: $e")
        }
        setContent {
            Connect4GameTheme {
                Connect4Game()
            }
        }
    }



    fun initializeRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig

        // set time until update
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults: Map<String, Any> = mapOf(
            BotDifficulty.EASY.remoteKey to BotDifficulty.EASY.depth,
            BotDifficulty.MEDIUM.remoteKey to BotDifficulty.MEDIUM.depth,
            BotDifficulty.HARD.remoteKey to BotDifficulty.HARD.depth
        )
        remoteConfig.setDefaultsAsync(defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Firebase.analytics.logEvent("remote_config_fetched", Bundle().apply {
                        putBoolean("was_updated", updated)
                    })
                }
                else {
                    Firebase.analytics.logEvent("remote_config_fetched", Bundle().apply {
                        putBoolean("was_updated", false)
                    })
                }
            }
    }


}