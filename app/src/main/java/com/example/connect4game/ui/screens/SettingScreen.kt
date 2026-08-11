package com.example.connect4game.ui.screens

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.connect4game.R
import com.example.connect4game.data.BotDifficultyManager
import com.example.connect4game.data.ScoreManager
import com.example.connect4game.model.game.core.BotDifficulty
import com.example.connect4game.model.game.types.GameType
import com.example.connect4game.model.settings.audio.SoundManager
import com.example.connect4game.model.settings.language.AppLanguage
import com.example.connect4game.ui.theme.Connect4GameTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp)) {
    val context = LocalContext.current
    val soundManager: SoundManager = remember { SoundManager(context = context) }
    val scoreManager: ScoreManager = remember { ScoreManager(context = context) }
    val botDifficultyManager: BotDifficultyManager = remember { BotDifficultyManager(context = context) }
    // Check the app current language
    val configuration = LocalConfiguration.current
    val currentLangTag = configuration.locales[0].language
    val initialLanguage = if (currentLangTag == "ar") AppLanguage.ARABIC else AppLanguage.ENGLISH
    val coroutineScope = rememberCoroutineScope()
    // observe volumeFlow. trigger on flow value change
    val currentVolume by soundManager.volumeFlow.collectAsState(initial = SoundManager.DEFAULT_SOUND_VOLUME)
    var gameTypeToReset by remember { mutableStateOf<GameType?>(null) }
    val selectedDifficulty: BotDifficulty by botDifficultyManager.botDifficultyFlow.collectAsState(initial = BotDifficulty.MEDIUM)


    var selectedLanguage by remember { mutableStateOf(initialLanguage) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(stringResource(R.string.language), fontSize = 20.sp, color = Color.White)

        LanguageRadioButton(
            text = stringResource(R.string.english),
            selected = (selectedLanguage == AppLanguage.ENGLISH),
            onClick = {
                selectedLanguage = AppLanguage.ENGLISH
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(AppLanguage.ENGLISH.tag)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        )
        LanguageRadioButton(
            text = stringResource(R.string.arabic_word),
            selected = (selectedLanguage == AppLanguage.ARABIC),
            onClick = {
                selectedLanguage = AppLanguage.ARABIC
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(AppLanguage.ARABIC.tag)
                AppCompatDelegate.setApplicationLocales(appLocale)
            })
        Spacer(modifier = Modifier.height(20.dp))
        Text(stringResource(R.string.sound), fontSize = 20.sp, color = Color.White)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                contentDescription = stringResource(R.string.mute),
                tint = Color.White
            )

            Slider(
                value = currentVolume,
                valueRange = 0f..1f,
                onValueChange = { newVolume ->
                    coroutineScope.launch(Dispatchers.IO) {
                        soundManager.saveVolume(newVolume)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = stringResource(R.string.max_volume),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = stringResource(R.string.game_data), fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Column(horizontalAlignment = Alignment.Start) {
            ResetWinsButton(text = stringResource(R.string.reset_single_player_wins)) {
                gameTypeToReset = GameType.SINGLE_PLAYER
            }
            Spacer(modifier = Modifier.height(10.dp))
            ResetWinsButton(text = stringResource(R.string.reset_two_player_wins)) {
                gameTypeToReset = GameType.TWO_PLAYER
            }

        }

        gameTypeToReset?.let { type ->
            val scoresResetMessage = stringResource(id = R.string.scores_reset)
            ResetAlertDialog(
                gameType = type,
                onConfirm = {
                    coroutineScope.launch {
                        scoreManager.resetWins(gameType = type)
                        Toast.makeText(context,
                            scoresResetMessage, Toast.LENGTH_SHORT).show()
                    }

                },
                onDismiss = {
                    gameTypeToReset = null
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(R.string.bot_difficulty), fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DifficultyRadioButton(
                text = stringResource(R.string.easy),
                selected = selectedDifficulty == BotDifficulty.EASY,
                color = Color.Green.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            ) {
                coroutineScope.launch {
                    botDifficultyManager.saveBotDifficulty(BotDifficulty.EASY)
                }
            }

            DifficultyRadioButton(
                text = stringResource(R.string.medium),
                selected = selectedDifficulty == BotDifficulty.MEDIUM,
                color = Color.Yellow.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            ) {
                coroutineScope.launch {
                    botDifficultyManager.saveBotDifficulty(BotDifficulty.MEDIUM)
                }
            }

            DifficultyRadioButton(
                text = stringResource(R.string.hard),
                selected = selectedDifficulty == BotDifficulty.HARD,
                color = Color.Red,
                modifier = Modifier.weight(1f)
            ) {
                coroutineScope.launch {
                    botDifficultyManager.saveBotDifficulty(BotDifficulty.HARD)
                }
            }
        }





        


    }


}

@Composable
fun LanguageRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp),
            color = Color.White
        )
    }
}

@Composable
fun DifficultyRadioButton(
    text: String,
    selected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 1.dp)
            .background(color = color, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.6f)
            )
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp
        )
    }
}
@Composable
fun ResetWinsButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB30000),
            contentColor = Color.White)) {
        Text(text = text)
    }
}
@Composable
fun ResetAlertDialog(
    gameType: GameType,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val gameTypeToReset = when(gameType) {
        GameType.SINGLE_PLAYER -> stringResource(R.string.single_player)
        GameType.TWO_PLAYER -> stringResource(R.string.two_player)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.reset_scores)) },
        text = { Text(text = stringResource(
            R.string.delete_wins_message,
            gameTypeToReset
        )) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
@Preview(showBackground = true, locale = "en", showSystemUi = true)
fun PreviewSettingScreen() {
    Connect4GameTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SettingScreen()
        }
    }
}