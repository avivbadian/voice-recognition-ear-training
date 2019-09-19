package com.example.myapplication

import android.content.Context
import android.media.SoundPool
import java.util.*
import kotlin.collections.ArrayList
import java.io.*


class SoundPoolPlayer {

    private var mShortPlayer: SoundPool?
    private val mSounds = HashMap<String, AudioSound>()

    init {
        // setup player
        mShortPlayer = SoundPool.Builder().setMaxStreams(1).build()
    }

    fun setMode(ctx:Context, mode: Mode) {
        var files: Array<out String>? = null
        try {
            when (mode) {
                Mode.CMajorIntervals -> {
                    files = ctx.assets.list("audios/Intervals/C Major")
                }
            }

            val size = files?.size!!
            if (size > 0) {
                for (i in 0 until size) {
                    val fileNameNoExt = files[i].substring(0, files[i].lastIndexOf('.'))
                    val descriptor = ctx.assets.openFd("audios/Intervals/C Major/" + files[i])
                    val soundId = mShortPlayer?.load(descriptor, 0)
                    // TODO: Get precise duration
                    mSounds[fileNameNoExt] = AudioSound(4000, soundId!!)
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun playRandomResource() : String {

        val randomIndex = (0 until mSounds.size).random()
        val resourcesList = ArrayList<String>(mSounds.keys)
        val randomResourceName = resourcesList[randomIndex]
        val randomResourceVal  = mSounds[randomResourceName]
        mShortPlayer!!.play(randomResourceVal!!.id, 0.99f, 0.99f, 0, 0, 1f)
        Thread.sleep(randomResourceVal.duration.toLong())
        return randomResourceName
    }

    fun playSelectedResource(piResource: String) {
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