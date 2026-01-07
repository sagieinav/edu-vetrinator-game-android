package dev.sagi.georgethevetrinator.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import dev.sagi.georgethevetrinator.R
import com.google.android.material.textview.MaterialTextView
import dev.sagi.georgethevetrinator.databinding.ActivityGameBinding
import dev.sagi.georgethevetrinator.databinding.FragmentGameOverBinding

class GameOverFragment(): Fragment() {
    private var _binding: FragmentGameOverBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        _binding = FragmentGameOverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews()
        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        btnSaveScore = binding.btnSaveScore
        btnRestart = binding.btnRestart
        btnBackHome = binding.btnBackHome
        tvResultScore = binding.resultScore.tvResultScore
    }


    private fun initViews() {
        // Get the score from arguments
        finalScore = arguments?.getInt("SCORE_KEY") ?: 0
        tvResultScore.text = finalScore.toString()

        btnRestart.setOnClickListener { listener?.onRestartClicked() }
        btnBackHome.setOnClickListener { listener?.onHomeClicked() }
        btnSaveScore.setOnClickListener { listener?.onSaveClicked(finalScore) }
    }
}