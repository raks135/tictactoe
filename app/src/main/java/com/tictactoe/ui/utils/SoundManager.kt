package com.tictactoe.ui.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Manages sound effects for the game
 */
class SoundManager(private val context: Context) {
    
    private var soundPool: SoundPool? = null
    private var moveSound: Int = 0
    private var winSound: Int = 0
    private var drawSound: Int = 0
    private var clickSound: Int = 0
    
    private var isEnabled: Boolean = true
    
    init {
        initializeSoundPool()
    }
    
    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Note: In a real app, you would load actual sound files from res/raw
        // For now, we'll use system sounds or create simple tones
    }
    
    fun playMoveSound() {
        if (isEnabled && moveSound != 0) {
            soundPool?.play(moveSound, 1f, 1f, 1, 0, 1f)
        }
    }
    
    fun playWinSound() {
        if (isEnabled && winSound != 0) {
            soundPool?.play(winSound, 1f, 1f, 1, 0, 1f)
        }
    }
    
    fun playDrawSound() {
        if (isEnabled && drawSound != 0) {
            soundPool?.play(drawSound, 1f, 1f, 1, 0, 1f)
        }
    }
    
    fun playClickSound() {
        if (isEnabled && clickSound != 0) {
            soundPool?.play(clickSound, 0.5f, 0.5f, 1, 0, 1f)
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
    
    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
