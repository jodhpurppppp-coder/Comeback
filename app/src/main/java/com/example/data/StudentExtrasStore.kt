package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

data class StudyChannelItem(
    val id: String,
    val channelName: String,
    val teacherOrTopic: String,
    val examTag: String
)

data class StudentExtraDetails(
    val mobileNumber: String = "",
    val ageYears: Int = 17,
    val targetExamYear: Int = 2027,
    val expectedScore: Int = 680,
    val maxScore: Int = 720,
    val avatarPresetIndex: Int = 0,
    val customAvatarUri: String = "",
    val emailOtpVerified: Boolean = true,
    val studyChannels: List<StudyChannelItem> = defaultChannels()
) {
    fun neetAgeValidationMessage(): Pair<Boolean, String> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearsUntilExam = (targetExamYear - currentYear).coerceAtLeast(0)
        val ageAtExamDec31 = ageYears + yearsUntilExam
        return if (ageAtExamDec31 >= 17) {
            true to "Eligible for NEET UG $targetExamYear: You will be $ageAtExamDec31 yrs old by Dec 31, $targetExamYear (Meets NTA minimum age 17 requirement)."
        } else {
            val eligibleYear = targetExamYear + (17 - ageAtExamDec31)
            false to "Under 17 Alert: At age $ageYears, you will be $ageAtExamDec31 in $targetExamYear. NTA NEET UG requires minimum age 17 by Dec 31 of admission year (Eligible in $eligibleYear)."
        }
    }

    companion object {
        fun defaultChannels(): List<StudyChannelItem> = listOf(
            StudyChannelItem("ch_1", "NCERT Official & Exemplar Hub", "Biology, Physics & Chemistry Core", "NEET UG"),
            StudyChannelItem("ch_2", "Physics Galaxy & Numericals Lab", "Mechanics, Electrodynamics & PYQs", "JEE"),
            StudyChannelItem("ch_3", "Organic & Physical Chemistry Mastery", "Named Reactions & Mole Concept", "NEET UG"),
            StudyChannelItem("ch_4", "Board 95%+ Topper Answer Writing", "Class 9, 10, 11 & 12 NCERT Solutions", "Class 10")
        )
    }
}

object StudentExtrasStore {
    private const val PREF_NAME = "regain_student_extras_prefs"

    fun load(context: Context, userId: String, examTarget: String): StudentExtraDetails {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val defaultMax = when (examTarget) {
            "NEET UG" -> 720
            "JEE" -> 300
            else -> 500
        }
        val defaultExpected = when (examTarget) {
            "NEET UG" -> 680
            "JEE" -> 250
            else -> 475
        }
        val mobile = prefs.getString("${userId}_mobile", "") ?: ""
        val age = prefs.getInt("${userId}_age", 17)
        val year = prefs.getInt("${userId}_exam_year", 2027)
        val expected = prefs.getInt("${userId}_expected_score_${examTarget}", defaultExpected)
        val avatarIdx = prefs.getInt("${userId}_avatar_idx", 0)
        val avatarUri = prefs.getString("${userId}_avatar_uri", "") ?: ""
        val otpVerified = prefs.getBoolean("${userId}_otp_verified", true)

        val channelsJson = prefs.getString("${userId}_channels", null)
        val channels = if (channelsJson.isNullOrBlank()) {
            StudentExtraDetails.defaultChannels()
        } else {
            try {
                val arr = JSONArray(channelsJson)
                val list = mutableListOf<StudyChannelItem>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    list.add(
                        StudyChannelItem(
                            id = obj.optString("id", "ch_$i"),
                            channelName = obj.optString("channelName", "Study Channel"),
                            teacherOrTopic = obj.optString("teacherOrTopic", "Concept Revision"),
                            examTag = obj.optString("examTag", examTarget)
                        )
                    )
                }
                if (list.isEmpty()) StudentExtraDetails.defaultChannels() else list
            } catch (e: Exception) {
                StudentExtraDetails.defaultChannels()
            }
        }

        return StudentExtraDetails(
            mobileNumber = mobile,
            ageYears = age,
            targetExamYear = year,
            expectedScore = expected,
            maxScore = defaultMax,
            avatarPresetIndex = avatarIdx,
            customAvatarUri = avatarUri,
            emailOtpVerified = otpVerified,
            studyChannels = channels
        )
    }

    fun saveProfileExtras(
        context: Context,
        userId: String,
        examTarget: String,
        mobileNumber: String,
        ageYears: Int,
        targetExamYear: Int,
        expectedScore: Int,
        avatarPresetIndex: Int,
        customAvatarUri: String
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString("${userId}_mobile", mobileNumber.trim().take(20))
            .putInt("${userId}_age", ageYears.coerceIn(12, 35))
            .putInt("${userId}_exam_year", targetExamYear.coerceIn(2026, 2032))
            .putInt("${userId}_expected_score_${examTarget}", expectedScore.coerceAtLeast(50))
            .putInt("${userId}_avatar_idx", avatarPresetIndex)
            .putString("${userId}_avatar_uri", customAvatarUri)
            .apply()
    }

    fun addStudyChannel(
        context: Context,
        userId: String,
        examTarget: String,
        channelName: String,
        teacherOrTopic: String
    ): List<StudyChannelItem> {
        val current = load(context, userId, examTarget).studyChannels.toMutableList()
        val newItem = StudyChannelItem(
            id = "ch_${System.currentTimeMillis()}",
            channelName = channelName.trim().ifBlank { "My Study Channel" }.take(60),
            teacherOrTopic = teacherOrTopic.trim().ifBlank { "Daily Lectures & Notes" }.take(80),
            examTag = examTarget
        )
        current.add(0, newItem)
        saveChannels(context, userId, current)
        return current
    }

    fun removeStudyChannel(
        context: Context,
        userId: String,
        examTarget: String,
        channelId: String
    ): List<StudyChannelItem> {
        val current = load(context, userId, examTarget).studyChannels.filterNot { it.id == channelId }
        saveChannels(context, userId, current)
        return current
    }

    private fun saveChannels(context: Context, userId: String, list: List<StudyChannelItem>) {
        val arr = JSONArray()
        list.take(25).forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("channelName", item.channelName)
                    put("teacherOrTopic", item.teacherOrTopic)
                    put("examTag", item.examTag)
                }
            )
        }
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("${userId}_channels", arr.toString()).apply()
    }
}
