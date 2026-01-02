package com.example.georgethevetrinator.utilities

class Constants() {
    object GameGrid {
        const val ROWS = 7
        const val COLS = 5
    }
    object Timer {
        const val DELAY_EASY: Long = 400L
        const val DELAY_HARD: Long = 250L
    }

    object Sensor {
        const val TILT_ALPHA = 0.2f
        const val TILT_THRESHOLD = 5.0f
        const val NEUTRAL_ZONE = 1.5f
    }

    object GameFeel {
        const val VIBRATION_DURATION_SHORT: Long = 500L
        const val VIBRATION_DURATION_LONG: Long = 1_00L
    }
}
