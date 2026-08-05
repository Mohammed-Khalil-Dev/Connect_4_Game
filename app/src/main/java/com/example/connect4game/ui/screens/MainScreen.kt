package com.example.connect4game.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        GameTypesArea(onGameTypeSelected)
    }
}


@Composable
fun GameTypesArea(onGameTypeSelected: (GameType) -> Unit) {
    Button(
        onClick = { onGameTypeSelected(GameType.SINGLE_PLAYER) },
        modifier = Modifier.width(200.dp)
    ) {
        Text(stringResource(R.string.single_player_vs_bot))
    }

    Button(
        onClick = { onGameTypeSelected(GameType.TWO_PLAYER) },
        modifier = Modifier.width(200.dp)
    ) {
        Text(stringResource(R.string.two_player_local))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    // Provide a dummy empty block { } for the preview so it doesn't crash
    MainScreen(onGameTypeSelected = { })
}