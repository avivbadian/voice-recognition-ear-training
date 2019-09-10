package com.example.myapplication

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.lang.Exception
import java.util.*

class TextToSpeechManager : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech
    private var context: Context

    constructor(ctx : Context) {
        context = ctx
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported")
            } else {
                welcomeUser()
            }

        } else {
            Log.e("TTS", "Initialization Failed")
        }
    }


    private fun welcomeUser() {
        val text = "Welcome."
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun errorMessage(e: Exception) {
        val text = "Error " + e.message
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }


    fun destroy() {
        tts.stop()
        tts.shutdown()
    }
}