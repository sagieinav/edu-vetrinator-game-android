package dev.sagi.georgethevetrinator.ui.game

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import dev.sagi.georgethevetrinator.MyApp
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.logic.GameManager
import dev.sagi.georgethevetrinator.model.enums.GameControls
import dev.sagi.georgethevetrinator.model.enums.GameDifficulty
import dev.sagi.georgethevetrinator.model.enums.GameMode
import dev.sagi.georgethevetrinator.model.enums.MoveDirection
import dev.sagi.georgethevetrinator.model.entities.ScoreRecord
import dev.sagi.georgethevetrinator.services.TiltDetector
import dev.sagi.georgethevetrinator.utilities.Constants
import com.google.android.material.textview.MaterialTextView
import dev.sagi.georgethevetrinator.databinding.ActivityGameBinding
import dev.sagi.georgethevetrinator.services.GameTimer
import kotlinx.coroutines.delay
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

    // === APPLICATION SERVICES ===
    private val signalManager get() = (application as MyApp).signalManager
    private val scoreRepository get() = (application as MyApp).scoreRepository
    private val locationService get() = (application as MyApp).locationService
    private val imageLoader get() = (application as MyApp).imageLoader

    // === GAME SERVICES ===
    private lateinit var gameTimer: GameTimer
    private lateinit var tiltDetector: TiltDetector
    private var isBoosting = false
    private var lastBoostTime: Long = 0

    // === BINDING ===
    private lateinit var binding: ActivityGameBinding

//    ======================================== FUNCTIONS ========================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Binding:
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Game Services:
        tiltDetector = TiltDetector(
            this,
            onTilt = { direction -> movementInitiated(direction) },
            onFlick = { triggerBoost() }
        )
        gameTimer = GameTimer(lifecycleScope) {
            advanceGame()
        }

        // 3. Find views
        findViews()

        // 4. Initialize game and its views
        initGame()
        initViews()
    }

    override fun onResume() {
        super.onResume()
        val baseDelay = if (gameDifficulty == GameDifficulty.EASY)
            Constants.Timer.DELAY_EASY else Constants.Timer.DELAY_HARD

        gameTimer.start {
            (baseDelay / gameManager.speedMultiplier).toLong()
        }

        if (gameControls == GameControls.TILT) {
            tiltDetector.start()
        }
    }

    override fun onPause() {
        super.onPause()
        gameTimer.stop()
        tiltDetector.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the grid renderer to prevent memory leaks
        gameGridRenderer.release()
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
        val crashMsg = getString(gameManager.crashMsgResourceId)
        signalManager.notifyCollision(crashMsg)
    }

    private fun handleCoinCollected() {
        signalManager.onCoinCollected()
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
        gameTimer.stop()
        signalManager.notifyGameOver()
        showGameOver() // Triggers `GameOverFragment`
    }


    // === VIEWS (FIND, INIT, REFRESH) ===
    private fun findViews() {
        gridView = binding.gridGameBoard
        btnMoveLeft = binding.layoutControls.btnMoveLeft
        btnMoveRight = binding.layoutControls.btnMoveRight
        tvScore = binding.layoutHud.tvScore
        viewHearts = arrayOf(
            binding.layoutHud.ivHeart1,
            binding.layoutHud.ivHeart2,
            binding.layoutHud.ivHeart3
        )
        ivBoostIndicator = binding.layoutHud.ivBoostIndicator
    }

    private fun initViews() {
        if (gameControls == GameControls.TILT) {
            binding.layoutControls.root.visibility = View.GONE
            // initSensor() already been called in onCreate
        }
        else {
            btnMoveLeft.setOnClickListener { view: View -> movementInitiated(MoveDirection.LEFT) }
            btnMoveRight.setOnClickListener { view: View -> movementInitiated(MoveDirection.RIGHT) }
        }

        // gameGridRenderer will find and init all game board views:
        gameGridRenderer = GameGridRenderer(
            this,
            gridView,
            ROWS,
            COLS,
            imageLoader)

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
                imageLoader.loadImage(R.drawable.ic_heart, viewHearts[i])
            }
            else {
                imageLoader.loadImage(R.drawable.ic_heart_empty, viewHearts[i])
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
        signalManager.onBoostActivated()
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
            .replace(binding.gameFragmentContainer.id, gameOverFragment)
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
            .replace(binding.gameFragmentContainer.id, registerFragment)
            .addToBackStack(null) // Allows "Cancel" to work via popBackStack
            .commit()
    }


    override fun onScoreRegistered(score: Int, name: String) {
        if (hasLocationPermission()) {
            locationService.getCurrentLocation { lat, lon ->
                val newRecord = ScoreRecord(name, score, lat, lon)
                scoreRepository.saveScore(newRecord)
            }
        } else {
            val newRecord = ScoreRecord(name, score, 0.0, 0.0)
            scoreRepository.saveScore(newRecord)
        }

        onHomeClicked()
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRestartClicked() {
        signalManager.startMusic()
        // 1. Remove the GameOverFragment
        val fragment = supportFragmentManager.findFragmentById(binding.gameFragmentContainer.id)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }

        // 2. Reset the logic via GameManager
        gameManager.resetGame()

        // 3. Clear the UI
        refreshUI()

        // 4. Restart the timer
        val baseDelay = if (gameDifficulty == GameDifficulty.EASY)
            Constants.Timer.DELAY_EASY else Constants.Timer.DELAY_HARD

        gameTimer.start {
            (baseDelay / gameManager.speedMultiplier).toLong()
        }
    }

    override fun onHomeClicked() {
        signalManager.startMusic()
        finish()
    }
}