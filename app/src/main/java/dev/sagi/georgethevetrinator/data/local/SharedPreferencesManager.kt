package dev.sagi.georgethevetrinator.data.local

import android.content.Context
import dev.sagi.georgethevetrinator.utilities.Constants

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        Constants.SP.DATA_FILE,
        Context.MODE_PRIVATE
    )

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}