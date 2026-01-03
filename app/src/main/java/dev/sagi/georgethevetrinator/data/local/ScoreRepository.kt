package dev.sagi.georgethevetrinator.data.local

import android.util.Log
import dev.sagi.georgethevetrinator.model.entities.ScoreRecord
import dev.sagi.georgethevetrinator.utilities.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreRepository {
    private val gson = Gson()

    fun saveScore(newRecord: ScoreRecord) {
        val currentScores = getTopScores().toMutableList()
        currentScores.add(newRecord)

        val topTen = currentScores.sortedByDescending { it.score }.take(10)

        val topTenAsJson: String = gson.toJson(topTen)
        Log.d("SAVE_CHECK", "JSON to save: $topTenAsJson") // Check if JSON is actually created
        // Use the Singleton Manager we just updated
        SharedPreferencesManager.Companion.getInstance().putString(Constants.SP.LEADERBOARDS_KEY, topTenAsJson)
        Log.d("SAVE_CHECK", "Result saved!")
    }

    fun getTopScores(): List<ScoreRecord> {
        val topTenAsJson = SharedPreferencesManager.Companion.getInstance().getString(Constants.SP.LEADERBOARDS_KEY, "")
        if (topTenAsJson.isEmpty()) return emptyList()

        val type = object : TypeToken<List<ScoreRecord>>() {}.type
        return gson.fromJson(topTenAsJson, type)
    }
}