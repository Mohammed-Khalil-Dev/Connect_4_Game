package com.example.connect4game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.connect4game.model.Piece

@Composable
fun BoardSlot(piece: Piece) {

    val circleColor = when (piece) {
        Piece.RED -> Color.Red
        Piece.YELLOW -> Color.Yellow
        Piece.EMPTY -> Color.White
    }
    Box(contentAlignment = Alignment.Center,
        // aspectRatio(1f) makes height same as width
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            .background(Color.Blue)) {

        Box(modifier = Modifier
            .fillMaxSize(0.8f)
            .background(
                color = circleColor,
                shape = CircleShape
            ))

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardSlot() {
    BoardSlot(Piece.RED)
}