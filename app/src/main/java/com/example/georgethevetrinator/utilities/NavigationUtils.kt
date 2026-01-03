package com.example.georgethevetrinator.utilities

import androidx.appcompat.app.AppCompatActivity
import com.example.georgethevetrinator.ui.leaderboards.LeaderboardsWrapperFragment


fun AppCompatActivity.navigateToLeaderboards(containerId: Int) {
    val fragment = LeaderboardsWrapperFragment()
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(containerId, fragment)
        .addToBackStack(null)
        .commit()
}