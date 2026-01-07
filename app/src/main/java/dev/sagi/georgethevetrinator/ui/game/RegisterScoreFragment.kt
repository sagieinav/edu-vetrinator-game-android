package dev.sagi.georgethevetrinator.ui.game

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import dev.sagi.georgethevetrinator.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import dev.sagi.georgethevetrinator.databinding.FragmentGameOverBinding
import dev.sagi.georgethevetrinator.databinding.FragmentRegisterScoreBinding

class RegisterScoreFragment : Fragment() {
    private var _binding: FragmentRegisterScoreBinding? = null
    private val binding get() = _binding!!
    private var score: Int = 0

    companion object {
        fun newInstance(score: Int): RegisterScoreFragment {
            val fragment = RegisterScoreFragment()
            val args = Bundle()
            args.putInt("SCORE_KEY", score)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        score = arguments?.getInt("SCORE_KEY") ?: 0
        setupScoreUI()
        setupButtons()
        setupKeyboardAutoShow()
    }

    private fun setupScoreUI() {
        // Accessing nested included layout: register_score_view -> tv_result_score
        binding.registerScoreView.tvResultScore.text = score.toString()
    }

    private fun setupButtons() {
        binding.apply {
            btnConfirmRegister.setOnClickListener {
                val name = etEnterName.text.toString().trim()
                if (name.isNotEmpty()) {
                    Log.d("RegisterFragment", "Button clicked. Name: ${name}")
                    (activity as? GameOverFragment.GameOverListener)?.onScoreRegistered(score, name)
                } else {
                    etEnterName.error = "Name cant be empty"
                }
            }

            btnCancelRegister.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupKeyboardAutoShow() {
        binding.etEnterName.apply {
            requestFocus()
            postDelayed({
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }, 200)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 4. Critical cleanup for fragments
        _binding = null
    }
}