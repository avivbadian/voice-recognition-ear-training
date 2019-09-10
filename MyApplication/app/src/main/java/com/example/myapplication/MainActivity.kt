package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    private var player: SoundPoolPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = SoundPoolPlayer(this)

    }

    fun playSound(view: View) {
        player?.playShortResource(R.raw.test1)
    }
}
