package com.example.georgethevetrinator.ui.game

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.georgethevetrinator.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class RegisterScoreFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etName = view.findViewById<TextInputEditText>(R.id.et_enter_name)

        // 1. Request focus for the view
        etName.requestFocus()

        // 2. Post a delayed command to show the keyboard
        etName.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_score, container, false)

        // 1. Get score from arguments
        score = arguments?.getInt("SCORE_KEY") ?: 0

        // 2. Find and setup Score View
        val scoreView = view.findViewById<View>(R.id.register_score_view)
        val tvFinalScore = scoreView.findViewById<MaterialTextView>(R.id.tv_result_score)
        tvFinalScore.text = score.toString()

        // 3. UI Elements
        val etName = view.findViewById<TextInputEditText>(R.id.et_enter_name)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btn_confirm_register)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel_register)

        // Confirm Click
        btnConfirm.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                Log.d("RegisterFragment", "Button clicked! Name: $name")
                (activity as? GameOverFragment.GameOverListener)?.onScoreRegistered(score, name)
            } else {
                etName.error = "Name cannot be empty"
            }
        }

        // Cancel Click
        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack() // Go back to GameOverFragment
        }

        return view
    }
}