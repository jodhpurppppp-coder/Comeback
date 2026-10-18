package com.example.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FocusSession
import com.example.data.RegainPresets
import com.example.data.RegainRepository
import com.example.data.RoutinePresetPack
import com.example.data.RoutineTask
import com.example.data.UserProfile
import com.example.util.DeviceFocusHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class ActiveTimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val linkedRoutine: RoutineTask? = null,
    val sessionTitle: String = "Deep Concept Focus",
    val subject: String = "Physics",
    val period: String = "MORNING",
    val plannedMinutes: Int = 45,
    val totalSeconds: Int = 45 * 60,
    val remainingSeconds: Int = 45 * 60,
    val elapsedSeconds: Int = 0,
    val strictShieldActive: Boolean = true,
    val autoStartRoutineEnabled: Boolean = true,
    val shortFormShieldEnabled: Boolean = true,
    val blockedDistractionCount: Int = 0,
    val ambientSoundLabel: String = "Rainforest Sanctuary",
    val showAppBlockInterceptorPreview: Boolean = false,
    val lastInterceptedAppName: String = "Instagram (Reels & Feed)"
)

class RegainViewModel(
    private val repository: RegainRepository,
    private val currentUserId: String
) : ViewModel() {

    val profileState: StateFlow<UiState<UserProfile?>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing profile", error)
            emit(UiState.Error(error.localizedMessage ?: "Failed to load profile"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val routinesState: StateFlow<UiState<List<RoutineTask>>> = repository.observeRoutines(currentUserId)
        .map<List<RoutineTask>, UiState<List<RoutineTask>>> { UiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing routines", error)
            emit(UiState.Error(error.localizedMessage ?: "Failed to load routines"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val sessionsState: StateFlow<UiState<List<FocusSession>>> = repository.observeSessions(currentUserId)
        .map<List<FocusSession>, UiState<List<FocusSession>>> { UiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing sessions", error)
            emit(UiState.Error(error.localizedMessage ?: "Failed to load sessions"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private val _timerState = MutableStateFlow(ActiveTimerState())
    val timerState: StateFlow<ActiveTimerState> = _timerState.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _showExamSwitcherModal = MutableStateFlow(false)
    val showExamSwitcherModal: StateFlow<Boolean> = _showExamSwitcherModal.asStateFlow()

    private var timerJob: Job? = null
    private val autoTriggeredRoutineKeys = mutableSetOf<String>()

    fun setShowExamSwitcherModal(show: Boolean) {
        _showExamSwitcherModal.value = show
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun postBanner(message: String) {
        _statusBanner.value = message
    }

    fun setAutoStartRoutineEnabled(context: Context, enabled: Boolean) {
        DeviceFocusHelper.setAutoStartRoutineTimerEnabled(context, enabled)
        _timerState.value = _timerState.value.copy(autoStartRoutineEnabled = enabled)
        _statusBanner.value = if (enabled) {
            "Auto-Start Routine Timer & App Blocker enabled! Scheduled blocks will start automatically."
        } else {
            "Auto-Start Routine Timer paused. You can start routine blocks manually."
        }
    }

    fun setShortFormShieldEnabled(context: Context, enabled: Boolean) {
        DeviceFocusHelper.setShortFormShieldEnabled(context, enabled)
        _timerState.value = _timerState.value.copy(shortFormShieldEnabled = enabled)
        _statusBanner.value = if (enabled) {
            "Short-Form Video Shield ON: Instagram Reels & YouTube Shorts will be intercepted!"
        } else {
            "Short-Form Video Shield updated."
        }
    }

    /**
     * Checks whether any uncompleted routine block matches the current HH:mm time
     * and automatically starts the focus timer + app blocker shield + notification.
     */
    fun checkAndAutoStartScheduledRoutine(context: Context) {
        val tState = _timerState.value
        if (!tState.autoStartRoutineEnabled || tState.isRunning) return
        val routines = (routinesState.value as? UiState.Success)?.data ?: return
        if (routines.isEmpty()) return

        val nowHHmm = SimpleDateFormat("HH:mm", Locale.US).format(Date())
        val matchingTask = routines.firstOrNull { task ->
            !task.isCompletedToday &&
                task.notifyOnStart &&
                (task.startTime.trim() == nowHHmm || task.startTime.equals("Now", ignoreCase = true)) &&
                !autoTriggeredRoutineKeys.contains("${task.id}_$nowHHmm")
        } ?: return

        autoTriggeredRoutineKeys.add("${matchingTask.id}_$nowHHmm")
        configureTimerForRoutine(matchingTask)
        startOrResumeTimer(context)

        val blockedCount = ((profileState.value as? UiState.Success)?.data?.blockedPackages?.size ?: 6).coerceAtLeast(1)
        DeviceFocusHelper.sendAutoStartedRoutineNotification(context, matchingTask, blockedCount)
        _statusBanner.value = "⏰ Auto-Started '${matchingTask.title}' (${matchingTask.durationMinutes}m)! Timer running, notification sent & $blockedCount apps blocked."
    }

    fun selectExamAndInitialize(
        context: Context,
        displayName: String,
        examCategory: String,
        examTarget: String,
        dailyGoalMinutes: Long,
        applyStarterPreset: Boolean
    ) {
        viewModelScope.launch {
            val result = repository.saveOrUpdateExamTrack(
                displayName = displayName,
                examCategory = examCategory,
                examTarget = examTarget,
                dailyGoalMinutes = dailyGoalMinutes
            )
            result.onSuccess {
                _showExamSwitcherModal.value = false
                if (applyStarterPreset) {
                    val existingRoutines = (routinesState.value as? UiState.Success)?.data ?: emptyList()
                    if (existingRoutines.none { it.examTag == examTarget }) {
                        val starterPack = RegainPresets.getPresetsForExam(examTarget).firstOrNull()
                        if (starterPack != null) {
                            repository.applyPresetPack(starterPack)
                            DeviceFocusHelper.sendDailyScheduleDigestNotification(
                                context = context,
                                examTarget = examTarget,
                                totalTasks = starterPack.items.size,
                                totalMinutes = starterPack.totalStudyMinutes
                            )
                        }
                    }
                }
                _statusBanner.value = "Exam Track set to $examTarget! Morning, Afternoon & Evening routines ready."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Could not save exam track"
            }
        }
    }

    fun addCustomRoutineTask(
        context: Context,
        title: String,
        subject: String,
        period: String,
        blockType: String,
        startTime: String,
        durationMinutes: Long,
        examTag: String,
        notifyOnStart: Boolean,
        autoStartNow: Boolean = false
    ) {
        viewModelScope.launch {
            val nowHHmm = SimpleDateFormat("HH:mm", Locale.US).format(Date())
            val effectiveStartTime = if (startTime.equals("Now", ignoreCase = true) || startTime.isBlank()) {
                nowHHmm
            } else {
                startTime.trim()
            }

            val result = repository.createRoutineTask(
                title = title,
                subject = subject,
                period = period,
                blockType = blockType,
                startTime = effectiveStartTime,
                durationMinutes = durationMinutes,
                examTag = examTag,
                presetName = "Custom Routine",
                notifyOnStart = notifyOnStart
            )
            result.onSuccess { id ->
                val createdTask = RoutineTask(
                    id = id,
                    title = title,
                    subject = subject,
                    period = period,
                    blockType = blockType,
                    startTime = effectiveStartTime,
                    durationMinutes = durationMinutes,
                    examTag = examTag,
                    presetName = "Custom Routine",
                    notifyOnStart = notifyOnStart
                )
                val shouldAutoStartImmediately = autoStartNow ||
                    startTime.equals("Now", ignoreCase = true) ||
                    (effectiveStartTime == nowHHmm && _timerState.value.autoStartRoutineEnabled && !_timerState.value.isRunning)

                val blockedCount = ((profileState.value as? UiState.Success)?.data?.blockedPackages?.size ?: 6).coerceAtLeast(1)

                if (shouldAutoStartImmediately) {
                    autoTriggeredRoutineKeys.add("${id}_$nowHHmm")
                    configureTimerForRoutine(createdTask)
                    startOrResumeTimer(context)
                    DeviceFocusHelper.sendAutoStartedRoutineNotification(context, createdTask, blockedCount)
                    _statusBanner.value = "⏰ Custom Routine '$title' saved & Timer Auto-Started (${durationMinutes}m)! $blockedCount apps blocked & notification sent."
                } else {
                    if (notifyOnStart) {
                        DeviceFocusHelper.sendRoutineReminderNotification(context, createdTask)
                    }
                    _statusBanner.value = "Added '$title' ($period • $effectiveStartTime) to your daily routine. Timer will auto-start at $effectiveStartTime!"
                }
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to create routine task"
            }
        }
    }

    fun loadPresetRoutinePack(context: Context, pack: RoutinePresetPack) {
        viewModelScope.launch {
            val result = repository.applyPresetPack(pack)
            result.onSuccess { count ->
                _statusBanner.value = "Applied '${pack.name}' ($count blocks across Morning, Afternoon & Evening)!"
                DeviceFocusHelper.sendDailyScheduleDigestNotification(
                    context = context,
                    examTarget = pack.targetExam,
                    totalTasks = count,
                    totalMinutes = pack.totalStudyMinutes
                )
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to apply preset"
            }
        }
    }

    fun toggleRoutineTaskCompleted(task: RoutineTask) {
        viewModelScope.launch {
            val newComplete = !task.isCompletedToday
            repository.toggleRoutineCompletion(task, newComplete).onSuccess {
                if (newComplete && (task.blockType == "STUDY" || task.blockType == "DAILY_ROUTINE")) {
                    val examTarget = (profileState.value as? UiState.Success)?.data?.examTarget ?: task.examTag
                    repository.recordCompletedFocusSession(
                        routineTask = task,
                        title = task.title,
                        subject = task.subject,
                        period = task.period,
                        plannedMinutes = task.durationMinutes,
                        actualMinutes = task.durationMinutes,
                        blockedAttempts = 2L,
                        examTarget = examTarget
                    )
                    _statusBanner.value = "Completed '${task.title}'! Earned +${task.durationMinutes * 4 + 50} Focus XP."
                }
            }
        }
    }

    fun deleteRoutineTask(routineId: String) {
        viewModelScope.launch {
            repository.deleteRoutineTask(routineId).onSuccess {
                _statusBanner.value = "Routine block removed."
            }
        }
    }

    fun updateBlockerConfiguration(
        strictEnabled: Boolean,
        dailyNotificationEnabled: Boolean,
        blockedPackages: List<String>
    ) {
        viewModelScope.launch {
            repository.updateBlockerAndNotificationSettings(
                strictBlockEnabled = strictEnabled,
                dailyNotificationEnabled = dailyNotificationEnabled,
                blockedPackages = blockedPackages
            ).onSuccess {
                _timerState.value = _timerState.value.copy(strictShieldActive = strictEnabled)
                _statusBanner.value = "Focus Shield (${blockedPackages.size} apps blocked) & Notification settings updated."
            }
        }
    }

    fun claimMilestoneOrDailyStreakCheckIn(badgeId: String? = null) {
        viewModelScope.launch {
            repository.claimMilestoneOrStreakGoalCheckIn(badgeId).onSuccess { msg ->
                _statusBanner.value = msg
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Could not update streak milestone"
            }
        }
    }

    // --- FOCUS TIMER & ACTIVE APP BLOCKER CONTROLS ---

    fun configureTimerForRoutine(task: RoutineTask) {
        timerJob?.cancel()
        val mins = task.durationMinutes.toInt().coerceIn(5, 240)
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            isPaused = false,
            linkedRoutine = task,
            sessionTitle = task.title,
            subject = task.subject,
            period = task.period,
            plannedMinutes = mins,
            totalSeconds = mins * 60,
            remainingSeconds = mins * 60,
            elapsedSeconds = 0,
            strictShieldActive = true,
            blockedDistractionCount = 0
        )
    }

    fun configureQuickTimer(title: String, subject: String, period: String, durationMinutes: Int) {
        if (_timerState.value.isRunning) return
        val safeMins = durationMinutes.coerceIn(5, 240)
        _timerState.value = _timerState.value.copy(
            linkedRoutine = null,
            sessionTitle = title,
            subject = subject,
            period = period,
            plannedMinutes = safeMins,
            totalSeconds = safeMins * 60,
            remainingSeconds = safeMins * 60,
            elapsedSeconds = 0
        )
    }

    fun setAmbientSound(label: String) {
        _timerState.value = _timerState.value.copy(ambientSoundLabel = label)
    }

    fun startOrResumeTimer(context: Context) {
        val current = _timerState.value
        if (current.isRunning && !current.isPaused) return

        _timerState.value = current.copy(isRunning = true, isPaused = false)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning && !_timerState.value.isPaused && _timerState.value.remainingSeconds > 0) {
                delay(1000L)
                val s = _timerState.value
                val nextRemaining = (s.remainingSeconds - 1).coerceAtLeast(0)
                val nextElapsed = s.elapsedSeconds + 1

                // Real-time background/foreground app blocker check while studying as per routine
                val profile = (profileState.value as? UiState.Success)?.data
                val blockedList = profile?.blockedPackages ?: emptyList()
                val detectedBlockedApp = if (s.strictShieldActive && nextElapsed % 3 == 0) {
                    DeviceFocusHelper.detectForegroundBlockedApp(context, blockedList)
                } else null

                val bonusBlock = if (detectedBlockedApp != null) {
                    1
                } else if (s.strictShieldActive && nextElapsed % 35 == 0) {
                    1
                } else {
                    0
                }

                if (detectedBlockedApp != null) {
                    DeviceFocusHelper.sendBlockedAppInterventionNotification(
                        context = context,
                        interceptedAppName = detectedBlockedApp,
                        activeStudyTitle = s.sessionTitle,
                        examTarget = profile?.examTarget ?: "Study Routine"
                    )
                }

                _timerState.value = s.copy(
                    remainingSeconds = nextRemaining,
                    elapsedSeconds = nextElapsed,
                    blockedDistractionCount = s.blockedDistractionCount + bonusBlock,
                    showAppBlockInterceptorPreview = s.showAppBlockInterceptorPreview || (detectedBlockedApp != null),
                    lastInterceptedAppName = detectedBlockedApp ?: s.lastInterceptedAppName
                )
                if (nextRemaining == 0) {
                    completeCurrentFocusSession(context, fullCompletion = true)
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isPaused = true)
    }

    fun triggerAppBlockPreview(
        context: Context? = null,
        appName: String = "Instagram (Reels & Feed)"
    ) {
        val current = _timerState.value
        val examTarget = (profileState.value as? UiState.Success)?.data?.examTarget ?: "Exam Track"
        if (context != null) {
            DeviceFocusHelper.sendBlockedAppInterventionNotification(
                context = context,
                interceptedAppName = appName,
                activeStudyTitle = current.sessionTitle,
                examTarget = examTarget
            )
        }
        _timerState.value = current.copy(
            blockedDistractionCount = current.blockedDistractionCount + 1,
            showAppBlockInterceptorPreview = true,
            lastInterceptedAppName = appName
        )
    }

    fun dismissAppBlockPreview() {
        _timerState.value = _timerState.value.copy(showAppBlockInterceptorPreview = false)
    }

    fun completeCurrentFocusSession(context: Context, fullCompletion: Boolean = false) {
        timerJob?.cancel()
        val s = _timerState.value
        val actualMinutes = if (fullCompletion) {
            s.plannedMinutes.toLong()
        } else {
            maxOf(1L, (s.elapsedSeconds / 60).toLong().takeIf { it > 0 } ?: (s.plannedMinutes / 2).toLong().coerceAtLeast(5L))
        }
        val examTarget = (profileState.value as? UiState.Success)?.data?.examTarget ?: "NEET UG"
        val xpEarned = actualMinutes * 4L + if (actualMinutes >= s.plannedMinutes) 50L else 15L

        _timerState.value = s.copy(
            isRunning = false,
            isPaused = false,
            remainingSeconds = s.totalSeconds,
            elapsedSeconds = 0
        )

        viewModelScope.launch {
            repository.recordCompletedFocusSession(
                routineTask = s.linkedRoutine,
                title = s.sessionTitle,
                subject = s.subject,
                period = s.period,
                plannedMinutes = s.plannedMinutes.toLong(),
                actualMinutes = actualMinutes,
                blockedAttempts = maxOf(1L, s.blockedDistractionCount.toLong()),
                examTarget = examTarget
            ).onSuccess {
                DeviceFocusHelper.sendSessionCompletedNotification(
                    context = context,
                    title = s.sessionTitle,
                    minutes = actualMinutes,
                    xpEarned = xpEarned
                )
                _statusBanner.value = "Focus Session Saved! +$actualMinutes mins logged & +$xpEarned XP earned."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Could not save session"
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        val s = _timerState.value
        _timerState.value = s.copy(
            isRunning = false,
            isPaused = false,
            remainingSeconds = s.totalSeconds,
            elapsedSeconds = 0
        )
    }

    companion object {
        private const val TAG = "RegainVM"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
