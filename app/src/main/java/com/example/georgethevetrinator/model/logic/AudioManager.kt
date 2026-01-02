package com.example.georgethevetrinator.model.logic

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.example.georgethevetrinator.R

class AudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build())
        .build()

    // Sound IDs
    private var soundCoin: Int = 0
    private var soundCrash: Int = 0
    private var soundBoost: Int = 0

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

    fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.bg_music).apply {
                isLooping = true
                setVolume(0.3f, 0.3f)
            }
        }
        mediaPlayer?.start()
    }

    fun playCoinSfx() = soundPool.play(soundCoin, 1f, 1f, 0, 0, 1f)
    fun playCrashSfx() = soundPool.play(soundCrash, 1f, 1f, 0, 0, 1f)
    fun playBoostSfx() = soundPool.play(soundBoost, 1f, 1f, 0, 0, 1f)

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }
    fun stopAll() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        soundPool.release()
    }

}