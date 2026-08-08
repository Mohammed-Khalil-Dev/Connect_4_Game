package com.example.connect4game.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
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
import com.example.connect4game.model.AppLanguage
import com.example.connect4game.model.SoundManager
import com.example.connect4game.ui.theme.Connect4GameTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp)) {
    val context = LocalContext.current
    val soundManager: SoundManager = remember { SoundManager(context = context) }
    // Check the app current language
    val configuration = LocalConfiguration.current
    val currentLangTag = configuration.locales[0].language
    val initialLanguage = if (currentLangTag == "ar") AppLanguage.ARABIC else AppLanguage.ENGLISH
    val coroutineScope = rememberCoroutineScope()
    // observe volumeFlow. trigger on flow value change
    val currentVolume by soundManager.volumeFlow.collectAsState(initial = SoundManager.DEFAULT_SOUND_VOLUME)


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



        


    }


}

@Composable
fun LanguageRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
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