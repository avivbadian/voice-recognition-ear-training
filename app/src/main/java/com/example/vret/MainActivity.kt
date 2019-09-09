package com.example.vret

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener { playMusic() }
    }

    fun playMusic() {

        mediaPlayer = MediaPlayer.create(this, R.raw.test1)

        mediaPlayer?.setOnPreparedListener {
            println("starts playing")
            mediaPlayer?.start()
        }

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
        }
    }
}
