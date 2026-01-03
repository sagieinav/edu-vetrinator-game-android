package dev.sagi.georgethevetrinator

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.sagi.georgethevetrinator.logic.AudioManager
import dev.sagi.georgethevetrinator.data.local.ScoreRepository
import dev.sagi.georgethevetrinator.logic.VibrationManager
import dev.sagi.georgethevetrinator.data.local.SharedPreferencesManager
import dev.sagi.georgethevetrinator.utilities.SignalImageLoader

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
        SignalImageLoader.init(this)

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
