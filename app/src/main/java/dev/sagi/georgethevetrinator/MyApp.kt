package dev.sagi.georgethevetrinator

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.sagi.georgethevetrinator.services.AudioManager
import dev.sagi.georgethevetrinator.data.local.ScoreRepository
import dev.sagi.georgethevetrinator.services.VibrationManager
import dev.sagi.georgethevetrinator.data.local.SharedPreferencesManager
import dev.sagi.georgethevetrinator.services.SignalImageLoader
import dev.sagi.georgethevetrinator.services.SignalManager

class MyApp: Application(), DefaultLifecycleObserver {
    lateinit var scoreRepository: ScoreRepository

    override fun onCreate() {
        super<Application>.onCreate()

        scoreRepository = ScoreRepository()
        SharedPreferencesManager.init(this)
        SignalManager.init(this)
        SignalImageLoader.init(this)

        // Listen to the whole app's lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        SignalManager.getInstance().startMusic()
    }

    override fun onStop(owner: LifecycleOwner) {
        SignalManager.getInstance().pauseMusic()
    }
}
