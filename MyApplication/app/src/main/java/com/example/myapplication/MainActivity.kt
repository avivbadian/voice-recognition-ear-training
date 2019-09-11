package com.example.myapplication

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SPEECH_INTENT = 100

    private var player: SoundPoolPlayer? = null
    private var ttsMan: TextToSpeechManager? = null
    private var sttParser: VoiceRecognitionParser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = SoundPoolPlayer(this)
        ttsMan = TextToSpeechManager(this)
        sttParser = VoiceRecognitionParser()
        takeInput()
    }

    private fun takeInput() {
        // Trying to catch user voice
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)

        try{
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INTENT)
        } catch (e: Exception){
            // If there is an error message let the user know through tts
            ttsMan?.errorMessage(e)
        }
    }


    fun playSound(view: View) {
        player?.playShortResource(R.raw.up2)
    }

    public override fun onDestroy() {
        player?.release()
        ttsMan?.destroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_SPEECH_INTENT -> {
                    // Get text from result
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val number = sttParser?.getNumberFromResult(result)
                    println(number)
                }
            }
        } else {
            Toast.makeText(applicationContext, "Failed to recognize speech!", Toast.LENGTH_LONG)
                .show()
        }
    }
}
