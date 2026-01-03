package com.example.georgethevetrinator.model.logic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.georgethevetrinator.utilities.Constants

class VibrationManager private constructor(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    companion object {
        @Volatile
        private var instance: VibrationManager? = null

        fun getInstance(context: Context): VibrationManager {
            return instance ?: synchronized(this) {
                instance ?: VibrationManager(context.applicationContext).also { instance = it }
            }
        }
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