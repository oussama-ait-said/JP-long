package com.example.data.curriculum

import com.example.data.model.JlptLevel

data class KanaChar(
  val id: String,
  val symbol: String,
  val romaji: String,
  val arabicPhonetic: String,
  val rowTitle: String,
  val mnemonicArabic: String,
  val isKatakana: Boolean = false,
)

data class VocabItem(
  val id: String,
  val japanese: String,
  val reading: String,
  val romaji: String,
  val arabicMeaning: String,
  val exampleJp: String,
  val exampleReading: String,
  val exampleAr: String,
  val level: JlptLevel,
)

data class KanjiItem(
  val id: String,
  val character: String,
  val onyomi: String,
  val kunyomi: String,
  val arabicMeaning: String,
  val strokeCount: Int,
  val sampleWord: String,
  val sampleReading: String,
  val sampleMeaning: String,
  val level: JlptLevel,
)

data class GrammarExample(
  val japanese: String,
  val reading: String,
  val romaji: String,
  val arabicTranslation: String,
)

data class GrammarPoint(
  val id: String,
  val pattern: String,
  val romaji: String,
  val arabicTitle: String,
  val explanationArabic: String,
  val formulaArabic: String,
  val examples: List<GrammarExample>,
  val level: JlptLevel,
)

data class Lesson(
  val id: String,
  val order: Int,
  val level: JlptLevel,
  val titleArabic: String,
  val subtitleArabic: String,
  val xpReward: Long,
  val grammarPoints: List<GrammarPoint>,
  val vocabulary: List<VocabItem>,
  val kanji: List<KanjiItem>,
)

data class QuizQuestion(
  val id: String,
  val level: JlptLevel,
  val promptArabic: String,
  val promptJapanese: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanationArabic: String,
)
