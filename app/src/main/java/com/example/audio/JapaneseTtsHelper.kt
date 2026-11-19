package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class JapaneseTtsHelper(context: Context) {
  private var tts: TextToSpeech? = null
  private var isInitialized = false
  private var isJapaneseAvailable = false

  init {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        val result = tts?.setLanguage(Locale.JAPANESE)
        isJapaneseAvailable =
          result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED
        if (!isJapaneseAvailable) {
          tts?.language = Locale.US
        }
        tts?.setSpeechRate(0.88f)
        isInitialized = true
      }
    }
  }

  /**
   * Speaks Japanese text if TTS engine is ready.
   * Returns true if playback started, false if Japanese TTS is unavailable on device.
   */
  fun speak(text: String): Boolean {
    val engine = tts ?: return false
    if (!isInitialized || text.isBlank()) return false
    engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kotoba_${System.currentTimeMillis()}")
    return isJapaneseAvailable
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
    isInitialized = false
  }
}
