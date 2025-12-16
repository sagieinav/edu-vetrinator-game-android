package com.example.georgethevetrinator.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.georgethevetrinator.R
import com.example.georgethevetrinator.model.entities.GameMode
import com.example.georgethevetrinator.ui.game.GameActivity
import com.example.georgethevetrinator.ui.home.HomeActivity


class ResultActivity : AppCompatActivity() {
//    ======================================== ATTRIBUTES ========================================

    // === LINKED VIEWS (STATIC) ===
    private lateinit var restartButtonView: AppCompatImageButton
    private lateinit var toHomeButtonView: AppCompatImageButton

//    ======================================== FUNCTIONS ========================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_activity_result)) { v, insets ->
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
        restartButtonView = findViewById(R.id.btn_restart)
        toHomeButtonView = findViewById(R.id.btn_back_home)
    }

    private fun initViews() {
        restartButtonView.setOnClickListener { view: View -> restartGame() }
        toHomeButtonView.setOnClickListener { view: View -> changeToHome() }
    }


    private fun restartGame() {
        val intent = Intent(this, GameActivity::class.java)
        // Could only reach result screen in normal mode, so restarting in normal mode:
        intent.putExtra("GAME_MODE", GameMode.NORMAL.name)

        startActivity(intent)
        finish()
    }

    private fun changeToHome() {
        val intent = Intent(this, HomeActivity::class.java)

        startActivity(intent)
        finish()
    }

}