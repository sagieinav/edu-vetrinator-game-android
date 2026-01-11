package dev.sagi.georgethevetrinator.services

import android.util.Log
import kotlinx.coroutines.*

class GameTimer(
    private val scope: CoroutineScope,
    private val onTick: suspend () -> Unit
) {
    private var timerJob: Job? = null
    var isRunning = false
        private set

    fun start(getDelay: () -> Long) {
        if (isRunning) return
        isRunning = true

        timerJob = scope.launch {
            while (isActive) {
                val currentDelay = getDelay()
                Log.d("GameTimer", "Tick with delay: $currentDelay")
                delay(currentDelay)
                onTick()
            }
        }
    }

    fun stop() {
        isRunning = false
        timerJob?.cancel()
        timerJob = null
    }
}