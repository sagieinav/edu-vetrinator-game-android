package dev.sagi.georgethevetrinator.data.local

import android.content.Context
import dev.sagi.georgethevetrinator.utilities.Constants

class SharedPreferencesManager private constructor(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        Constants.SP.DATA_FILE,
        Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun init(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
        }

        fun getInstance(): SharedPreferencesManager {
            return instance ?: throw IllegalStateException("Must call init(context) first")
        }
    }

    fun putString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}