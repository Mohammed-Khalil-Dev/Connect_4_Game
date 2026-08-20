package com.example.connect4game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.connect4game.model.game.types.GameType
import com.example.connect4game.ui.components.CustomTopBar
import com.example.connect4game.ui.factory.GameViewModelFactory
import com.example.connect4game.ui.screens.GameScreen
import com.example.connect4game.ui.screens.MainScreen
import com.example.connect4game.ui.screens.Screen
import com.example.connect4game.ui.screens.SettingScreen
import com.example.connect4game.ui.theme.AbyssNavy
import com.example.connect4game.ui.theme.DeepPlum
import com.example.connect4game.ui.theme.MidnightPurple
import com.example.connect4game.ui.theme.NightMagenta
import com.example.connect4game.ui.theme.TwilightBlue
import com.example.connect4game.ui.viewmodels.GameScreenViewModel

val twilightGradientBrush = Brush.linearGradient(
    colors = listOf(
        TwilightBlue,
        MidnightPurple,
        NightMagenta,
        DeepPlum,
        AbyssNavy
    )
)
@Composable
fun Connect4Game() {
    val navController = rememberNavController()
    // update the UI on Navigation stack change
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // get the current route(screen)
    val currentScreen = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val singlePlayerFactory = remember { GameViewModelFactory(context = context, gameType = GameType.SINGLE_PLAYER) }
    val twoPlayerFactory = remember { GameViewModelFactory(context = context, gameType = GameType.TWO_PLAYER) }

    val singlePlayerViewModel: GameScreenViewModel = viewModel(key = GameType.SINGLE_PLAYER.name,
        factory = singlePlayerFactory)
    val twoPlayerViewModel: GameScreenViewModel = viewModel(key = GameType.TWO_PLAYER.name,
        factory = twoPlayerFactory)

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(brush = twilightGradientBrush),
        containerColor = Color.Transparent,
        topBar = {
            CustomTopBar(
                currentScreen = currentScreen,
                onBackClicked = { navController.popBackStack() },
                onSettingsClicked = {
                    navController.navigate(route = Screen.Setting.name)
                }
            )
        }

    ) { innerPadding ->



        NavHost(
            navController = navController,
            startDestination = Screen.MainMenu.name
        ) {


            composable(route = Screen.MainMenu.name) {
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

            composable(route = Screen.SinglePlayer.name) {
                GameScreen(
                    gameType = GameType.SINGLE_PLAYER,
                    paddingValues = innerPadding,
                    viewModel = singlePlayerViewModel
                )
            }


            composable(route = Screen.TwoPlayer.name) {
                GameScreen(
                    gameType = GameType.TWO_PLAYER,
                    paddingValues = innerPadding,
                    viewModel = twoPlayerViewModel
                )
            }

            composable(route = Screen.Setting.name) {
                SettingScreen(paddingValues = innerPadding)
            }
        }
    }

}