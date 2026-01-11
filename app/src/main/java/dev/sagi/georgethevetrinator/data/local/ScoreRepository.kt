package dev.sagi.georgethevetrinator.data.local

import android.content.Context
import android.util.Log
import dev.sagi.georgethevetrinator.model.entities.ScoreRecord
import dev.sagi.georgethevetrinator.utilities.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreRepository(
    private val spManager: SharedPreferencesManager
) {
    private val gson = Gson()

    fun saveScore(newRecord: ScoreRecord) {
        val currentScores = getTopScores().toMutableList()
        currentScores.add(newRecord)

        val topTen = currentScores.sortedByDescending { it.score }.take(10)

        val topTenAsJson: String = gson.toJson(topTen)


        spManager.putString(Constants.SP.LEADERBOARDS_KEY, topTenAsJson)
    }

    fun getTopScores(): List<ScoreRecord> {
        val topTenAsJson = spManager.getString(Constants.SP.LEADERBOARDS_KEY, "")
        if (topTenAsJson.isEmpty()) return emptyList()

        val type = object : TypeToken<List<ScoreRecord>>() {}.type
        return gson.fromJson(topTenAsJson, type)
    }
}