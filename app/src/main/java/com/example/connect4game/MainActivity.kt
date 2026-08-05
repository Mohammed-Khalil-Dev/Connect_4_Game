package com.example.connect4game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.connect4game.model.GameType
import com.example.connect4game.ui.screens.GameScreen
import com.example.connect4game.ui.screens.MainScreen
import com.example.connect4game.ui.theme.Connect4GameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Connect4GameTheme(darkTheme = true) {

                // mutableStateOf reruns setContent on value change.
                // remember Compose not to reset this variable back to null.
                var currentGameType by remember { mutableStateOf<GameType?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if (currentGameType == null) {
                        MainScreen(
                            paddingValues = innerPadding,
                            onGameTypeSelected = { selectedType ->
                                currentGameType = selectedType
                            }
                        )
                    }
                    else {
                        GameScreen(gameType = currentGameType!!, paddingValues = innerPadding)
                    }
                }
            }
        }
    }
}