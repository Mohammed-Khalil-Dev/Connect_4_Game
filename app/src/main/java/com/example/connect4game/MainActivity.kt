package com.example.connect4game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.connect4game.model.GameType
import com.example.connect4game.ui.screens.GameScreen
import com.example.connect4game.ui.screens.MainScreen
import com.example.connect4game.ui.theme.Connect4GameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Connect4GameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()


                    NavHost(
                        navController = navController,
                        startDestination = "main_screen"
                    ) {


                        composable("main_screen") {
                            MainScreen(
                                onGameTypeSelected = { selectedType ->

                                    if (selectedType == GameType.SINGLE_PLAYER) {
                                        navController.navigate("single_player_screen")
                                    } else {
                                        navController.navigate("two_player_screen")
                                    }
                                }
                            )
                        }

                        composable("single_player_screen") {
                            GameScreen(
                                gameType = GameType.SINGLE_PLAYER,

                            )
                        }


                        composable("two_player_screen") {
                            GameScreen(
                                gameType = GameType.TWO_PLAYER
                            )
                        }
                    }
                }
            }
        }
    }
}