package dev.sagi.georgethevetrinator.logic

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import dev.sagi.georgethevetrinator.R

class AudioManager private constructor(private val context: Context) {
    companion object {
        @Volatile private var instance: AudioManager? = null
        fun getInstance(context: Context): AudioManager =
            instance ?: synchronized(this) {
                instance ?: AudioManager(context.applicationContext).also { instance = it }
            }
    }

    private var currentTrackResId: Int = -1
    private var mediaPlayer: MediaPlayer? = null
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Sound IDs
    private var soundCoin: Int = 0
    private var soundCrash: Int = 0
    private var soundBoost: Int = 0
    private val bgMusic: Int = R.raw.bg_music
    private val bgGameOver: Int = R.raw.bg_game_over


    init {
        // Load SFX into memory
        soundCoin = soundPool.load(context, R.raw.sfx_coin, 1)
        soundCrash = soundPool.load(context, R.raw.sfx_crash, 1)
        soundBoost = soundPool.load(context, R.raw.sfx_boost, 1)

        soundPool.setOnLoadCompleteListener { pool, sampleId, status ->
            // Status 0 means success
            Log.d("Audio", "Sound $sampleId loaded with status $status")
        }
    }

    private fun playTrack(resId: Int, isLooping: Boolean = true) {
        // 1. Clean up the old player
        mediaPlayer?.stop()
        mediaPlayer?.release()

        // 2. Initialize new track
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            this.isLooping = isLooping
            setVolume(0.4f, 0.4f)
            start()
        }
    }


    fun startMusic() {
        if (currentTrackResId != bgMusic || mediaPlayer == null || mediaPlayer?.isPlaying == false) {
            currentTrackResId = bgMusic
            playTrack(bgMusic)
        }
    }

    fun startGameOverMusic() {
        if (currentTrackResId != bgGameOver) {
            currentTrackResId = bgGameOver
            playTrack(R.raw.bg_game_over)
        }
    }

    fun playCoinSfx() = soundPool.play(soundCoin, 1f, 1f, 0, 0, 1f)
    fun playCrashSfx() = soundPool.play(soundCrash, 1f, 1f, 0, 0, 1f)
    fun playBoostSfx() = soundPool.play(soundBoost, 1f, 1f, 0, 0, 1f)

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
    }

    fun resumeMusic() {
        if (mediaPlayer?.isPlaying == false) mediaPlayer?.start()
    }

    fun stopAll() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool.release()
    }
}

