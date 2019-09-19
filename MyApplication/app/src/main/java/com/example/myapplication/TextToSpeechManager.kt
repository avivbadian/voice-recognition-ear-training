package com.example.myapplication

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.myapplication.utils.ManualResetEvent
import java.util.*


class TextToSpeechManager(ctx: Context, private var eventListener: TtsNotificationsListener) : TextToSpeech.OnInitListener,
    UtteranceProgressListener() {

    private var monitor = Object()

    override fun onStart(p0: String?) { }

    override fun onError(utteranceId: String?) {
        println("error on $utteranceId")
    }

    override fun onDone(utteranceId: String?) {
        manualResetEvent.set()
    }

    private var tts: TextToSpeech
    private var context: Context = ctx
    private var currentAnswer: String = ""
    private var manualResetEvent = ManualResetEvent(false)

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
            tts.setOnUtteranceProgressListener(this)

            if (status == TextToSpeech.SUCCESS) {
                // set US English as language for tts
                val result = tts.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS","The Language specified is not supported")
                } else {

                }

                eventListener.onSuccessfullyInitialized()

            } else {
                Log.e("TTS", "Initialization Failed")
            }

    }

    fun speak(text: String) {
        synchronized(monitor) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            manualResetEvent.waitOne()
            manualResetEvent.reset()
        }
    }

    fun setCorrectAnswer(answer: String) {
        currentAnswer = answer
    }

    fun submitUserAnswer(userAnswer: String){
        if (userAnswer == currentAnswer.toLowerCase()) {
            speak("Correct.")
        } else {
            speak("Wrong. The correct answer is: $currentAnswer")
        }
    }



    fun destroy() {
        tts.stop()
        tts.shutdown()
    }

    fun errorRecognizing() {
        speak("Could not catch that. Please try again")
    }

    fun generalError() {
        speak("General error.")
    }

    fun askForAnswer() {
        speak("Go")
    }
}