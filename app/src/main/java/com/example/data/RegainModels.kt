package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Scholar",
    val examCategory: String = "UNSET", // UNSET, BOARDS, COMPETITIVE
    val examTarget: String = "Not Selected", // Not Selected, Class 9, Class 10, Class 11, Class 12, NEET UG, JEE
    val dailyGoalMinutes: Long = 300L,
    val xpPoints: Long = 0L,
    val regainCoins: Long = 0L,
    val currentStreak: Long = 0L,
    val longestStreak: Long = 0L,
    val totalStudyMinutes: Long = 0L,
    val completedSessionsCount: Long = 0L,
    val perfectDaysCount: Long = 0L,
    val strictBlockEnabled: Boolean = true,
    val dailyNotificationEnabled: Boolean = true,
    val lastActiveDate: String = "",
    val unlockedBadges: List<String> = emptyList(),
    val blockedPackages: List<String> = listOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.whatsapp",
        "com.snapchat.android",
        "com.zhiliaoapp.musically",
        "com.twitter.android"
    ),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class RoutineTask(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "General",
    val period: String = "MORNING", // MORNING, AFTERNOON, EVENING, CUSTOM
    val blockType: String = "STUDY", // STUDY, BREAK, DAILY_ROUTINE
    val startTime: String = "06:30",
    val durationMinutes: Long = 45L,
    val completedMinutesToday: Long = 0L,
    val isCompletedToday: Boolean = false,
    val examTag: String = "All",
    val presetName: String = "Custom",
    val notifyOnStart: Boolean = true,
    val priorityOrder: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FocusSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val routineId: String = "quick_focus",
    val title: String = "Deep Focus Session",
    val subject: String = "Physics",
    val period: String = "MORNING", // MORNING, AFTERNOON, EVENING, CUSTOM
    val plannedMinutes: Long = 45L,
    val actualMinutes: Long = 45L,
    val xpEarned: Long = 100L,
    val focusScore: Long = 95L,
    val blockedAttempts: Long = 2L,
    val dateKey: String = "2026-10-18",
    val hourOfDay: Long = 8L,
    val examTarget: String = "NEET UG",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class ExamCategoryOption(
    val key: String,
    val title: String,
    val subtitle: String,
    val badgeText: String
) {
    BOARDS(
        key = "BOARDS",
        title = "Boards Exam",
        subtitle = "Structured NCERT & School Board Mastery for Classes 9, 10, 11 & 12",
        badgeText = "Class 9 • 10 • 11 • 12"
    ),
    COMPETITIVE(
        key = "COMPETITIVE",
        title = "Competitive Exam",
        subtitle = "High-Intensity Rank-Booster Routines & Deep Focus for Entrance Exams",
        badgeText = "NEET UG • JEE"
    )
}

data class ExamSubTrackInfo(
    val targetName: String,
    val categoryKey: String,
    val headline: String,
    val description: String,
    val recommendedDailyMinutes: Int,
    val subjects: List<String>,
    val highlightTag: String
)

object ExamCatalog {
    val allTracks: List<ExamSubTrackInfo> = listOf(
        ExamSubTrackInfo(
            targetName = "Class 9",
            categoryKey = "BOARDS",
            headline = "Class 9 Foundation & Olympiads",
            description = "Build rock-solid NCERT concepts across Science, Maths & Social Studies with balanced daily study habits.",
            recommendedDailyMinutes = 180,
            subjects = listOf("Mathematics", "Science", "Social Science", "English", "Hindi / Sanskrit"),
            highlightTag = "Foundation Builder"
        ),
        ExamSubTrackInfo(
            targetName = "Class 10",
            categoryKey = "BOARDS",
            headline = "Class 10 Board 95%+ Target",
            description = "Master Board pattern questions, NCERT exemplars, sample papers, and daily formula revision.",
            recommendedDailyMinutes = 240,
            subjects = listOf("Mathematics", "Science", "Social Science", "English", "Sample Papers"),
            highlightTag = "Board Milestone"
        ),
        ExamSubTrackInfo(
            targetName = "Class 11",
            categoryKey = "BOARDS",
            headline = "Class 11 Core Stream Mastery",
            description = "Bridge the Class 11 depth jump with disciplined derivations, numerical practice & daily revision.",
            recommendedDailyMinutes = 270,
            subjects = listOf("Physics", "Chemistry", "Mathematics", "Biology", "English & CS"),
            highlightTag = "Core Stream"
        ),
        ExamSubTrackInfo(
            targetName = "Class 12",
            categoryKey = "BOARDS",
            headline = "Class 12 Board Topper Track",
            description = "High-scoring answer writing, previous year board papers, NCERT line-by-line & practical prep.",
            recommendedDailyMinutes = 300,
            subjects = listOf("Physics", "Chemistry", "Mathematics", "Biology", "Board PYQs"),
            highlightTag = "12th Boards Peak"
        ),
        ExamSubTrackInfo(
            targetName = "NEET UG",
            categoryKey = "COMPETITIVE",
            headline = "NEET UG Medical Aspirant (700+ Goal)",
            description = "Intensive NCERT Biology line-by-line mastery, Organic/Physical Chemistry drills & Physics speed numericals.",
            recommendedDailyMinutes = 420,
            subjects = listOf("Biology (Botany & Zoology)", "Physics", "Chemistry", "NCERT Revision", "NEET Mock & PYQs"),
            highlightTag = "Medical Ranker"
        ),
        ExamSubTrackInfo(
            targetName = "JEE",
            categoryKey = "COMPETITIVE",
            headline = "JEE Main & Advanced Aspirant",
            description = "Deep problem-solving blocks for Calculus, Mechanics, Electrodynamics, Organic Chemistry & NTA mocks.",
            recommendedDailyMinutes = 420,
            subjects = listOf("Mathematics", "Physics", "Chemistry", "Advanced Problem Solving", "JEE Mock & PYQs"),
            highlightTag = "IIT / NIT Target"
        )
    )

    fun getTrack(targetName: String): ExamSubTrackInfo {
        return allTracks.find { it.targetName == targetName } ?: allTracks[4] // default NEET UG
    }

    fun getTracksForCategory(categoryKey: String): List<ExamSubTrackInfo> {
        return allTracks.filter { it.categoryKey == categoryKey }
    }
}

data class BadgeMilestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val requirementLabel: String,
    val rewardXp: Int = 150,
    val rewardCoins: Int = 30,
    val recommendedPeriod: String = "MORNING",
    val recommendedMinutes: Int = 45,
    val checkUnlocked: (UserProfile) -> Boolean,
    val progressFraction: (UserProfile) -> Float,
    val progressText: (UserProfile) -> String
)

object GamificationCatalog {
    val badges: List<BadgeMilestone> = listOf(
        BadgeMilestone(
            id = "early_bird",
            title = "Early Bird",
            subtitle = "Complete a Morning study session before noon or earn 200+ Focus XP in morning slots",
            requirementLabel = "Morning Study Goal",
            rewardXp = 150,
            rewardCoins = 35,
            recommendedPeriod = "MORNING",
            recommendedMinutes = 45,
            checkUnlocked = { it.unlockedBadges.contains("early_bird") || it.xpPoints >= 200 || it.completedSessionsCount >= 1 },
            progressFraction = {
                if (it.unlockedBadges.contains("early_bird")) 1f
                else (it.xpPoints.toFloat() / 200f).coerceIn(0f, 1f)
            },
            progressText = {
                if (it.unlockedBadges.contains("early_bird") || it.xpPoints >= 200) "Unlocked • Morning Scholar"
                else "${it.xpPoints.coerceAtMost(200)} / 200 Morning XP"
            }
        ),
        BadgeMilestone(
            id = "deep_work_master",
            title = "Deep Work Master",
            subtitle = "Complete 180+ minutes of uninterrupted deep focus with the ComeBack App Shield active",
            requirementLabel = "180m Deep Focus",
            rewardXp = 300,
            rewardCoins = 60,
            recommendedPeriod = "MORNING",
            recommendedMinutes = 60,
            checkUnlocked = { it.unlockedBadges.contains("deep_work_master") || it.totalStudyMinutes >= 180 || it.completedSessionsCount >= 3 },
            progressFraction = {
                if (it.unlockedBadges.contains("deep_work_master")) 1f
                else maxOf(
                    it.totalStudyMinutes.toFloat() / 180f,
                    it.completedSessionsCount.toFloat() / 3f
                ).coerceIn(0f, 1f)
            },
            progressText = {
                if (it.unlockedBadges.contains("deep_work_master") || it.totalStudyMinutes >= 180 || it.completedSessionsCount >= 3) {
                    "Unlocked • Deep Work Master"
                } else {
                    "${it.totalStudyMinutes}m / 180m Deep Focus (${it.completedSessionsCount}/3 sessions)"
                }
            }
        ),
        BadgeMilestone(
            id = "first_focus",
            title = "First Focus Spark",
            subtitle = "Complete your very first distraction-free study session",
            requirementLabel = "1 Session Completed",
            rewardXp = 100,
            rewardCoins = 25,
            recommendedPeriod = "MORNING",
            recommendedMinutes = 25,
            checkUnlocked = { it.unlockedBadges.contains("first_focus") || it.completedSessionsCount >= 1 },
            progressFraction = {
                if (it.unlockedBadges.contains("first_focus") || it.completedSessionsCount >= 1) 1f else 0.3f
            },
            progressText = { "${it.completedSessionsCount.coerceAtMost(1)} / 1 Session" }
        ),
        BadgeMilestone(
            id = "streak_3",
            title = "3-Day Study Streak",
            subtitle = "Maintain 3 consecutive days of meeting your daily study goals",
            requirementLabel = "3-Day Goal Streak",
            rewardXp = 200,
            rewardCoins = 40,
            recommendedPeriod = "AFTERNOON",
            recommendedMinutes = 45,
            checkUnlocked = { it.unlockedBadges.contains("streak_3") || it.longestStreak >= 3 || it.currentStreak >= 3 },
            progressFraction = { (maxOf(it.longestStreak, it.currentStreak).toFloat() / 3f).coerceIn(0f, 1f) },
            progressText = { "${maxOf(it.longestStreak, it.currentStreak).coerceAtMost(3)} / 3 Consecutive Days" }
        ),
        BadgeMilestone(
            id = "perfect_week",
            title = "Perfect Week",
            subtitle = "Maintain 7 consecutive days of study goal adherence or achieve 7 perfect goal days",
            requirementLabel = "7-Day Goal Streak",
            rewardXp = 500,
            rewardCoins = 100,
            recommendedPeriod = "EVENING",
            recommendedMinutes = 60,
            checkUnlocked = { it.unlockedBadges.contains("perfect_week") || it.longestStreak >= 7 || it.currentStreak >= 7 || it.perfectDaysCount >= 7 },
            progressFraction = {
                val best = maxOf(it.longestStreak, it.currentStreak, it.perfectDaysCount)
                (best.toFloat() / 7f).coerceIn(0f, 1f)
            },
            progressText = {
                val best = maxOf(it.longestStreak, it.currentStreak, it.perfectDaysCount)
                "${best.coerceAtMost(7)} / 7 Perfect Days"
            }
        ),
        BadgeMilestone(
            id = "hours_10",
            title = "10 Hours Focus Club",
            subtitle = "Accumulate 600+ minutes (10 hours) of verified study focus",
            requirementLabel = "10 Hours Studied",
            rewardXp = 400,
            rewardCoins = 80,
            recommendedPeriod = "AFTERNOON",
            recommendedMinutes = 60,
            checkUnlocked = { it.unlockedBadges.contains("hours_10") || it.totalStudyMinutes >= 600 },
            progressFraction = { (it.totalStudyMinutes.toFloat() / 600f).coerceIn(0f, 1f) },
            progressText = { "${it.totalStudyMinutes / 60}h ${it.totalStudyMinutes % 60}m / 10h" }
        ),
        BadgeMilestone(
            id = "hours_50",
            title = "50 Hours Ranker",
            subtitle = "Reach 3,000 minutes (50 hours) of focused exam preparation",
            requirementLabel = "50 Hours Studied",
            rewardXp = 750,
            rewardCoins = 150,
            recommendedPeriod = "MORNING",
            recommendedMinutes = 90,
            checkUnlocked = { it.unlockedBadges.contains("hours_50") || it.totalStudyMinutes >= 3000 },
            progressFraction = { (it.totalStudyMinutes.toFloat() / 3000f).coerceIn(0f, 1f) },
            progressText = { "${it.totalStudyMinutes / 60}h / 50h Studied" }
        ),
        BadgeMilestone(
            id = "hours_100",
            title = "100 Hours Studied",
            subtitle = "Elite milestone: 6,000 minutes (100 hours) of deep exam mastery",
            requirementLabel = "100 Hours Studied",
            rewardXp = 1500,
            rewardCoins = 300,
            recommendedPeriod = "MORNING",
            recommendedMinutes = 90,
            checkUnlocked = { it.unlockedBadges.contains("hours_100") || it.totalStudyMinutes >= 6000 },
            progressFraction = { (it.totalStudyMinutes.toFloat() / 6000f).coerceIn(0f, 1f) },
            progressText = { "${it.totalStudyMinutes / 60}h / 100h Studied" }
        ),
        BadgeMilestone(
            id = "shield_commander",
            title = "ComeBack Shield Master",
            subtitle = "Complete 15+ focus sessions with Strict App Blocker active",
            requirementLabel = "15 Shielded Sessions",
            rewardXp = 450,
            rewardCoins = 90,
            recommendedPeriod = "EVENING",
            recommendedMinutes = 45,
            checkUnlocked = { it.unlockedBadges.contains("shield_commander") || (it.completedSessionsCount >= 15 && it.strictBlockEnabled) },
            progressFraction = { (it.completedSessionsCount.toFloat() / 15f).coerceIn(0f, 1f) },
            progressText = { "${it.completedSessionsCount.coerceAtMost(15)} / 15 Shielded Sessions" }
        )
    )
}
