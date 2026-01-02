package com.example.georgethevetrinator.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.georgethevetrinator.R
import com.example.georgethevetrinator.model.entities.GameControls
import com.example.georgethevetrinator.model.entities.GameDifficulty
import com.example.georgethevetrinator.model.entities.GameMode
import com.example.georgethevetrinator.ui.game.GameActivity
import com.google.android.material.textview.MaterialTextView

class HomeActivity : AppCompatActivity() {
//    ======================================== ATTRIBUTES ========================================
    // === LINKED VIEWS (STATIC) ===
    private lateinit var btnPlay: AppCompatImageButton
    private lateinit var switchMode: SwitchCompat
    private lateinit var tvNormal: MaterialTextView
    private lateinit var tvEndless: MaterialTextView

    private lateinit var switchDifficulty: SwitchCompat
    private lateinit var tvEasy: MaterialTextView
    private lateinit var tvHard: MaterialTextView

    private lateinit var switchControls: SwitchCompat
    private lateinit var tvButtons: MaterialTextView
    private lateinit var tvTilt: MaterialTextView

//    ======================================== FUNCTIONS ========================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_activity_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Find views
        findViews()

        // 2. Initialize viers
        initViews()
    }


    // === FIND & INITIALIZE VIEWS
    private fun findViews() {
        btnPlay = findViewById(R.id.btn_play)

        switchMode = findViewById(R.id.switch_mode)
        tvNormal = findViewById(R.id.tv_normal)
        tvEndless = findViewById(R.id.tv_endless)

        switchDifficulty = findViewById(R.id.switch_difficulty)
        tvEasy = findViewById(R.id.tv_easy)
        tvHard = findViewById(R.id.tv_hard)

        switchControls = findViewById(R.id.switch_controls)
        tvButtons = findViewById(R.id.tv_buttons)
        tvTilt = findViewById(R.id.tv_tilt)
    }

    private fun initViews() {
        btnPlay.setOnClickListener { view: View -> startGame(GameMode.NORMAL) }
        initSwitches()
    }

    private fun initSwitches() {
        tvNormal.isActivated = true
        tvEndless.isActivated = false
        switchMode.setOnCheckedChangeListener { _, isChecked ->
            tvNormal.isActivated = !isChecked
            tvEndless.isActivated = isChecked
        }

        tvEasy.isActivated = true
        tvHard.isActivated = false
        switchDifficulty.setOnCheckedChangeListener { _, isChecked ->
            tvEasy.isActivated = !isChecked
            tvHard.isActivated = isChecked
        }

        tvButtons.isActivated = true
        tvTilt.isActivated = false
        switchControls.setOnCheckedChangeListener { _, isChecked ->
            tvButtons.isActivated = !isChecked
            tvTilt.isActivated = isChecked
        }
    }

    private fun startGame(gameMode: GameMode) {
        val intent = Intent(this, GameActivity::class.java)

        val selectedMode = if (switchMode.isChecked) GameMode.ENDLESS.name else GameMode.NORMAL.name
        val selectedDifficulty = if (switchDifficulty.isChecked) GameDifficulty.HARD.name else GameDifficulty.EASY.name
        val selectedControls = if (switchControls.isChecked) GameControls.TILT.name else GameControls.BUTTONS.name
        intent.putExtra("GAME_MODE", selectedMode) // Passing the enum name as a String
        intent.putExtra("GAME_DIFFICULTY", selectedDifficulty)
        intent.putExtra("GAME_CONTROLS", selectedControls)

        startActivity(intent)
        finish()
    }

}