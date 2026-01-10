package com.tictactoe.data

import android.content.Context
import android.content.SharedPreferences
import com.tictactoe.domain.Difficulty

/**
 * Manages app preferences and settings
 */
class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "tictactoe_prefs"
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        private const val KEY_THEME = "theme"
        private const val KEY_WINS_X = "wins_x"
        private const val KEY_WINS_O = "wins_o"
        private const val KEY_DRAWS = "draws"
        private const val KEY_GAMES_PLAYED = "games_played"
    }
    
    // Settings
    var difficulty: Difficulty
        get() = Difficulty.valueOf(prefs.getString(KEY_DIFFICULTY, Difficulty.MEDIUM.name)!!)
        set(value) = prefs.edit().putString(KEY_DIFFICULTY, value.name).apply()
    
    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()
    
    var hapticEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC_ENABLED, value).apply()
    
    var theme: String
        get() = prefs.getString(KEY_THEME, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()
    
    // Statistics
    var winsX: Int
        get() = prefs.getInt(KEY_WINS_X, 0)
        set(value) = prefs.edit().putInt(KEY_WINS_X, value).apply()
    
    var winsO: Int
        get() = prefs.getInt(KEY_WINS_O, 0)
        set(value) = prefs.edit().putInt(KEY_WINS_O, value).apply()
    
    var draws: Int
        get() = prefs.getInt(KEY_DRAWS, 0)
        set(value) = prefs.edit().putInt(KEY_DRAWS, value).apply()
    
    var gamesPlayed: Int
        get() = prefs.getInt(KEY_GAMES_PLAYED, 0)
        set(value) = prefs.edit().putInt(KEY_GAMES_PLAYED, value).apply()
    
    /**
     * Reset all statistics
     */
    fun resetStatistics() {
        prefs.edit().apply {
            putInt(KEY_WINS_X, 0)
            putInt(KEY_WINS_O, 0)
            putInt(KEY_DRAWS, 0)
            putInt(KEY_GAMES_PLAYED, 0)
            apply()
        }
    }
}
