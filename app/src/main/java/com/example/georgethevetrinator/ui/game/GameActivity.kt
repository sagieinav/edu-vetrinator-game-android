package com.example.georgethevetrinator.ui.game

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.georgethevetrinator.MyApp
import com.example.georgethevetrinator.R
import com.example.georgethevetrinator.logic.GameManager
import com.example.georgethevetrinator.model.enums.GameControls
import com.example.georgethevetrinator.model.enums.GameDifficulty
import com.example.georgethevetrinator.model.enums.GameMode
import com.example.georgethevetrinator.model.enums.MoveDirection
import com.example.georgethevetrinator.model.entities.ScoreRecord
import com.example.georgethevetrinator.logic.TiltDetector
import com.example.georgethevetrinator.utilities.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity(), GameOverFragment.GameOverListener {
//    ======================================== ATTRIBUTES ========================================
    // === GAME SETTINGS & GAME MANAGER (initialized later. Now setting default fallback) ===
    private lateinit var gameManager: GameManager
    var gameMode = GameMode.NORMAL
    var gameDifficulty = GameDifficulty.EASY
    var gameControls = GameControls.BUTTONS

    // === GAME GRID ===
    private lateinit var gameGridRenderer: GameGridRenderer
    private lateinit var gridView: GridLayout
    private val ROWS = Constants.GameGrid.ROWS
    private val COLS = Constants.GameGrid.COLS

    // === LINKED VIEWS (STATIC) ===
    private lateinit var tvScore: MaterialTextView
    private lateinit var viewHearts: Array<AppCompatImageView>
    private lateinit var ivBoostIndicator: AppCompatImageView
    private lateinit var btnMoveLeft: AppCompatImageButton
    private lateinit var btnMoveRight: AppCompatImageButton

    // === TIMER ===
    private var timerJob: Job? = null
    private var startTime : Long = 0

    // === AUDIO MANAGER ===
    private val audioManager by lazy {
        (application as MyApp).audioManager
    }

    // === VIBRATION MANAGER ===
    private val vibrationManager by lazy {
        (application as MyApp).vibrationManager
    }

    // === TILT DETECTOR ===
    private lateinit var tiltDetector: TiltDetector
    private var isBoosting = false
    private var lastBoostTime: Long = 0

    // === LOCATION ===
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // === TOAST ===
    private var currentToast: Toast? = null


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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tiltDetector = TiltDetector(
            this,
            onTilt = { direction -> movementInitiated(direction) },
            onFlick = { triggerBoost() }
        )

        // 2. Find views
        findViews()

        // 3. Initialize game and its views
        initGame()
        initViews()
    }

    override fun onResume() {
        super.onResume()
        startTimer()
        if (gameControls == GameControls.TILT) {
            tiltDetector.start()
        }
    }

    override fun onPause() {
        super.onPause()
        timerJob?.cancel()
        tiltDetector.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the grid renderer to prevent memory leaks
        gameGridRenderer.release()
    }

    fun showToast(message: String) {
        // Cancel the existing toast if it is currently showing
        currentToast?.cancel()

        // Create and show the new toast:
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
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

        // 3. Internal Event Callbacks
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
        gameManager.onObstacleHit = { handleObstacleHit() }
        gameManager.onCoinCollected = { handleCoinCollected() }
        gameManager.onGameOver = { handleGameOver() }
    }

    private fun handleObstacleHit() {
        audioManager.playCrashSfx()
        val crashMsg = getString(gameManager.crashMsgResourceId)
        showToast(crashMsg)
        vibrationManager.vibrateShort()
    }

    private fun handleCoinCollected() {
        audioManager.playCoinSfx()
        animateScoreCoin()
    }

    private fun animateScoreCoin() {
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

    private fun handleGameOver() {
        vibrationManager.vibrateLong()
        timerJob?.cancel()
        audioManager.startGameOverMusic()
        showGameOver() // Triggers `GameOverFragment`
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

    // === VIEWS (FIND, INIT, REFRESH) ===
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
        tvScore.text = gameManager.score.toString()

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
    private fun advanceGame() {
        gameManager.advanceGame()
        refreshUI()
    }

    // === GAME OVER FRAGMENT CALLBACKS ===
    private fun showGameOver() {
        val gameOverFragment = GameOverFragment.newInstance(gameManager.score)
        gameOverFragment.setListener(this)

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
            .replace(R.id.game_fragment_container, gameOverFragment)
            .commit()
    }

    override fun onSaveClicked(score: Int) {
        navigateToRegistration(score)
    }

    private fun navigateToRegistration(score: Int) {
        val registerFragment = RegisterScoreFragment.newInstance(score)

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .replace(R.id.game_fragment_container, registerFragment)
            .addToBackStack(null) // Allows "Cancel" to work via popBackStack
            .commit()
    }


    override fun onScoreRegistered(score: Int, name: String) {
        // Check for permissions before requesting location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude ?: 0.0
                val lon = location?.longitude ?: 0.0

                val newRecord = ScoreRecord(name, score, lat, lon)
                (application as MyApp).scoreRepository.saveScore(newRecord)
                Log.d("LOCATION_DEBUG", "Saved with: $lat, $lon")
            }
        } else {
            // If no permission, save with 0.0, 0.0
            val newRecord = ScoreRecord(name, score)
            (application as MyApp).scoreRepository.saveScore(newRecord)
            Log.d("LOCATION_DEBUG", "No permission!")
        }
        onHomeClicked()
    }

    override fun onRestartClicked() {
        audioManager.startMusic()
        // 1. Remove the GameOverFragment
        val fragment = supportFragmentManager.findFragmentById(R.id.game_fragment_container)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }

        // 2. Reset the logic via GameManager
        gameManager.resetGame()

        // 3. Clear the UI
        refreshUI()

        // 4. Restart the loop
        startTimer()
    }

    override fun onHomeClicked() {
        audioManager.startMusic()
        finish()
    }
}