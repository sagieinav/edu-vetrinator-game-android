package com.example.georgethevetrinator.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.georgethevetrinator.R
import com.example.georgethevetrinator.model.GameMode
import com.example.georgethevetrinator.ui.game.GameActivity

class HomeActivity : AppCompatActivity() {
//    ======================================== ATTRIBUTES ========================================
    // === LINKED VIEWS (STATIC) ===
    private lateinit var normalButtonView: AppCompatImageButton
    private lateinit var extendedButtonView: AppCompatImageButton


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
        normalButtonView = findViewById(R.id.btn_normal_mode)
        extendedButtonView = findViewById(R.id.btn_endless_mode)
    }

    private fun initViews() {
        normalButtonView.setOnClickListener { view: View -> gameModeSelected(GameMode.NORMAL) }
        extendedButtonView.setOnClickListener { view: View -> gameModeSelected(GameMode.ENDLESS) }
    }

    private fun gameModeSelected(gameMode: GameMode) {
        startGame(gameMode)
    }

    private fun startGame(gameMode: GameMode) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("GAME_MODE", gameMode.name) // Passing the enum name as a String

        startActivity(intent)
        finish()
    }

}