package com.example.georgethevetrinator

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.georgethevetrinator.logic.AudioManager
import com.example.georgethevetrinator.data.local.ScoreRepository
import com.example.georgethevetrinator.logic.VibrationManager
import com.example.georgethevetrinator.data.local.SharedPreferencesManager

class MyApp: Application(), DefaultLifecycleObserver {

    lateinit var audioManager: AudioManager
    lateinit var vibrationManager: VibrationManager
    lateinit var scoreRepository: ScoreRepository

    override fun onCreate() {
        super<Application>.onCreate()

        audioManager = AudioManager.getInstance(this)
        vibrationManager = VibrationManager.getInstance(this)
        scoreRepository = ScoreRepository()
        SharedPreferencesManager.init(this)

        // Register to listen to the whole app's lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        // App comes to foreground
        audioManager.startMusic()
    }

    override fun onStop(owner: LifecycleOwner) {
        // App goes to background (Home button/Task switcher)
        audioManager.pauseMusic()
    }
}
