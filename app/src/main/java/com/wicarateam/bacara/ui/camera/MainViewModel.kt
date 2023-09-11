package com.wicarateam.bacara.ui.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import darren.googlecloudtts.GoogleCloudTTS
import darren.googlecloudtts.parameter.AudioConfig
import darren.googlecloudtts.parameter.AudioEncoding
import darren.googlecloudtts.parameter.VoiceSelectionParams
import io.reactivex.rxjava3.core.Completable

class MainViewModel(application: Application, private val mGoogleCloudTTS: GoogleCloudTTS) :
    AndroidViewModel(application) {

    fun speak(text: String?): Completable {
        return fromCallable { mGoogleCloudTTS.start(text) }
    }

    fun dispose() {
        mGoogleCloudTTS.close()
    }

    fun initTTSVoice(languageCode: String?, voiceName: String?, pitch: Float, speakRate: Float) {
        mGoogleCloudTTS.setVoiceSelectionParams(VoiceSelectionParams(languageCode, voiceName))
            .setAudioConfig(
                AudioConfig(
                    AudioEncoding.MP3,
                    speakRate,
                    pitch
                )
            )
    }

    private fun fromCallable(runnable: Runnable): Completable {
        return Completable.fromCallable {
            runnable.run()
            Void.TYPE
        }
    }
}