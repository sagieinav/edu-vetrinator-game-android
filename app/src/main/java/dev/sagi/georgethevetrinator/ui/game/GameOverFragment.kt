package dev.sagi.georgethevetrinator.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import dev.sagi.georgethevetrinator.R
import com.google.android.material.textview.MaterialTextView

class GameOverFragment(): Fragment() {
//    ======================================== NEW INSTANCE CONSTRUCTOR PATTERN ========================================
    companion object {
        fun newInstance(score: Int): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putInt("SCORE_KEY", score) // Stash the score in the bundle
            fragment.arguments = args
            return fragment
        }
    }

    //    ======================================== CALLBACK INTERFACE ========================================
    interface GameOverListener {
        fun onRestartClicked()
        fun onHomeClicked()
        fun onSaveClicked(score: Int)
        fun onScoreRegistered(score: Int, name: String)
    }

    // Attach GameActivity as the listener
    fun setListener(callback: GameOverListener) {
        this.listener = callback
    }

//    ======================================== ATTRIBUTES ========================================
    // === LINKED VIEWS (STATIC) ===
    private lateinit var btnSaveScore: AppCompatImageButton
    private lateinit var btnRestart: AppCompatImageButton
    private lateinit var btnBackHome: AppCompatImageButton
    private lateinit var tvResultScore: MaterialTextView

    private var listener: GameOverListener? = null
    private var finalScore: Int = 0


//    ======================================== FUNCTIONS ========================================
    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_game_over,
            container,
            false
        )
        findViews(view)
        initViews(view)
        return view
    }

    private fun findViews(view: View) {
        btnSaveScore = view.findViewById(R.id.btn_save_score)
        btnRestart = view.findViewById(R.id.btn_restart)
        btnBackHome = view.findViewById(R.id.btn_back_home)
        tvResultScore = view.findViewById(R.id.tv_result_score)
    }


    private fun initViews(view: View) {
        // Get the score from arguments
        finalScore = arguments?.getInt("SCORE_KEY") ?: 0
        tvResultScore.text = finalScore.toString()

        btnRestart.setOnClickListener { listener?.onRestartClicked() }
        btnBackHome.setOnClickListener { listener?.onHomeClicked() }
        btnSaveScore.setOnClickListener { listener?.onSaveClicked(finalScore) }
    }

}