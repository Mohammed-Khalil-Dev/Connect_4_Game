package com.example.connect4game.model

import android.content.Context
import android.media.SoundPool
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.connect4game.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private val Context.soundDataStore: DataStore<Preferences> by preferencesDataStore(name = "sound_settings")

class SoundManager(private val context: Context) {

    companion object {
        private val VOLUME_KEY = floatPreferencesKey("sound_volume")
        const val DEFAULT_SOUND_VOLUME: Float = 0.7f
    }

    var soundVolume: Float = 1f
        private set

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .build()

    private val dropSoundId = soundPool.load(context, R.raw.piece_drop_sound, 1)

    // broadcast changes to observers
    val volumeFlow: Flow<Float> = context.soundDataStore.data
        // Extract the volume value from each new preferences update
        .map { preferencesMap ->
            preferencesMap[VOLUME_KEY] ?: DEFAULT_SOUND_VOLUME
        }


    init {
        // Load the saved volume when SoundManager starts up
        CoroutineScope(Dispatchers.IO).launch {
            // Fetch the current preferences map from the Reactive stream,
            // which is a stream of versions of the same object
            val preferencesMap: Preferences = context.soundDataStore.data.first()
            soundVolume = preferencesMap[VOLUME_KEY] ?: DEFAULT_SOUND_VOLUME
        }
    }

    fun playSound(sound: Sound) {
        when(sound) {
            Sound.DROP_PIECE -> soundPool.play(dropSoundId, soundVolume, soundVolume, 0, 0, 1f)
        }
    }

    suspend fun saveVolume(volumeValue: Float) {
        soundVolume = volumeValue
        context.soundDataStore.edit { preferencesMutableMap ->
            preferencesMutableMap[VOLUME_KEY] = volumeValue
        }
    }
}