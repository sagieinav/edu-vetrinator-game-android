package com.example.georgethevetrinator.ui.game

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.georgethevetrinator.R
import com.example.georgethevetrinator.logic.GameManager
import com.example.georgethevetrinator.model.entities.GameControls
import com.example.georgethevetrinator.model.entities.GameDifficulty
import com.example.georgethevetrinator.model.entities.GameMode
import com.example.georgethevetrinator.model.entities.MoveDirection
import com.example.georgethevetrinator.model.logic.AudioManager
import com.example.georgethevetrinator.ui.result.ResultActivity
import com.example.georgethevetrinator.utilities.Constants
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity(), SensorEventListener {
//    ======================================== ATTRIBUTES ========================================
    // === GAME SETTINGS (initialized later. Now setting default fallback) ===
    var gameMode = GameMode.NORMAL
    var gameDifficulty = GameDifficulty.EASY
    var gameControls = GameControls.BUTTONS
    // === GAME MANAGER, AUDIO MANAGER, GAME RENDERER ===
    private lateinit var gameManager: GameManager
    private lateinit var audioManager: AudioManager
    private lateinit var gameGridRenderer: GameGridRenderer

    // === LINKED VIEWS (STATIC) ===
    private lateinit var tvScore: MaterialTextView
    private lateinit var viewHearts: Array<AppCompatImageView>
    private lateinit var ivBoostIndicator: AppCompatImageView
    private lateinit var btnMoveLeft: AppCompatImageButton
    private lateinit var btnMoveRight: AppCompatImageButton

    // === GAME GRID ===
    private lateinit var gridView: GridLayout
    private val ROWS = Constants.GameGrid.ROWS
    private val COLS = Constants.GameGrid.COLS

    // === TIMER JOB ===
    private lateinit var timerJob : Job
    private var startTime : Long = 0

    // === VIBRATOR ===
    private lateinit var vibrator: Vibrator

    // === SENSOR MANAGER ===
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var xAxis = 0f
    private var canMoveAgain = true
    private var lastYAxis = 0f
    private var isBoosting = false
    private var lastBoostTime: Long = 0


    //    ======================================== FUNCTIONS ========================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_activity_game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Initialize services
        initVibrator()
        initSensor()
        audioManager = AudioManager(applicationContext)

        // 2. Find views
        findViews()

        // 3. Initialize game and its views
        initGame()
        initViews()
    }


    // === GAME INITIALIZATION ===
    private fun initGame() {
        // 1. Game Mode
        initGameSettings()

        // 2. Game Manager
        gameManager = GameManager(
            viewHearts.size,
            ROWS,
            COLS,
            gameMode,
            gameDifficulty,
            gameControls
        )

        // 3. Event Callbacks (onCollision, onGameOver)
        initEventCallbacks()
    }

    private fun initGameSettings() {
        // 1. Mode
        val modeString: String? = intent.getStringExtra("GAME_MODE")
        if (modeString != null) {
            try {
                gameMode = GameMode.valueOf(modeString)
            }
            catch (e: IllegalArgumentException) { }
        }

        // 2. Difficulty
        val difficultyString: String? = intent.getStringExtra("GAME_DIFFICULTY")
        if (difficultyString != null) {
            try {
                gameDifficulty = GameDifficulty.valueOf(difficultyString)
            }
            catch (e: IllegalArgumentException) { }
        }

        // 3. Controls
        val controlsString: String? = intent.getStringExtra("GAME_CONTROLS")
        if (controlsString != null) {
            try {
                gameControls = GameControls.valueOf(controlsString)
            }
            catch (e: IllegalArgumentException) { }
        }
    }

    private fun initEventCallbacks() {
        // Initialize the 'onCollision' event:
        gameManager.onObstacleHit = {
            audioManager.playCrashSfx()
            val crashMsg = getString(gameManager.crashMsgResourceId)
            Toast.makeText(this, crashMsg, Toast.LENGTH_SHORT).show()
            vibrateShort()
        }

        gameManager.onCoinCollected = {
            audioManager.playCoinSfx()
            val colorFrom = ContextCompat.getColor(this, R.color.brown_george)
            val colorTo = Color.parseColor("#FFD700") // Gold

            // 1. Transition to Gold
            ObjectAnimator.ofInt(tvScore, "textColor", colorFrom, colorTo).apply {
                duration = 250
                setEvaluator(ArgbEvaluator())
                start()
            }

            // 2. Return to Original after 0.8 second
            lifecycleScope.launch {
                delay(800)
                ObjectAnimator.ofInt(tvScore, "textColor", colorTo, colorFrom).apply {
                    duration = 350
                    setEvaluator(ArgbEvaluator())
                    start()
                }
            }
        }

        // Initialize the 'onGameOver' event:
        gameManager.onGameOver = {
            vibrateLong()

            Log.d(getString(R.string.log_tag_game_status), getString(R.string.game_over_msg))
            timerJob.cancel()
            lifecycleScope.launch {
                delay(500)
                finishGame()
            }
        }
    }

    // === TIMER ===
    private fun startTimer() {
        startTime = System.currentTimeMillis()
        val baseDelay = if (gameDifficulty == GameDifficulty.EASY) Constants.Timer.DELAY_EASY else Constants.Timer.DELAY_HARD
        timerJob = lifecycleScope.launch {
            while (isActive) {
                val dynamicDelay = (baseDelay / gameManager.speedMultiplier).toLong()
                Log.d("GameLoop", "Tick!")
                delay(dynamicDelay)
                advanceGame()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        audioManager.startMusic()
        startTimer()
        if (gameControls == GameControls.TILT) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        audioManager.pauseMusic()
        timerJob.cancel()
        if (gameControls == GameControls.TILT) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the grid renderer to prevent memory leaks
        gameGridRenderer.release()

        // Clean up audio manager
        audioManager.stopAll()
    }




    // === VIBRATOR ===
    private fun initVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else { // Backwards compatibility for older Android versions
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    private fun vibrateShort() {
        // Log it to confirm it's working:
        Log.d("VibrationCheck", getString(R.string.vibration_msg))

        vibrator.vibrate(VibrationEffect.createOneShot(
            Constants.GameFeel.VIBRATION_DURATION_SHORT,
            VibrationEffect.DEFAULT_AMPLITUDE)
        )
    }

    private fun vibrateLong() {
        // Log it to confirm it's working:
        Log.d("VibrationCheck", getString(R.string.vibration_msg))

        vibrator.vibrate(VibrationEffect.createOneShot(
            Constants.GameFeel.VIBRATION_DURATION_LONG,
            VibrationEffect.DEFAULT_AMPLITUDE)
        )
    }

    // === SENSOR MANAGER ===
    private fun initSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (gameControls != GameControls.TILT || event == null) return

        val rawXAxis = event.values[0]
        val rawYAxis = event.values[1]
        xAxis = Constants.Sensor.TILT_ALPHA * rawXAxis + (1 - Constants.Sensor.TILT_ALPHA) * xAxis

        if (Math.abs(xAxis) < Constants.Sensor.NEUTRAL_ZONE) canMoveAgain = true
        if (canMoveAgain) processHorizontalTilt(xAxis)

        processVerticalFlick(rawYAxis)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed, implementing empty function to satisfy interface
    }

    fun processHorizontalTilt(xAxis: Float) {
        if (xAxis > Constants.Sensor.TILT_SIDE_THRESHOLD) {
            movementInitiated(MoveDirection.LEFT)
            canMoveAgain = false
        }
        else if (xAxis < -Constants.Sensor.TILT_SIDE_THRESHOLD) {
            movementInitiated(MoveDirection.RIGHT)
            canMoveAgain = false
        }
    }

    private fun processVerticalFlick(currentY: Float) {
        val currentTime = System.currentTimeMillis()
        val deltaY = currentY - lastYAxis
        lastYAxis = currentY

        // 1. Check if already boosting or in cooldown
        if (isBoosting || (currentTime - lastBoostTime < Constants.Sensor.BOOST_COOLDOWN)) {
            return
        }

        // 2. Detect forward flick
        if (deltaY < -Constants.Sensor.FLICK_THRESHOLD) {
            triggerBoost()
        }
    }

private fun triggerBoost() {
    isBoosting = true
    audioManager.playBoostSfx()
    lastBoostTime = System.currentTimeMillis()
    gameManager.speedMultiplier = Constants.Sensor.BOOST_SPEED

    ivBoostIndicator.visibility = View.VISIBLE
    ivBoostIndicator.alpha = 0f
    ivBoostIndicator.scaleX = 0f
    ivBoostIndicator.scaleY = 0f
    ivBoostIndicator.animate()
        .alpha(1f)
        .scaleX(1.2f)
        .scaleY(1.2f)
        .setDuration(200)
        .withEndAction {
            ivBoostIndicator.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
        }.start()

    // Auto-Reset the timer when boost ends
    lifecycleScope.launch {
        delay(Constants.Sensor.BOOST_DURATION)
        gameManager.speedMultiplier = 1.0f

        ivBoostIndicator.animate()
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(300)
            .withEndAction {
                ivBoostIndicator.visibility = View.INVISIBLE
                isBoosting = false
            }.start()
    }
}




    // === FIND & INITIALIZE VIEWS
    private fun findViews() {
        gridView = findViewById(R.id.grid_game_board)
        btnMoveLeft = findViewById(R.id.btn_move_left)
        btnMoveRight = findViewById(R.id.btn_move_right)
        tvScore = findViewById(R.id.tv_score)
        viewHearts = arrayOf(
            findViewById(R.id.iv_heart1),
            findViewById(R.id.iv_heart2),
            findViewById(R.id.iv_heart3)
        )
        ivBoostIndicator = findViewById(R.id.iv_boost_indicator)
    }

    private fun initViews() {
        if (gameControls == GameControls.TILT) {
            findViewById<View>(R.id.layout_controls).visibility = View.GONE
            // initSensor() already been called in onCreate
        }
        else {
            btnMoveLeft.setOnClickListener { view: View -> movementInitiated(MoveDirection.LEFT) }
            btnMoveRight.setOnClickListener { view: View -> movementInitiated(MoveDirection.RIGHT) }
        }

        // gameGridRenderer will find and init all game board views:
        gameGridRenderer = GameGridRenderer(this, gridView, ROWS, COLS)

        refreshUI()
    }

    private fun refreshUI() {
//        tvScore.text = String.format("%06d", gameManager.score)
        tvScore.text = String.format("%d", gameManager.score)

        // 1. Refresh life count:
        updateLivesUI()
        // 2. Render player:
        gameGridRenderer.renderPlayer(gameManager.player)
        // 3. Render obstacles:
        gameGridRenderer.renderObstacles(gameManager.activeEntities)
    }

    private fun updateLivesUI() {
        for (i in viewHearts.indices) {
            if (i < gameManager.currentHealth) {
                viewHearts[i]
                    .setImageResource(R.drawable.ic_heart)
            }
            else {
                val imageView = viewHearts[i]
                viewHearts[i]
                    .setImageResource(R.drawable.ic_heart_empty)
            }
        }
    }


    // === GAME LIFETIME ===
    private fun movementInitiated(clickedDirection: MoveDirection) {
        gameManager.movePlayer(clickedDirection)
        gameGridRenderer.renderPlayer(gameManager.player)
    }


    private fun advanceGame() {
        gameManager.advanceGame()
        refreshUI()
    }


    private fun finishGame() {
        val intent = Intent(this, ResultActivity::class.java)

        startActivity(intent)
        finish()
    }
}