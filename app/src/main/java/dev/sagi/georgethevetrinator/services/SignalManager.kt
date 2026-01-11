package dev.sagi.georgethevetrinator.services

import android.content.Context
import android.widget.Toast

class SignalManager(context: Context) {

    private val appContext = context.applicationContext
    private val audioManager = AudioManager(appContext)
    private val vibrationManager = VibrationManager(appContext)
    private var currentToast: Toast? = null
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