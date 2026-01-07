package dev.sagi.georgethevetrinator.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.sagi.georgethevetrinator.MyApp
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.model.enums.GameControls
import dev.sagi.georgethevetrinator.model.enums.GameDifficulty
import dev.sagi.georgethevetrinator.model.enums.GameMode
import dev.sagi.georgethevetrinator.ui.game.GameActivity
import dev.sagi.georgethevetrinator.utilities.navigateToLeaderboards
import com.google.android.material.textview.MaterialTextView
import dev.sagi.georgethevetrinator.databinding.ActivityGameBinding
import dev.sagi.georgethevetrinator.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
//    ======================================== ATTRIBUTES ========================================
    // === SERVICES ===
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            Log.d("PERMISSIONS", "Location Granted!")
        }
    }

    // === LINKED VIEWS (STATIC) ===
    private lateinit var btnPlay: AppCompatImageButton
    private lateinit var btnLeaderboards: AppCompatImageButton
    private lateinit var switchMode: SwitchCompat
    private lateinit var tvNormal: MaterialTextView
    private lateinit var tvEndless: MaterialTextView

    private lateinit var switchDifficulty: SwitchCompat
    private lateinit var tvEasy: MaterialTextView
    private lateinit var tvHard: MaterialTextView

    private lateinit var switchControls: SwitchCompat
    private lateinit var tvButtons: MaterialTextView
    private lateinit var tvTilt: MaterialTextView

    // === BINDING ===
    private lateinit var binding: ActivityHomeBinding

//    ======================================== FUNCTIONS ========================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Services & Permissions
        checkLocationPermissions()

        // 3. Find views
        findViews()

        // 4. Initialize views
        initViews()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }


    // === FIND & INITIALIZE VIEWS
    private fun findViews() {
        btnPlay = binding.btnPlay
        btnLeaderboards = binding.btnLeaderboards

        switchMode = binding.homeSettings.rowMode.switchMode
        tvNormal = binding.homeSettings.rowMode.tvNormal
        tvEndless = binding.homeSettings.rowMode.tvEndless

        switchDifficulty = binding.homeSettings.rowDifficulty.switchDifficulty
        tvEasy = binding.homeSettings.rowDifficulty.tvEasy
        tvHard = binding.homeSettings.rowDifficulty.tvHard

        switchControls = binding.homeSettings.rowControls.switchControls
        tvButtons = binding.homeSettings.rowControls.tvButtons
        tvTilt = binding.homeSettings.rowControls.tvTilt
    }

    private fun initViews() {
        btnPlay.setOnClickListener { view: View -> startGame() }
        btnLeaderboards.setOnClickListener { navigateToLeaderboards(binding.homeFragmentContainer.id) }
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

    private fun startGame() {
        val intent = Intent(this, GameActivity::class.java)

        val selectedMode = if (switchMode.isChecked) GameMode.ENDLESS.name else GameMode.NORMAL.name
        val selectedDifficulty = if (switchDifficulty.isChecked) GameDifficulty.HARD.name else GameDifficulty.EASY.name
        val selectedControls = if (switchControls.isChecked) GameControls.TILT.name else GameControls.BUTTONS.name
        intent.putExtra("GAME_MODE", selectedMode) // Passing the enum name as a String
        intent.putExtra("GAME_DIFFICULTY", selectedDifficulty)
        intent.putExtra("GAME_CONTROLS", selectedControls)

        startActivity(intent)
    }

}