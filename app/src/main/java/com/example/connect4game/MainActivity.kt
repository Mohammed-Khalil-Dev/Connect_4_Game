package com.example.connect4game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.connect4game.model.GameType
import com.example.connect4game.ui.components.CustomTopBar
import com.example.connect4game.ui.screens.GameScreen
import com.example.connect4game.ui.screens.MainScreen
import com.example.connect4game.ui.screens.Screen
import com.example.connect4game.ui.theme.Connect4GameTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            // update the UI on Navigation stack change
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            // get the current route(screen)
            val currentScreen = navBackStackEntry?.destination?.route
            Connect4GameTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                        .background(brush = Brush.linearGradient(colors = listOf(Color(0xFF331266),
                            Color(0xFF0A0910)))),
                    containerColor = Color.Transparent,
                    topBar = {
                        CustomTopBar(
                            currentScreen = currentScreen,
                            onBackClicked = { navController.popBackStack() },
                            onSettingsClicked = {
                              //TODO: Implement settings
                            }
                        )
                    }

                ) { innerPadding ->



                    NavHost(
                        navController = navController,
                        startDestination = Screen.MainMenu.name
                    ) {


                        composable(Screen.MainMenu.name) {
                            MainScreen(
                                onGameTypeSelected = { selectedType ->
                                    if (selectedType == GameType.SINGLE_PLAYER) {
                                        navController.navigate(Screen.SinglePlayer.name)
                                    }
                                    else {
                                        navController.navigate(Screen.TwoPlayer.name)
                                    }
                                }
                            )
                        }

                        composable(Screen.SinglePlayer.name) {
                            GameScreen(
                                gameType = GameType.SINGLE_PLAYER,
                                paddingValues = innerPadding
                            )
                        }


                        composable(Screen.TwoPlayer.name) {
                            GameScreen(
                                gameType = GameType.TWO_PLAYER,
                                paddingValues = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}