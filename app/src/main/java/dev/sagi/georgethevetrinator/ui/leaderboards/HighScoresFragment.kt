package dev.sagi.georgethevetrinator.ui.leaderboards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.sagi.georgethevetrinator.MyApp
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.databinding.FragmentHighScoresBinding
import dev.sagi.georgethevetrinator.interfaces.Callback_HighScoreClicked
import dev.sagi.georgethevetrinator.ui.adapters.ScoreAdapter

class HighScoresFragment : Fragment() {

    private var callback: Callback_HighScoreClicked? = null
    private var _binding: FragmentHighScoresBinding? = null
    private val binding get() = _binding!!

    fun setHighScoreCallback(callback: Callback_HighScoreClicked) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHighScoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Fetch the scores using the score repository
        val scoreRepository = (requireActivity().application as MyApp).scoreRepository
        val topScores = scoreRepository.getTopScores()

        // Access RecyclerView from the binding
        binding.rvLeaderboard.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ScoreAdapter(topScores) { record ->
                callback?.onLeaderboardsItemClicked(record.latitude, record.longitude)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 5. CRITICAL: Null out the binding to avoid memory leaks
        _binding = null
    }
}