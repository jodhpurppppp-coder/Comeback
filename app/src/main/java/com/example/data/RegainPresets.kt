package com.example.data

data class RoutinePresetItem(
    val title: String,
    val subject: String,
    val period: String, // MORNING, AFTERNOON, EVENING, CUSTOM
    val blockType: String, // STUDY, BREAK, DAILY_ROUTINE
    val startTime: String,
    val durationMinutes: Long,
    val priorityOrder: Long
)

data class RoutinePresetPack(
    val id: String,
    val name: String,
    val subtitle: String,
    val targetExam: String,
    val totalStudyMinutes: Int,
    val items: List<RoutinePresetItem>
)

object RegainPresets {
    fun getPresetsForExam(examTarget: String): List<RoutinePresetPack> {
        return when (examTarget) {
            "NEET UG" -> listOf(
                RoutinePresetPack(
                    id = "neet_topper_full",
                    name = "NEET UG 700+ Full Day Routine",
                    subtitle = "Morning NCERT Biology + Afternoon Physics Numericals + Evening Chemistry & PYQs",
                    targetExam = "NEET UG",
                    totalStudyMinutes = 405,
                    items = listOf(
                        RoutinePresetItem("Wake Up, Hydration & Mindful Breathing", "Daily Wellness", "MORNING", "DAILY_ROUTINE", "05:45", 20L, 1L),
                        RoutinePresetItem("NCERT Biology Line-by-Line & Diagrams", "Biology (Botany & Zoology)", "MORNING", "STUDY", "06:15", 90L, 2L),
                        RoutinePresetItem("Protein Breakfast & Sunlight Walk", "Break / Recharge", "MORNING", "BREAK", "07:45", 30L, 3L),
                        RoutinePresetItem("Organic & Inorganic Chemistry Reactions", "Chemistry", "MORNING", "STUDY", "08:15", 90L, 4L),
                        RoutinePresetItem("Physics Mechanics & Electrodynamics Drill", "Physics", "AFTERNOON", "STUDY", "14:00", 90L, 5L),
                        RoutinePresetItem("Power Nap & Eye Rest (No Screens)", "Break / Recharge", "AFTERNOON", "BREAK", "15:30", 25L, 6L),
                        RoutinePresetItem("NEET 45-Question Timed Speed Sprint", "NEET Mock & PYQs", "AFTERNOON", "STUDY", "16:00", 60L, 7L),
                        RoutinePresetItem("Mistake Notebook & Formula Flashcards", "NCERT Revision", "EVENING", "STUDY", "19:30", 75L, 8L),
                        RoutinePresetItem("Next Day Planner & Digital Detox Lock", "Daily Wellness", "EVENING", "DAILY_ROUTINE", "21:30", 15L, 9L)
                    )
                ),
                RoutinePresetPack(
                    id = "neet_morning_blitz",
                    name = "Morning Bio & Chem Blitz",
                    subtitle = "High-retention 3-hour early morning study routine before classes",
                    targetExam = "NEET UG",
                    totalStudyMinutes = 180,
                    items = listOf(
                        RoutinePresetItem("Morning Cold Splash & Goal Review", "Daily Wellness", "MORNING", "DAILY_ROUTINE", "05:30", 15L, 1L),
                        RoutinePresetItem("NCERT Botany & Zoology Active Recall", "Biology (Botany & Zoology)", "MORNING", "STUDY", "05:45", 90L, 2L),
                        RoutinePresetItem("Physical Chemistry Formula & Mol Concept", "Chemistry", "MORNING", "STUDY", "07:30", 90L, 3L)
                    )
                )
            )
            "JEE" -> listOf(
                RoutinePresetPack(
                    id = "jee_ranker_full",
                    name = "JEE Main & Advanced Ranker Routine",
                    subtitle = "Deep Calculus, Physics Problem Solving & Physical/Organic Chemistry",
                    targetExam = "JEE",
                    totalStudyMinutes = 420,
                    items = listOf(
                        RoutinePresetItem("Morning Mobility & Zero-Phone Start", "Daily Wellness", "MORNING", "DAILY_ROUTINE", "06:00", 20L, 1L),
                        RoutinePresetItem("Calculus & Coordinate Geometry Deep Block", "Mathematics", "MORNING", "STUDY", "06:30", 105L, 2L),
                        RoutinePresetItem("Breakfast & Stretch Break", "Break / Recharge", "MORNING", "BREAK", "08:15", 30L, 3L),
                        RoutinePresetItem("Physics: Rotational Motion & Electrostatics", "Physics", "MORNING", "STUDY", "08:45", 95L, 4L),
                        RoutinePresetItem("Organic Reaction Mechanisms & GOC", "Chemistry", "AFTERNOON", "STUDY", "14:00", 90L, 5L),
                        RoutinePresetItem("Afternoon Tea & Mindful Reset", "Break / Recharge", "AFTERNOON", "BREAK", "15:30", 20L, 6L),
                        RoutinePresetItem("JEE Advanced Multi-Concept Problems", "Advanced Problem Solving", "EVENING", "STUDY", "18:00", 75L, 7L),
                        RoutinePresetItem("NTA Previous Year Paper Timed Analysis", "JEE Mock & PYQs", "EVENING", "STUDY", "20:00", 55L, 8L),
                        RoutinePresetItem("Formula Sheet Review & Sleep Prep", "Daily Wellness", "EVENING", "DAILY_ROUTINE", "21:30", 15L, 9L)
                    )
                ),
                RoutinePresetPack(
                    id = "jee_evening_pyq",
                    name = "Evening Problem Solving & PYQ Sprint",
                    subtitle = "Focused afternoon/evening routine for coaching or school students",
                    targetExam = "JEE",
                    totalStudyMinutes = 210,
                    items = listOf(
                        RoutinePresetItem("Post-Class Brain Decompression", "Break / Recharge", "AFTERNOON", "BREAK", "16:30", 20L, 1L),
                        RoutinePresetItem("Physics Numericals & Free Body Diagrams", "Physics", "EVENING", "STUDY", "17:00", 75L, 2L),
                        RoutinePresetItem("Algebra & Vectors Daily Practice Sheet (DPP)", "Mathematics", "EVENING", "STUDY", "18:30", 75L, 3L),
                        RoutinePresetItem("Inorganic NCERT & Exceptions Recap", "Chemistry", "EVENING", "STUDY", "20:30", 60L, 4L)
                    )
                )
            )
            else -> {
                val cleanTarget = if (examTarget in listOf("Class 9", "Class 10", "Class 11", "Class 12")) examTarget else "Class 10"
                val isSenior = cleanTarget in listOf("Class 11", "Class 12")
                val primarySub1 = if (isSenior) "Physics" else "Mathematics"
                val primarySub2 = if (isSenior) "Chemistry" else "Science"
                val primarySub3 = if (isSenior) "Mathematics" else "Social Science"
                listOf(
                    RoutinePresetPack(
                        id = "board_95_full",
                        name = "$cleanTarget Board 95%+ Balanced Routine",
                        subtitle = "Morning Concept Revision + Afternoon School Homework/NCERT + Evening Answer Writing",
                        targetExam = cleanTarget,
                        totalStudyMinutes = 255,
                        items = listOf(
                            RoutinePresetItem("Morning Wake Up, Hydration & Desk Setup", "Daily Wellness", "MORNING", "DAILY_ROUTINE", "06:00", 15L, 1L),
                            RoutinePresetItem("$primarySub1: NCERT Chapter & Examples", primarySub1, "MORNING", "STUDY", "06:15", 60L, 2L),
                            RoutinePresetItem("Post-School Refresh & Healthy Snack", "Break / Recharge", "AFTERNOON", "BREAK", "15:30", 25L, 3L),
                            RoutinePresetItem("$primarySub2: Core Concepts & Diagrams", primarySub2, "AFTERNOON", "STUDY", "16:00", 60L, 4L),
                            RoutinePresetItem("Outdoor Walk / Stretching Break", "Break / Recharge", "AFTERNOON", "BREAK", "17:00", 20L, 5L),
                            RoutinePresetItem("$primarySub3: In-Text Questions & PYQs", primarySub3, "EVENING", "STUDY", "18:00", 75L, 6L),
                            RoutinePresetItem("English / Literature & Summary Notes", "English", "EVENING", "STUDY", "20:00", 60L, 7L),
                            RoutinePresetItem("Pack Bag & Tomorrow's Top 3 Priorities", "Daily Wellness", "EVENING", "DAILY_ROUTINE", "21:15", 15L, 8L)
                        )
                    ),
                    RoutinePresetPack(
                        id = "board_custom_pomodoro",
                        name = "$cleanTarget High-Focus Pomodoro Pack",
                        subtitle = "50-minute intense study blocks with 10-minute screen-free recharge breaks",
                        targetExam = cleanTarget,
                        totalStudyMinutes = 150,
                        items = listOf(
                            RoutinePresetItem("Pomodoro 1: $primarySub1 Problem Set", primarySub1, "MORNING", "STUDY", "07:00", 50L, 1L),
                            RoutinePresetItem("10-Min Mindful Hydration Break", "Break / Recharge", "MORNING", "BREAK", "07:50", 10L, 2L),
                            RoutinePresetItem("Pomodoro 2: $primarySub2 NCERT Notes", primarySub2, "AFTERNOON", "STUDY", "16:30", 50L, 3L),
                            RoutinePresetItem("Pomodoro 3: $primarySub3 Board Questions", primarySub3, "EVENING", "STUDY", "19:00", 50L, 4L)
                        )
                    )
                )
            }
        }
    }

    val defaultDistractingApps: List< Pair<String, String> > = listOf(
        "com.instagram.android" to "Instagram (Reels & Feed)",
        "com.google.android.youtube" to "YouTube (Shorts & Videos)",
        "com.whatsapp" to "WhatsApp Messenger",
        "com.snapchat.android" to "Snapchat",
        "com.zhiliaoapp.musically" to "TikTok / Short Video",
        "com.twitter.android" to "X (Twitter)",
        "com.facebook.katana" to "Facebook",
        "com.reddit.frontpage" to "Reddit",
        "com.netflix.mediaclient" to "Netflix",
        "com.pubg.imobile" to "BGMI / Mobile Gaming"
    )
}
