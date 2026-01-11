package dev.sagi.georgethevetrinator

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.sagi.georgethevetrinator.data.local.ScoreRepository
import dev.sagi.georgethevetrinator.data.local.SharedPreferencesManager
import dev.sagi.georgethevetrinator.services.LocationService
import dev.sagi.georgethevetrinator.services.ImageLoader
import dev.sagi.georgethevetrinator.services.SignalManager

class MyApp: Application(), DefaultLifecycleObserver {
    lateinit var signalManager: SignalManager
    lateinit var imageLoader: ImageLoader
    lateinit var scoreRepository: ScoreRepository
    lateinit var locationService: LocationService

    override fun onCreate() {
        super<Application>.onCreate()

        val spManager = SharedPreferencesManager(this)
        scoreRepository = ScoreRepository(spManager)
        locationService = LocationService(this)
        signalManager = SignalManager(this)
        imageLoader = ImageLoader(this)

        // Listen to the whole app's lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        signalManager.startMusic()
    }

    override fun onStop(owner: LifecycleOwner) {
        signalManager.pauseMusic()
    }
}
