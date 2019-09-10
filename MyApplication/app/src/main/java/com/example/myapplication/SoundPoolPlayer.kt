package com.example.myapplication

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool

import java.util.HashMap

class SoundPoolPlayer(pContext: Context) {
    private var mShortPlayer: SoundPool? = null
    private val mSounds = HashMap<Int, Int>()

    init {
        // setup Soundpool
        mShortPlayer = SoundPool.Builder().setMaxStreams(10).build()
        mSounds[R.raw.test1] = mShortPlayer!!.load(pContext, R.raw.test1, 1)
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