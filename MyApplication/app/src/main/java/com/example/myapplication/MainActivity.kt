package com.example.myapplication

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.speech.RecognizerIntent
import android.content.Intent
import java.util.*
import android.widget.Toast




class MainActivity : AppCompatActivity() {
    private var player: SoundPoolPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = SoundPoolPlayer(this)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        startActivityForResult(intent, 10)
    }

    fun playSound(view: View) {
        player?.playShortResource(R.raw.test1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {

            }
        } else {
            Toast.makeText(applicationContext, "Failed to recognize speech!", Toast.LENGTH_LONG)
                .show()
        }
    }

    // method to loop through results trying to find a number
    private fun getNumberFromResult(results: ArrayList<String>): Int {
        for (str in results) {
            if (getIntNumberFromText(str) !== -1) {
                return getIntNumberFromText(str)
            }
        }
        return -1
    }


    // method to convert string number to integer
    private fun getIntNumberFromText(strNum: String): Int {
        when (strNum) {
            "zero" -> return 0
            "one" -> return 1
            "two" -> return 2
            "three" -> return 3
            "four" -> return 4
            "five" -> return 5
            "six" -> return 6
            "seven" -> return 7
            "eight" -> return 8
            "nine" -> return 9
        }
        return -1
    }
}
