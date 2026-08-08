package com.example.connect4game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connect4game.R
import com.example.connect4game.model.GameType


@Composable
fun MainScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onGameTypeSelected: (GameType) -> Unit
) {
    Column(
        modifier = Modifier.padding(paddingValues).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val rainbowColors = listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta
        )

        Text(stringResource(R.string.app_name), style = TextStyle(
            fontSize = 30.sp,
            brush = Brush.linearGradient(colors = rainbowColors)
        )
        )
        GameTypesArea(onGameTypeSelected)
    }
}


@Composable
fun GameTypesArea(onGameTypeSelected: (GameType) -> Unit) {
    val boardBlueGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF00BCD4)) // Deep blue to bright cyan
    )
    val buttonShape = RoundedCornerShape(50.dp)

    Button(
        onClick = { onGameTypeSelected(GameType.SINGLE_PLAYER) },
        modifier = Modifier
            .width(200.dp)
            .background(brush = boardBlueGradient, shape = buttonShape),
        colors = buttonColors(containerColor = Color.Transparent)
    ) {
        Text(stringResource(R.string.single_player_vs_bot))
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = { onGameTypeSelected(GameType.TWO_PLAYER) },
        modifier = Modifier
            .width(200.dp)
            .background(brush = boardBlueGradient, shape = buttonShape),
        colors = buttonColors(containerColor = Color.Transparent)
    ) {
        Text(stringResource(R.string.two_player_local))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {

    MainScreen(onGameTypeSelected = { })
}