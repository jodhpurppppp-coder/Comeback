package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegainRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun currentDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun yesterdayDateKey(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }

    // --- USER PROFILE ---

    fun observeUserProfile(userId: String = requireUserId()): Flow<UserProfile?> = callbackFlow {
        val docRef = db.collection("users").document(userId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, docRef.path)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                trySend(profile)
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun getUserProfile(userId: String = requireUserId()): Result<UserProfile?> = runCatching {
        val docRef = db.collection("users").document(userId)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else {
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun saveOrUpdateExamTrack(
        displayName: String,
        examCategory: String,
        examTarget: String,
        dailyGoalMinutes: Long
    ): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val existing = try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

        val safeName = displayName.trim().ifBlank { "Scholar" }.take(100)
        val safeGoal = dailyGoalMinutes.coerceIn(30L, 960L)

        try {
            if (existing == null || existing.createdAt == null) {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "examCategory" to examCategory,
                    "examTarget" to examTarget,
                    "dailyGoalMinutes" to safeGoal,
                    "xpPoints" to 100L,
                    "regainCoins" to 25L,
                    "currentStreak" to 1L,
                    "longestStreak" to 1L,
                    "totalStudyMinutes" to 0L,
                    "completedSessionsCount" to 0L,
                    "perfectDaysCount" to 0L,
                    "strictBlockEnabled" to true,
                    "dailyNotificationEnabled" to true,
                    "lastActiveDate" to currentDateKey(),
                    "unlockedBadges" to listOf("first_focus"),
                    "blockedPackages" to listOf(
                        "com.instagram.android",
                        "com.google.android.youtube",
                        "com.whatsapp",
                        "com.snapchat.android"
                    ),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                val updatePayload = mapOf(
                    "displayName" to safeName,
                    "examCategory" to examCategory,
                    "examTarget" to examTarget,
                    "dailyGoalMinutes" to safeGoal,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            }
            val updatedSnap = docRef.get().await()
            updatedSnap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: UserProfile(id = uid, userId = uid, displayName = safeName, examCategory = examCategory, examTarget = examTarget)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun updateBlockerAndNotificationSettings(
        strictBlockEnabled: Boolean,
        dailyNotificationEnabled: Boolean,
        blockedPackages: List<String>
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val cleanPackages = blockedPackages.map { it.take(120) }.distinct().take(50)
        try {
            docRef.update(
                mapOf(
                    "strictBlockEnabled" to strictBlockEnabled,
                    "dailyNotificationEnabled" to dailyNotificationEnabled,
                    "blockedPackages" to cleanPackages,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- ROUTINE TASKS (STUDY & DAILY ROUTINES) ---

    fun observeRoutines(userId: String = requireUserId()): Flow<List<RoutineTask>> = callbackFlow {
        val colRef = db.collection("users").document(userId).collection("routines")
        val path = "users/$userId/routines"
        val registration = colRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(
                    RoutineTask::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.sortedWith(compareBy<RoutineTask> { it.startTime }.thenBy { it.priorityOrder })
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getUserRoutines(userId: String = requireUserId()): Result<List<RoutineTask>> = runCatching {
        val colRef = db.collection("users").document(userId).collection("routines")
        val path = "users/$userId/routines"
        try {
            val snap = colRef.whereEqualTo("userId", userId).get().await()
            snap.toObjects(RoutineTask::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                .sortedWith(compareBy<RoutineTask> { it.startTime }.thenBy { it.priorityOrder })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            throw e
        }
    }

    suspend fun getRoutineById(userId: String = requireUserId(), routineId: String): Result<RoutineTask?> = runCatching {
        val docRef = db.collection("users").document(userId).collection("routines").document(routineId)
        try {
            val snap = docRef.get().await()
            snap.toObject(RoutineTask::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun createRoutineTask(
        title: String,
        subject: String,
        period: String,
        blockType: String,
        startTime: String,
        durationMinutes: Long,
        examTag: String,
        presetName: String = "Custom",
        notifyOnStart: Boolean = true,
        priorityOrder: Long = 10L
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val colRef = db.collection("users").document(uid).collection("routines")
        val docRef = colRef.document()
        val validPeriod = if (period in listOf("MORNING", "AFTERNOON", "EVENING", "CUSTOM")) period else "MORNING"
        val validBlockType = if (blockType in listOf("STUDY", "BREAK", "DAILY_ROUTINE")) blockType else "STUDY"
        val payload = mapOf(
            "userId" to uid,
            "title" to title.trim().ifBlank { "Focused Study Block" }.take(120),
            "subject" to subject.trim().ifBlank { "General" }.take(60),
            "period" to validPeriod,
            "blockType" to validBlockType,
            "startTime" to startTime.trim().ifBlank { "07:00" }.take(10),
            "durationMinutes" to durationMinutes.coerceIn(5L, 360L),
            "completedMinutesToday" to 0L,
            "isCompletedToday" to false,
            "examTag" to examTag.trim().ifBlank { "All" }.take(40),
            "presetName" to presetName.trim().ifBlank { "Custom" }.take(80),
            "notifyOnStart" to notifyOnStart,
            "priorityOrder" to priorityOrder.coerceIn(0L, 1000L),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun applyPresetPack(presetPack: RoutinePresetPack): Result<Int> = runCatching {
        requireUserId()
        var count = 0
        for (item in presetPack.items) {
            createRoutineTask(
                title = item.title,
                subject = item.subject,
                period = item.period,
                blockType = item.blockType,
                startTime = item.startTime,
                durationMinutes = item.durationMinutes,
                examTag = presetPack.targetExam,
                presetName = presetPack.name,
                notifyOnStart = true,
                priorityOrder = item.priorityOrder
            ).getOrThrow()
            count++
        }
        count
    }

    suspend fun toggleRoutineCompletion(task: RoutineTask, completed: Boolean): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(task.id.isNotBlank()) { "Routine ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("routines").document(task.id)
        val newCompletedMinutes = if (completed) task.durationMinutes.coerceIn(0L, 360L) else 0L
        try {
            docRef.update(
                mapOf(
                    "completedMinutesToday" to newCompletedMinutes,
                    "isCompletedToday" to completed,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteRoutineTask(routineId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(routineId.isNotBlank()) { "Routine ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("routines").document(routineId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- FOCUS SESSIONS & GAMIFICATION / ANALYTICS ---

    fun observeSessions(userId: String = requireUserId()): Flow<List<FocusSession>> = callbackFlow {
        val colRef = db.collection("users").document(userId).collection("sessions")
        val path = "users/$userId/sessions"
        val registration = colRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(
                    FocusSession::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.sortedByDescending { it.createdAt?.seconds ?: 0L } ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getUserSessions(userId: String = requireUserId()): Result<List<FocusSession>> = runCatching {
        val colRef = db.collection("users").document(userId).collection("sessions")
        val path = "users/$userId/sessions"
        try {
            val snap = colRef.whereEqualTo("userId", userId).get().await()
            snap.toObjects(FocusSession::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                .sortedByDescending { it.createdAt?.seconds ?: 0L }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            throw e
        }
    }

    suspend fun recordCompletedFocusSession(
        routineTask: RoutineTask?,
        title: String,
        subject: String,
        period: String,
        plannedMinutes: Long,
        actualMinutes: Long,
        blockedAttempts: Long,
        examTarget: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val sessionsRef = db.collection("users").document(uid).collection("sessions")
        val docRef = sessionsRef.document()

        val safePlanned = plannedMinutes.coerceIn(1L, 360L)
        val safeActual = actualMinutes.coerceIn(1L, 360L)
        val validPeriod = if (period in listOf("MORNING", "AFTERNOON", "EVENING", "CUSTOM")) period else "MORNING"
        val earnedXp = (safeActual * 4L + if (safeActual >= safePlanned) 50L else 15L).coerceIn(10L, 5000L)
        val earnedCoins = (safeActual / 3L + 5L).coerceAtLeast(5L)
        val focusScore = ((safeActual.toDouble() / safePlanned.toDouble()) * 100.0).toLong().coerceIn(60L, 100L)
        val cal = Calendar.getInstance()
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY).toLong().coerceIn(0L, 23L)
        val today = currentDateKey()

        val sessionPayload = mapOf(
            "userId" to uid,
            "routineId" to (routineTask?.id ?: "quick_focus").take(128),
            "title" to title.trim().ifBlank { "Deep Focus Session" }.take(120),
            "subject" to subject.trim().ifBlank { "General" }.take(60),
            "period" to validPeriod,
            "plannedMinutes" to safePlanned,
            "actualMinutes" to safeActual,
            "xpEarned" to earnedXp,
            "focusScore" to focusScore,
            "blockedAttempts" to blockedAttempts.coerceIn(0L, 1000L),
            "dateKey" to today,
            "hourOfDay" to hourOfDay,
            "examTarget" to examTarget.trim().ifBlank { "NEET UG" }.take(40),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(sessionPayload).await()

            // Update RoutineTask if linked
            if (routineTask != null && routineTask.id.isNotBlank()) {
                val routineRef = db.collection("users").document(uid).collection("routines").document(routineTask.id)
                val updatedMins = (routineTask.completedMinutesToday + safeActual).coerceIn(0L, 360L)
                val isDone = updatedMins >= routineTask.durationMinutes
                routineRef.update(
                    mapOf(
                        "completedMinutesToday" to updatedMins,
                        "isCompletedToday" to isDone,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // Update UserProfile gamification stats
            val userRef = db.collection("users").document(uid)
            val profileSnap = userRef.get().await()
            if (profileSnap.exists()) {
                val profile = profileSnap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                if (profile != null) {
                    val newXp = (profile.xpPoints + earnedXp).coerceAtMost(10000000L)
                    val newCoins = (profile.regainCoins + earnedCoins).coerceAtMost(10000000L)
                    val newTotalMins = (profile.totalStudyMinutes + safeActual).coerceAtMost(10000000L)
                    val newSessionsCount = (profile.completedSessionsCount + 1L).coerceAtMost(1000000L)

                    val yesterday = yesterdayDateKey()
                    val newStreak = when (profile.lastActiveDate) {
                        today -> maxOf(1L, profile.currentStreak)
                        yesterday -> (profile.currentStreak + 1L).coerceAtMost(10000L)
                        else -> maxOf(1L, profile.currentStreak)
                    }
                    val newLongest = maxOf(profile.longestStreak, newStreak).coerceAtMost(10000L)
                    val newPerfectDays = if (newTotalMins >= profile.dailyGoalMinutes && profile.lastActiveDate != today) {
                        (profile.perfectDaysCount + 1L).coerceAtMost(100000L)
                    } else if (safeActual >= 45L && profile.perfectDaysCount == 0L) {
                        1L
                    } else {
                        profile.perfectDaysCount
                    }

                    val contextualBadges = mutableListOf<String>()
                    if (validPeriod == "MORNING" || hourOfDay < 12L) {
                        contextualBadges.add("early_bird")
                    }
                    if (safeActual >= 45L || newTotalMins >= 180L) {
                        contextualBadges.add("deep_work_master")
                    }

                    val candidateProfile = profile.copy(
                        xpPoints = newXp,
                        regainCoins = newCoins,
                        currentStreak = newStreak,
                        longestStreak = newLongest,
                        totalStudyMinutes = newTotalMins,
                        completedSessionsCount = newSessionsCount,
                        perfectDaysCount = newPerfectDays,
                        lastActiveDate = today,
                        unlockedBadges = (profile.unlockedBadges + contextualBadges).distinct()
                    )
                    val newlyUnlocked = GamificationCatalog.badges
                        .filter { it.checkUnlocked(candidateProfile) }
                        .map { it.id }
                    val mergedBadges = (profile.unlockedBadges + contextualBadges + newlyUnlocked).distinct().take(30)

                    userRef.update(
                        mapOf(
                            "xpPoints" to newXp,
                            "regainCoins" to newCoins,
                            "currentStreak" to newStreak,
                            "longestStreak" to newLongest,
                            "totalStudyMinutes" to newTotalMins,
                            "completedSessionsCount" to newSessionsCount,
                            "perfectDaysCount" to newPerfectDays,
                            "lastActiveDate" to today,
                            "unlockedBadges" to mergedBadges,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun claimMilestoneOrStreakGoalCheckIn(badgeId: String? = null): Result<String> = runCatching {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        val today = currentDateKey()
        try {
            val snap = userRef.get().await()
            val profile = snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: throw IllegalStateException("Profile not initialized")

            val targetBadge = GamificationCatalog.badges.find { it.id == badgeId }
            val bonusXp = targetBadge?.rewardXp?.toLong() ?: 120L
            val bonusCoins = targetBadge?.rewardCoins?.toLong() ?: 25L

            val newStreak = (profile.currentStreak + 1L).coerceAtMost(10000L)
            val newLongest = maxOf(profile.longestStreak, newStreak).coerceAtMost(10000L)
            val newPerfectDays = (profile.perfectDaysCount + 1L).coerceAtMost(100000L)
            val newXp = (profile.xpPoints + bonusXp).coerceAtMost(10000000L)
            val newCoins = (profile.regainCoins + bonusCoins).coerceAtMost(10000000L)

            val addedBadges = listOfNotNull(badgeId, "early_bird".takeIf { newStreak >= 2L })
            val candidate = profile.copy(
                xpPoints = newXp,
                regainCoins = newCoins,
                currentStreak = newStreak,
                longestStreak = newLongest,
                perfectDaysCount = newPerfectDays,
                unlockedBadges = (profile.unlockedBadges + addedBadges).distinct()
            )
            val unlockedAll = (
                profile.unlockedBadges +
                    addedBadges +
                    GamificationCatalog.badges.filter { it.checkUnlocked(candidate) }.map { it.id }
                ).distinct().take(30)

            userRef.update(
                mapOf(
                    "xpPoints" to newXp,
                    "regainCoins" to newCoins,
                    "currentStreak" to newStreak,
                    "longestStreak" to newLongest,
                    "perfectDaysCount" to newPerfectDays,
                    "lastActiveDate" to today,
                    "unlockedBadges" to unlockedAll,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            if (targetBadge != null) {
                "Badge '${targetBadge.title}' Verified! +$bonusXp XP & +$bonusCoins Coins • Streak: ${newStreak}d"
            } else {
                "Daily Study Goal Check-In Complete! ${newStreak}-Day Streak Active (+$bonusXp XP)"
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, userRef.path)
            throw e
        }
    }
}
