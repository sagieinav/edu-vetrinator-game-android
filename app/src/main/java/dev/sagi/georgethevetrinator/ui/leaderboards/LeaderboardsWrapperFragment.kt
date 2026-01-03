package dev.sagi.georgethevetrinator.ui.leaderboards

import MapFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.interfaces.Callback_HighScoreClicked

class LeaderboardsWrapperFragment : Fragment() {

    private lateinit var highScoresFragment: HighScoresFragment
    private lateinit var mapFragment: MapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboards_wrapper, container, false)

        // Initialize child fragments
        highScoresFragment = HighScoresFragment()
        mapFragment = MapFragment()

        // Set the callback so the list can talk to the map
        highScoresFragment.setHighScoreCallback(object : Callback_HighScoreClicked {
            override fun onLeaderboardsItemClicked(lat: Double, lon: Double) {
                mapFragment.zoomToLocation(lat, lon)
            }
        })

        // Use childFragmentManager to inflate them
        childFragmentManager.beginTransaction()
            .replace(R.id.list_child_container, highScoresFragment)
            .replace(R.id.map_child_container, mapFragment)
            .commit()

        return view
    }
}