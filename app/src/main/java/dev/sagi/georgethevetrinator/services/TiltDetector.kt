package dev.sagi.georgethevetrinator.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dev.sagi.georgethevetrinator.model.enums.MoveDirection
import dev.sagi.georgethevetrinator.utilities.Constants

class TiltDetector(
    context: Context,
    private val onTilt: (direction: MoveDirection) -> Unit,
    private val onFlick: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var xAxis = 0f
    private var lastYAxis = 0f
    private var canMoveAgain = true

    fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val rawX = it.values[0]
            val rawY = it.values[1]

            // 1. Horizontal Tilt Logic
            xAxis = Constants.Sensor.TILT_ALPHA * rawX + (1 - Constants.Sensor.TILT_ALPHA) * xAxis
            if (Math.abs(xAxis) < Constants.Sensor.NEUTRAL_ZONE) canMoveAgain = true

            if (canMoveAgain) processHorizontalTilt(xAxis)

            // 2. Vertical Flick Logic (Boost)
            val deltaY = rawY - lastYAxis
            lastYAxis = rawY
            if (deltaY < -Constants.Sensor.FLICK_THRESHOLD) {
                onFlick()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    private fun processHorizontalTilt(xAxis: Float) {
        if (xAxis > Constants.Sensor.TILT_SIDE_THRESHOLD) {
            onTilt(MoveDirection.LEFT)
            canMoveAgain = false
        } else if (xAxis < -Constants.Sensor.TILT_SIDE_THRESHOLD) {
            onTilt(MoveDirection.RIGHT)
            canMoveAgain = false
        }
    }

}
