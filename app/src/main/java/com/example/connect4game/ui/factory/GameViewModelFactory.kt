package com.example.connect4game.ui.factory


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.connect4game.data.BotDifficultyManager
import com.example.connect4game.data.BotPieceColorManager
import com.example.connect4game.data.ScoreManager
import com.example.connect4game.model.game.types.GameType
import com.example.connect4game.model.settings.audio.SoundManager
import com.example.connect4game.network.WiFiDirectManager
import com.example.connect4game.ui.viewmodels.GameScreenViewModel

class GameViewModelFactory(private val context: Context, private val gameType: GameType, private val wifiDirectManager: WiFiDirectManager? = null): ViewModelProvider.Factory {
    //override create so it create viewmodel with parameters
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameScreenViewModel::class.java)) {
            val appContext = context.applicationContext


            val scoreManager = ScoreManager(context = appContext)
            val soundManager = SoundManager(context = appContext)
            val difficultyManager = BotDifficultyManager(context = appContext)
            val botPieceColorManager = BotPieceColorManager(context = appContext)

            return GameScreenViewModel(
                gameType = gameType,
                scoreManager = scoreManager,
                soundManager = soundManager,
                botDifficultyManager = difficultyManager,
                botPieceColorManager = botPieceColorManager,
                wifiDirectManager = wifiDirectManager
            ) as T
        }
        else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}