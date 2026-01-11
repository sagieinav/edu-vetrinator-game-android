package dev.sagi.georgethevetrinator.services

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dev.sagi.georgethevetrinator.utilities.Constants

class VibrationManager(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrateShort() {
        vibrator.vibrate(VibrationEffect.createOneShot(
            Constants.GameFeel.VIBRATION_DURATION_SHORT,
            VibrationEffect.DEFAULT_AMPLITUDE)
        )
    }

    fun vibrateLong() {
        vibrator.vibrate(VibrationEffect.createOneShot(
            Constants.GameFeel.VIBRATION_DURATION_LONG,
            VibrationEffect.DEFAULT_AMPLITUDE)
        )
    }

}