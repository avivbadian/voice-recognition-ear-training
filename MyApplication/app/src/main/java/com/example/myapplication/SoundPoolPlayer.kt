package com.example.myapplication

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool

import java.util.HashMap

class SoundPoolPlayer(pContext: Context) {
    private var mShortPlayer: SoundPool? = null
    private val mSounds = HashMap<Int, Int>()

    init {
        // setup player
        mShortPlayer = SoundPool.Builder().setMaxStreams(1).build()
        mSounds[R.raw.up2] = mShortPlayer!!.load(pContext, R.raw.up2, 1)
    }

    fun playShortResource(piResource: Int) {
        val iSoundId = mSounds[piResource] as Int
        mShortPlayer!!.play(iSoundId, 0.99f, 0.99f, 0, 0, 1f)
    }

    // Cleanup
    fun release() {
        // Cleanup
        mShortPlayer!!.release()
        mShortPlayer = null
    }
}