package dev.sagi.georgethevetrinator.services

import android.content.Context
import android.widget.Toast

class SignalManager private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = AudioManager.getInstance(context)
    private val vibrationManager = VibrationManager.getInstance(context)
    private var currentToast: Toast? = null

    companion object {
        @Volatile
        private var instance: SignalManager? = null

        fun init(context: Context): SignalManager {
            return instance ?: synchronized(this) {
                instance ?: SignalManager(context).also { instance = it }
            }
        }

        fun getInstance(): SignalManager {
            return instance ?: throw IllegalStateException("SignalManager must be initialized in MyApp")
        }
    }

    fun showToast(message: String) {
        // Cancel the existing toast if it is currently showing
        currentToast?.cancel()

        // Create and show the new toast:
        currentToast = Toast.makeText(appContext, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    fun startMusic() { audioManager.startMusic() }
    fun pauseMusic() { audioManager.pauseMusic() }

    fun notifyCollision(message: String) {
        vibrationManager.vibrateShort()
        showToast(message)
        audioManager.playCrashSfx()
    }

    fun notifyGameOver() {
        vibrationManager.vibrateLong()
        audioManager.startGameOverMusic()
    }

    fun onCoinCollected() {
        audioManager.playCoinSfx()
    }

    fun onBoostActivated() {
        audioManager.playBoostSfx()
    }
}