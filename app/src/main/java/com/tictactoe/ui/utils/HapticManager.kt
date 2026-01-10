package com.tictactoe.ui.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Manages haptic feedback for the game
 */
class HapticManager(private val context: Context) {
    
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    private var isEnabled: Boolean = true
    
    /**
     * Light tap feedback for move placement
     */
    fun lightTap() {
        if (!isEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(50)
        }
    }
    
    /**
     * Medium feedback for invalid moves or errors
     */
    fun mediumFeedback() {
        if (!isEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(100)
        }
    }
    
    /**
     * Success pattern for winning
     */
    fun successPattern() {
        if (!isEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 100, 50, 100, 50, 200)
            val amplitudes = intArrayOf(0, 128, 0, 128, 0, 255)
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern, amplitudes, -1)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 100, 50, 100, 50, 200), -1)
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
}
