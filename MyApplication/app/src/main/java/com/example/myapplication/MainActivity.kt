package com.example.myapplication

import android.Manifest
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.AsyncTask
import edu.cmu.pocketsphinx.*
import java.io.IOException
import java.io.File
import java.lang.ref.WeakReference
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.speech.tts.UtteranceProgressListener


enum class Mode {
    None,
    CMajorIntervals
}

class MainActivity : AppCompatActivity(), RecognitionListener, TtsNotificationsListener {
    override fun onSuccessfullyInitialized() {
        InitRecognizerTask(this).execute()
    }

    private val intervalPossibleAnswers = arrayOf("major second",
        "major third", "perfect fourth", "perfect fifth",
        "major sixth", "major seventh", "perfect eighth")

    private var currentMode : Mode = Mode.None

    private val RECORD_REQUEST_CODE = 101
    private val TAG = "MusicalEar"

    /* We only need the keyphrase to start recognition, one menu with list of choices,
       and one word that is required for method switchSearch - it will bring recognizer
       back to listening for the keyphrase*/
    private val KWS_SEARCH = "wakeup"
    private val MENU_SEARCH = "menu"
    private val INTERVALS_SEARCH = "intervals"

    /* Keyword we are looking for to activate recognition */
    private val KEYPHRASE = "begin"

    /* Recognition object */
    private var recognizer: SpeechRecognizer? = null

    private var player: SoundPoolPlayer? = null
    private var ttsMan: TextToSpeechManager? = null
    private var sttParser: VoiceRecognitionParser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
    }

    fun run() {
        player = SoundPoolPlayer()
        chooseMode(Mode.CMajorIntervals)
        ttsMan = TextToSpeechManager(this, this)
        sttParser = VoiceRecognitionParser()
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     *
     * Switch between keyphrase or menu listening
     */
    override fun onPartialResult(hypothesis: Hypothesis?) { }

    private fun newIntervalRiddle() {
        val res = player?.playRandomResource()
        ttsMan?.setCorrectAnswer(res!!)
        ttsMan?.askForAnswer()
    }

    private fun chooseMode(mode: Mode) {
        currentMode = mode
        player?.setMode(this, mode)
    }

    /**
     * This callback is called when we stop the recognizer.
     * Full sentence is recognized
     */
    override fun onResult(hypothesis: Hypothesis?) {
        Log.d("onResult: ", "message: " +   hypothesis?.hypstr)
        val result = hypothesis?.hypstr

        if (result == INTERVALS_SEARCH) {
            reset()
        }

        else if (result == null || !intervalPossibleAnswers.contains(result)) {
            ttsMan?.errorRecognizing()
            recognizer?.startListening(INTERVALS_SEARCH)
        }

        else {
            ttsMan?.submitUserAnswer(result)
            reset()
        }
    }

    override fun onTimeout() { }

    /**
    Custom action on beginning of speech – don’t need any action for now
     */
    override fun onBeginningOfSpeech() { }


    /**
     * Reset back to keyphrase listening, or listen to menu options after end of speech
    */
    override fun onEndOfSpeech() {
        Log.d("onEndOfSpeech", recognizer?.getSearchName())
        recognizer?.stop()
    }

    private fun reset() {
        newIntervalRiddle()
        recognizer?.startListening(INTERVALS_SEARCH)
        Log.d("reset" , "Started listening")
    }

    /**
     * Print out recognition errors
     */
    override fun onError(ex: java.lang.Exception?) {
        println(ex?.message)
    }

    private class InitRecognizerTask internal constructor(context: MainActivity) : AsyncTask<Void, Void, Exception>() {

        private val activityReference: WeakReference<MainActivity> = WeakReference(context)

        override fun doInBackground(vararg p0: Void?): Exception? {
            try {
                val assets = Assets(activityReference.get())
                val assetDir = assets.syncAssets()
                activityReference.get()!!.setupRecognizer(assetDir)
            } catch (ex : IOException) {
                ex.printStackTrace()
                return ex
            }
            return null
        }

        override fun onPostExecute(result: Exception?) {
            if (result != null) {
                println(result.message)
            } else {
                (activityReference).get()!!.ttsMan!!.speak("Welcome.")
                (activityReference).get()!!.reset()
            }
        }
    }

    @Throws(IOException::class)
    private fun setupRecognizer(assetsDir: File) {
        recognizer = SpeechRecognizerSetup.defaultSetup()
            .setAcousticModel(File(assetsDir, "en-us-ptm"))
            .setDictionary(File(assetsDir, "cmudict-en-us.dict"))
//            .setKeywordThreshold(1e-5f)
            // Disable this line if you don't want recognizer to save raw
            // audio files to app's storage
            //.setRawLogDir(assetsDir)
            .recognizer
        recognizer?.addListener(this)
        // Create keyword-activation search.
        recognizer?.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE)

        // Create grammar-based search for selection between demos
        val menuGrammar = File(assetsDir, "menu.gram")
        recognizer?.addGrammarSearch(MENU_SEARCH, menuGrammar)

        // Create your custom grammar-based search
        val intervalsGrammar = File(assetsDir, "intervals.gram")
        recognizer?.addGrammarSearch(INTERVALS_SEARCH, intervalsGrammar)
    }

    public override fun onDestroy() {
        super.onDestroy()
        player?.release()
        ttsMan?.destroy()
        recognizer?.cancel()
        recognizer?.shutdown()

    }

    //region permissions

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access the microphone is required for this app to record audio.")
                    .setTitle("Permission required")

                builder.setPositiveButton("OK"
                ) { dialog, id ->
                    Log.i(TAG, "Clicked")
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                makeRequest()
            }
        } else {
            run()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    run()
                }
            }
        }
    }

    //endregion
}
