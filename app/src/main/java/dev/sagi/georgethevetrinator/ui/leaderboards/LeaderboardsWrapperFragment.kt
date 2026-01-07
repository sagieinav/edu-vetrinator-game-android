package dev.sagi.georgethevetrinator.ui.leaderboards

import MapFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.databinding.FragmentLeaderboardsWrapperBinding
import dev.sagi.georgethevetrinator.interfaces.Callback_HighScoreClicked

class LeaderboardsWrapperFragment : Fragment() {

    private lateinit var highScoresFragment: HighScoresFragment
    private lateinit var mapFragment: MapFragment

    private var _binding: FragmentLeaderboardsWrapperBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardsWrapperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChildFragments()
    }

    private fun setupChildFragments() {
        highScoresFragment = HighScoresFragment()
        mapFragment = MapFragment()

        // Set the callback so the list can talk to the map
        highScoresFragment.setHighScoreCallback(object : Callback_HighScoreClicked {
            override fun onLeaderboardsItemClicked(lat: Double, lon: Double) {
                mapFragment.zoomToLocation(lat, lon)
            }
        })

        // Use binding to reference the container IDs
        childFragmentManager.beginTransaction()
            .replace(binding.listChildContainer.id, highScoresFragment)
            .replace(binding.mapChildContainer.id, mapFragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}