package com.example.georgethevetrinator.ui.game

import android.content.Context
import android.content.Intent
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.georgethevetrinator.R
import com.example.georgethevetrinator.logic.GameManager
import com.example.georgethevetrinator.model.GameMode
import com.example.georgethevetrinator.model.MoveDirection
import com.example.georgethevetrinator.ui.result.ResultActivity
import com.example.georgethevetrinator.utilities.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
//    ======================================== ATTRIBUTES ========================================
    // === GAME MODE (initialized later. Now setting default fallback) ===
    var gameMode = GameMode.NORMAL
    // === GAME MANAGER & GAME RENDERER ===
    private lateinit var gameManager: GameManager
    private lateinit var gameGridRenderer: GameGridRenderer

    // === LINKED VIEWS (STATIC) ===
    private lateinit var heartsView: Array<AppCompatImageView>
    private lateinit var leftButtonView: AppCompatImageButton
    private lateinit var rightButtonView: AppCompatImageButton

    // === GAME GRID ===
    private lateinit var gridView: GridLayout
    private val ROWS = Constants.GameGrid.ROWS
    private val COLS = Constants.GameGrid.COLS

    // === TIMER JOB ===
    private lateinit var timerJob : Job
    private var startTime : Long = 0

    // === VIBRATOR ===
    private lateinit var vibrator: Vibrator


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

        // 2. Find views
        findViews()

        // 3. Initialize game and its views
        initGame()
        initViews()
    }


    // === GAME INITIALIZATION ===
    private fun initGame() {
        // 1. Game Mode
        initGameMode()

        // 2. Game Manager
        gameManager = GameManager(
            heartsView.size,
            ROWS,
            COLS,
            gameMode)

        // 3. Event Callbacks (onCollision, onGameOver)
        initEventCallbacks()
    }

    private fun initGameMode() {
        val modeString: String? = intent.getStringExtra("GAME_MODE")
        if (modeString != null) {
            try {
                gameMode = GameMode.valueOf(modeString)
            }
            catch (e: IllegalArgumentException) { }
        }
    }

    private fun initEventCallbacks() {
        // Initialize the 'onCollision' event:
        gameManager.onCollision = {
            val crashMsg = getString(gameManager.crashMsgResourceId)
            Toast.makeText(this, crashMsg, Toast.LENGTH_SHORT).show()
            vibrateShort()
        }

        // Initialize the 'onGameOver' event:
        gameManager.onGameOver = {
            vibrateLong()

            Log.d(getString(R.string.log_tag_game_status), getString(R.string.game_over_msg))
            timerJob.cancel()
            lifecycleScope.launch {
                delay(1_000)
                finishGame()
            }
        }
    }

    // === TIMER ===
    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerJob = lifecycleScope.launch {
            while (isActive) {
                Log.d("GameLoop", "Tick!")
                delay(Constants.Timer.DELAY)
                advanceGame()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        timerJob.cancel()
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


    // === FIND & INITIALIZE VIEWS
    private fun findViews() {
        gridView = findViewById(R.id.grid_game_board)
        leftButtonView = findViewById(R.id.btn_move_left)
        rightButtonView = findViewById(R.id.btn_move_right)
        heartsView = arrayOf(
            findViewById(R.id.iv_heart1),
            findViewById(R.id.iv_heart2),
            findViewById(R.id.iv_heart3)
        )
    }

    private fun initViews() {
        leftButtonView.setOnClickListener { view: View -> movementInitiated(MoveDirection.LEFT) }
        rightButtonView.setOnClickListener { view: View -> movementInitiated(MoveDirection.RIGHT) }

        // gameGridRenderer will find and init all game board views:
        gameGridRenderer = GameGridRenderer(this, gridView, ROWS, COLS)

        refreshUI()
    }

    private fun refreshUI() {
        // 1. Refresh life count:
        updateLivesUI()
        // 2. Render player:
        gameGridRenderer.renderPlayer(gameManager.player)
        // 3. Render obstacles:
        gameGridRenderer.renderObstacles(gameManager.activeObstacles)
    }

    private fun updateLivesUI() {
        for (i in heartsView.indices) {
            if (i < gameManager.currentHealth) {
                heartsView[i]
                    .setImageResource(R.drawable.ic_heart)
            }
            else {
                heartsView[i]
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

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the grid renderer to prevent memory leaks
        gameGridRenderer.release()
    }
}