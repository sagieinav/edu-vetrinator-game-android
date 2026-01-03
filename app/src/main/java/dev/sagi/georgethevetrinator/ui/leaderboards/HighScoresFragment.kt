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
import dev.sagi.georgethevetrinator.interfaces.Callback_HighScoreClicked
import dev.sagi.georgethevetrinator.ui.adapters.ScoreAdapter

class HighScoresFragment : Fragment() {

    private var callback: Callback_HighScoreClicked? = null

    fun setHighScoreCallback(callback: Callback_HighScoreClicked) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_scores, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_leaderboard)

        // Fetch the scores using our ScoreManager
        val scoreManager = (requireActivity().application as MyApp).scoreRepository
        val topScores = scoreManager.getTopScores()

        // Pass the callback to the adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ScoreAdapter(topScores) { record ->
            callback?.onLeaderboardsItemClicked(record.latitude, record.longitude)
        }

        return view
    }
}