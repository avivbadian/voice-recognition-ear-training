package com.example.myapplication.Services

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.speech.SpeechRecognizer
import java.lang.ref.WeakReference

class VoiceCommandService : Service() {

    val MSG_RECOGNIZER_START_LISTENING = 1
    val MSG_RECOGNIZER_CANCEL = 2

    private var audioManager : AudioManager? = null
    private var speechRecognizer : SpeechRecognizer? = null
    private var speecRecognizerIntent : Intent? = null

    override fun onCreate() {
        super.onCreate()

    }


     inner class IncomingHandler : Handler {

         // Weak reference so that the service is not forced to stay in memory
         private var mtarget : WeakReference<VoiceCommandService>

         constructor(target : VoiceCommandService) : super() {
             mtarget = WeakReference(target)
         }

         override fun handleMessage(msg: Message) {
             val target = mtarget.get()

             when (msg.what) {
                 MSG_RECOGNIZER_START_LISTENING ->
             }
         }
     }



    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}