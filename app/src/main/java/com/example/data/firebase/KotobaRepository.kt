package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.curriculum.CurriculumN4N3Data
import com.example.data.curriculum.KanaData
import com.example.data.model.FlashcardProgress
import com.example.data.model.JlptLevel
import com.example.data.model.QuizResult
import com.example.data.model.SrsQuality
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class KotobaRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = Firebase.auth,
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private fun requireUserId(expectedUserId: String): String {
    val uid = auth.currentUser?.uid
      ?: throw IllegalStateException("يجب تسجيل الدخول عبر Google للمتابعة.")
    check(uid == expectedUserId) { "تبدّل الحساب النشط؛ تم إلغاء العملية." }
    return uid
  }

  private fun todayKey(): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return fmt.format(Calendar.getInstance().time)
  }

  private fun yesterdayKey(): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return fmt.format(cal.time)
  }

  fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
    val uid = requireUserId(userId)
    val path = "users/$uid"
    emitAll(
      db.collection("users").document(uid)
        .snapshots()
        .map { snapshot ->
          if (snapshot.exists()) {
            snapshot.toObject(
              UserProfile::class.java,
              DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
            )
          } else {
            null
          }
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.GET, path)
          }
          throw error
        }
    )
  }

  fun observeFlashcards(userId: String): Flow<List<FlashcardProgress>> = flow {
    val uid = requireUserId(userId)
    val path = "users/$uid/flashcards"
    emitAll(
      db.collection("users").document(uid).collection("flashcards")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(
            FlashcardProgress::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  fun observeQuizResults(userId: String): Flow<List<QuizResult>> = flow {
    val uid = requireUserId(userId)
    val path = "users/$uid/quiz_results"
    emitAll(
      db.collection("users").document(uid).collection("quiz_results")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(30)
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(
            QuizResult::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun ensureUserProfile(userId: String, rawDisplayName: String): Result<Unit> {
    val path = "users/$userId"
    return try {
      val uid = requireUserId(userId)
      val docRef = db.collection("users").document(uid)
      val existing = docRef.get().await()
      if (!existing.exists()) {
        val cleanName = rawDisplayName.trim().ifBlank { "متعلّم كوتوبا" }.take(80)
        val payload = mapOf(
          "userId" to uid,
          "displayName" to cleanName,
          "targetLevel" to JlptLevel.N5.code,
          "dailyGoal" to 10L,
          "xp" to 0L,
          "streakDays" to 1L,
          "lastStudyDay" to todayKey(),
          "completedLessonIds" to emptyList<String>(),
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp(),
        )
        docRef.set(payload).await()
      }
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, path)
      Result.failure(e)
    }
  }

  suspend fun updatePreferences(
    userId: String,
    targetLevel: JlptLevel,
    dailyGoal: Long,
  ): Result<Unit> {
    val path = "users/$userId"
    return try {
      val uid = requireUserId(userId)
      val clampedGoal = dailyGoal.coerceIn(5L, 100L)
      db.collection("users").document(uid).update(
        mapOf(
          "targetLevel" to targetLevel.code,
          "dailyGoal" to clampedGoal,
          "updatedAt" to FieldValue.serverTimestamp(),
        )
      ).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, path)
      Result.failure(e)
    }
  }

  suspend fun completeLesson(
    userId: String,
    lessonId: String,
    xpReward: Long,
  ): Result<Unit> {
    val path = "users/$userId"
    return try {
      val uid = requireUserId(userId)
      require(lessonId.isNotBlank() && lessonId.length <= 64)
      val docRef = db.collection("users").document(uid)
      val snap = docRef.get().await()
      val profile = snap.toObject(UserProfile::class.java)
        ?: return Result.failure(IllegalStateException("ملف المتعلم غير موجود."))

      val alreadyCompleted = profile.completedLessonIds.contains(lessonId)
      val updatedLessons = if (alreadyCompleted) {
        profile.completedLessonIds
      } else {
        (profile.completedLessonIds + lessonId).take(100)
      }
      val earned = if (alreadyCompleted) 10L else xpReward.coerceIn(10L, 500L)
      val (newStreak, today) = computeUpdatedStreak(profile.streakDays, profile.lastStudyDay)

      docRef.update(
        mapOf(
          "completedLessonIds" to updatedLessons,
          "xp" to (profile.xp + earned).coerceAtMost(10_000_000L),
          "streakDays" to newStreak,
          "lastStudyDay" to today,
          "updatedAt" to FieldValue.serverTimestamp(),
        )
      ).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, path)
      Result.failure(e)
    }
  }

  suspend fun saveOrBookmarkFlashcard(
    userId: String,
    itemId: String,
    japanese: String,
    reading: String,
    romaji: String,
    arabicMeaning: String,
    level: JlptLevel,
  ): Result<Unit> {
    val safeId = sanitizeId(itemId)
    val path = "users/$userId/flashcards/$safeId"
    return try {
      val uid = requireUserId(userId)
      require(safeId.isNotBlank())
      val docRef = db.collection("users").document(uid).collection("flashcards").document(safeId)
      val existing = docRef.get().await()
      if (!existing.exists()) {
        val payload = mapOf(
          "userId" to uid,
          "itemId" to safeId,
          "japanese" to japanese.trim().take(80).ifBlank { "-" },
          "reading" to reading.trim().take(120).ifBlank { "-" },
          "romaji" to romaji.trim().take(120),
          "arabicMeaning" to arabicMeaning.trim().take(200).ifBlank { "-" },
          "level" to level.code,
          "box" to 0L,
          "intervalDays" to 0L,
          "easeFactor" to 2.5,
          "nextReviewAtMs" to System.currentTimeMillis(),
          "reviewCount" to 0L,
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp(),
        )
        docRef.set(payload).await()
      }
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, path)
      Result.failure(e)
    }
  }

  suspend fun seedStarterDeck(userId: String, level: JlptLevel): Result<Int> {
    val path = "users/$userId/flashcards"
    return try {
      val uid = requireUserId(userId)
      val colRef = db.collection("users").document(uid).collection("flashcards")
      val existingIds = colRef.get().await().documents.map { it.id }.toSet()

      val itemsToSeed: List<SeedEntry> = when (level) {
        JlptLevel.KANA -> KanaData.hiraganaList.take(10).map {
          SeedEntry(
            id = it.id,
            japanese = it.symbol,
            reading = it.symbol,
            romaji = it.romaji,
            arabicMeaning = "حرف هيراغانا — يُنطق: ${it.arabicPhonetic}",
            level = JlptLevel.KANA,
          )
        }
        else -> CurriculumN4N3Data.allLessons
          .filter { it.level == level }
          .flatMap { it.vocabulary }
          .take(8)
          .map {
            SeedEntry(
              id = it.id,
              japanese = it.japanese,
              reading = it.reading,
              romaji = it.romaji,
              arabicMeaning = it.arabicMeaning,
              level = it.level,
            )
          }
      }

      var added = 0
      val nowMs = System.currentTimeMillis()
      for (entry in itemsToSeed) {
        val safeId = sanitizeId(entry.id)
        if (!existingIds.contains(safeId)) {
          val payload = mapOf(
            "userId" to uid,
            "itemId" to safeId,
            "japanese" to entry.japanese.take(80),
            "reading" to entry.reading.take(120),
            "romaji" to entry.romaji.take(120),
            "arabicMeaning" to entry.arabicMeaning.take(200),
            "level" to entry.level.code,
            "box" to 0L,
            "intervalDays" to 0L,
            "easeFactor" to 2.5,
            "nextReviewAtMs" to nowMs,
            "reviewCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
          colRef.document(safeId).set(payload).await()
          added++
        }
      }
      Result.success(added)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, path)
      Result.failure(e)
    }
  }

  suspend fun reviewFlashcard(
    userId: String,
    card: FlashcardProgress,
    quality: SrsQuality,
  ): Result<Unit> {
    val cardId = card.itemId.ifBlank { card.id }
    val path = "users/$userId/flashcards/$cardId"
    return try {
      val uid = requireUserId(userId)
      require(cardId.isNotBlank())
      val (nextBox, nextInterval, nextEase) = computeSm2(
        currentBox = card.box,
        currentInterval = card.intervalDays,
        currentEase = card.easeFactor,
        quality = quality,
      )
      val nextReviewMs = if (quality == SrsQuality.AGAIN) {
        System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)
      } else {
        System.currentTimeMillis() + TimeUnit.DAYS.toMillis(nextInterval)
      }

      db.collection("users").document(uid)
        .collection("flashcards").document(cardId)
        .update(
          mapOf(
            "box" to nextBox,
            "intervalDays" to nextInterval,
            "easeFactor" to nextEase,
            "nextReviewAtMs" to nextReviewMs,
            "reviewCount" to (card.reviewCount + 1L).coerceAtMost(100_000L),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        ).await()

      // Award +5 XP and refresh daily streak on profile
      val userRef = db.collection("users").document(uid)
      val snap = userRef.get().await()
      val profile = snap.toObject(UserProfile::class.java)
      if (profile != null) {
        val (newStreak, today) = computeUpdatedStreak(profile.streakDays, profile.lastStudyDay)
        userRef.update(
          mapOf(
            "xp" to (profile.xp + 5L).coerceAtMost(10_000_000L),
            "streakDays" to newStreak,
            "lastStudyDay" to today,
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        ).await()
      }
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, path)
      Result.failure(e)
    }
  }

  suspend fun deleteFlashcard(userId: String, cardId: String): Result<Unit> {
    val path = "users/$userId/flashcards/$cardId"
    return try {
      val uid = requireUserId(userId)
      require(cardId.isNotBlank())
      db.collection("users").document(uid)
        .collection("flashcards").document(cardId)
        .delete()
        .await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, path)
      Result.failure(e)
    }
  }

  suspend fun recordQuizResult(
    userId: String,
    levelCode: String,
    score: Long,
    totalQuestions: Long,
  ): Result<Long> {
    val path = "users/$userId/quiz_results"
    return try {
      val uid = requireUserId(userId)
      val safeTotal = totalQuestions.coerceIn(1L, 100L)
      val safeScore = score.coerceIn(0L, safeTotal)
      val xpEarned = (safeScore * 15L + 10L).coerceIn(0L, 1000L)

      val docRef = db.collection("users").document(uid).collection("quiz_results").document()
      val payload = mapOf(
        "userId" to uid,
        "level" to levelCode,
        "score" to safeScore,
        "totalQuestions" to safeTotal,
        "xpEarned" to xpEarned,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
      )
      docRef.set(payload).await()

      val userRef = db.collection("users").document(uid)
      val snap = userRef.get().await()
      val profile = snap.toObject(UserProfile::class.java)
      if (profile != null) {
        val (newStreak, today) = computeUpdatedStreak(profile.streakDays, profile.lastStudyDay)
        userRef.update(
          mapOf(
            "xp" to (profile.xp + xpEarned).coerceAtMost(10_000_000L),
            "streakDays" to newStreak,
            "lastStudyDay" to today,
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        ).await()
      }
      Result.success(xpEarned)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, path)
      Result.failure(e)
    }
  }

  private fun computeUpdatedStreak(currentStreak: Long, lastStudyDay: String): Pair<Long, String> {
    val today = todayKey()
    val yesterday = yesterdayKey()
    val updatedStreak = when (lastStudyDay) {
      today -> max(1L, currentStreak)
      yesterday -> (currentStreak + 1L).coerceAtMost(10_000L)
      else -> 1L
    }
    return updatedStreak to today
  }

  private fun computeSm2(
    currentBox: Long,
    currentInterval: Long,
    currentEase: Double,
    quality: SrsQuality,
  ): Triple<Long, Long, Double> {
    val q = quality.qualityScore
    val newEase = (currentEase + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)))
      .coerceIn(1.3, 3.5)
    if (q < 3) {
      return Triple(0L, 0L, newEase)
    }
    val nextBox = (currentBox + 1L).coerceAtMost(10L)
    val nextInterval = when {
      currentBox == 0L -> 1L
      currentBox == 1L -> if (quality == SrsQuality.EASY) 4L else 3L
      else -> (max(1L, currentInterval) * newEase * if (quality == SrsQuality.EASY) 1.25 else 1.0)
        .roundToLong()
        .coerceIn(1L, 3650L)
    }
    return Triple(nextBox, nextInterval, newEase)
  }

  private fun sanitizeId(raw: String): String =
    raw.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(64)

  private data class SeedEntry(
    val id: String,
    val japanese: String,
    val reading: String,
    val romaji: String,
    val arabicMeaning: String,
    val level: JlptLevel,
  )
}
