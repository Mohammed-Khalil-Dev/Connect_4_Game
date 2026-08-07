package com.example.connect4game.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.connect4game.R
import com.example.connect4game.ui.screens.Screen

val TwilightBlue = Color(0xFF0F172A)
val MidnightPurple = Color(0xFF2E1065)
val NightMagenta = Color(0xFF4A044E)
val DeepPlum = Color(0xFF311235)
val AbyssNavy = Color(0xFF020617)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    currentScreen: String?,
    onBackClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val rainbowGradientBrush = Brush.linearGradient(
        colors = listOf(
            TwilightBlue,
            MidnightPurple,
            NightMagenta,
            DeepPlum,
            AbyssNavy
        )
    )
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false }, // If the user taps outside the box
            title = { Text(stringResource(R.string.exit_game)) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_leave)) },
            confirmButton = {
                TextButton(onClick = { activity?.finish() }) {
                    Text(stringResource(R.string.exit))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            when (currentScreen) {
                Screen.SinglePlayer.name -> Text(stringResource(R.string.single_player_game), color = Color.White)
                Screen.TwoPlayer.name -> Text(stringResource(R.string.two_player_game), color = Color.White)
            }

        },
        navigationIcon = {
            if (currentScreen != Screen.MainMenu.name) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back),
                        tint = Color.Yellow

                    )
                }
            }
            else {

                IconButton(onClick = {showExitDialog = true}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.exit_app),
                        tint = Color.Yellow
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        modifier = Modifier
            .background(brush = rainbowGradientBrush)
            .padding(horizontal = 4.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewCustomTopBar() {
    CustomTopBar(Screen.TwoPlayer.name,{}) { }
}