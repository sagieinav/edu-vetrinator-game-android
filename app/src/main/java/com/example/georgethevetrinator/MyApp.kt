package com.example.georgethevetrinator

import android.app.Application
import android.hardware.SensorManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.georgethevetrinator.model.entities.GameControls
import com.example.georgethevetrinator.model.logic.AudioManager
import com.example.georgethevetrinator.model.logic.VibrationManager

class MyApp: Application(), DefaultLifecycleObserver {

    lateinit var audioManager: AudioManager
    lateinit var vibrationManager: VibrationManager

    override fun onCreate() {
        super<Application>.onCreate()
        audioManager = AudioManager.getInstance(this)
        vibrationManager = VibrationManager.getInstance(this)

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
