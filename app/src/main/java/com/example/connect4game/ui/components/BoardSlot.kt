package com.example.connect4game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.connect4game.R
import com.example.connect4game.model.game.types.Piece

@Composable
fun BoardSlot(piece: Piece, isWinning: Boolean = false) {

    val circleColor = when (piece) {
        Piece.RED -> Color.Red
        Piece.ORANGE -> colorResource(R.color.orange)
        Piece.EMPTY -> Color.Black.copy(alpha = 0.5f)
    }

// Add these to your imports if they aren't there yet:
// import androidx.compose.ui.graphics.Brush
// import androidx.compose.ui.graphics.drawscope.Stroke

    val winningModifier = if (isWinning) {
        Modifier.drawWithContent {

            drawContent()



            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.White,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Transparent,
                        Color.Transparent
                    ),
                    radius = size.width / 2f
                )
            )


        }
    } else {
        Modifier
    }

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            .background(Color(0xFF3A1C5E))) {

        Box(modifier = Modifier
            .fillMaxSize(0.8f)
            .then(winningModifier)
            .background(
                color = circleColor,
                shape = CircleShape
            )) {

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (piece != Piece.EMPTY) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.75f)
                            .border(width = 2.dp, color = Color.Black.copy(alpha = 0.2f), shape = CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardSlot() {

    BoardSlot(Piece.EMPTY, isWinning = false)
}