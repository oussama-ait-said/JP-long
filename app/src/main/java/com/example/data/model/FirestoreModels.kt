package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class JlptLevel(
  val code: String,
  val titleArabic: String,
  val badgeJapanese: String,
  val descriptionArabic: String,
) {
  KANA(
    code = "KANA",
    titleArabic = "البداية والحروف",
    badgeJapanese = "あ・ア",
    descriptionArabic = "هيراغانا وكاتاكانا من الصفر",
  ),
  N5(
    code = "N5",
    titleArabic = "المستوى N5",
    badgeJapanese = "初級",
    descriptionArabic = "الأساسيات والمحادثات اليومية",
  ),
  N4(
    code = "N4",
    titleArabic = "المستوى N4",
    badgeJapanese = "初中級",
    descriptionArabic = "القواعد المترابطة والتعبيرات العملية",
  ),
  N3(
    code = "N3",
    titleArabic = "المستوى N3",
    badgeJapanese = "中級",
    descriptionArabic = "فهم المقالات والمحادثات الطبيعية",
  );

  companion object {
    fun fromRaw(raw: String?): JlptLevel =
      entries.firstOrNull { it.code == raw } ?: N5
  }
}

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val displayName: String = "",
  val targetLevel: String = JlptLevel.N5.code,
  val dailyGoal: Long = 10L,
  val xp: Long = 0L,
  val streakDays: Long = 0L,
  val lastStudyDay: String = "",
  val completedLessonIds: List<String> = emptyList(),
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class FlashcardProgress(
  @DocumentId val id: String = "",
  val userId: String = "",
  val itemId: String = "",
  val japanese: String = "",
  val reading: String = "",
  val romaji: String = "",
  val arabicMeaning: String = "",
  val level: String = JlptLevel.N5.code,
  val box: Long = 0L,
  val intervalDays: Long = 0L,
  val easeFactor: Double = 2.5,
  val nextReviewAtMs: Long = 0L,
  val reviewCount: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class QuizResult(
  @DocumentId val id: String = "",
  val userId: String = "",
  val level: String = JlptLevel.N5.code,
  val score: Long = 0L,
  val totalQuestions: Long = 1L,
  val xpEarned: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

enum class SrsQuality(
  val labelArabic: String,
  val hintArabic: String,
  val qualityScore: Int,
) {
  AGAIN("نسيتها", "مراجعة فورية", 1),
  HARD("صعبة", "فترة قصيرة", 3),
  GOOD("جيدة", "الوتيرة القياسية", 4),
  EASY("سهلة جداً", "فترة أطول", 5),
}
