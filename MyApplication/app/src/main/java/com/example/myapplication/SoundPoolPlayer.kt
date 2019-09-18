package com.example.myapplication

import android.content.Context
import android.media.SoundPool
import edu.cmu.pocketsphinx.Assets
import java.io.IOException

import java.util.HashMap

class SoundPoolPlayer(pContext: Context) {

    private var mShortPlayer: SoundPool? = null
    private val mSounds = HashMap<String, Int>()

    init {
        // setup player
        mShortPlayer = SoundPool.Builder().setMaxStreams(1).build()
    }

    fun setMode(ctx:Context, mode: Mode) {
        try {
            var files: Array<out String>?
            when (mode) {
                Mode.CMajorIntervals -> {
                    files = ctx.assets.list("audios/Intervals/C Major")
                }
            }

            val size = files?.size!!
            if (size > 0) {
                for (i in 1..size) {
                    mSounds[files[i]] = i
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun playShortResource(piResource: String) {
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