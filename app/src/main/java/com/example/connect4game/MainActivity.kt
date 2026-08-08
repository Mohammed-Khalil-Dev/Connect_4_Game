package com.example.connect4game

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.connect4game.ui.Connect4Navigation
import com.example.connect4game.ui.theme.Connect4GameTheme


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Connect4GameTheme {
                Connect4Navigation()
            }
        }
    }
}