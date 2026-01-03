package dev.sagi.georgethevetrinator.utilities

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
        const val TILT_SIDE_THRESHOLD = 5.0f
        const val NEUTRAL_ZONE = 1.5f

        const val BOOST_SPEED = 2.0f
        const val BOOST_DURATION = 1500L
        const val BOOST_COOLDOWN = BOOST_DURATION + 500L
        const val FLICK_THRESHOLD = 6.0f
    }

    object Game {
        const val COIN_BONUS: Int = 100
        const val BASE_OBSTACLE_PROB = 0.5
        const val COIN_PROB = 0.2
    }

    object GameFeel {
        const val VIBRATION_DURATION_SHORT: Long = 300L
        const val VIBRATION_DURATION_LONG: Long = 1_000L
    }

    object SP {
        const val DATA_FILE = "data_file"
        const val LEADERBOARDS_KEY = "leaderboards_key"
    }
}
