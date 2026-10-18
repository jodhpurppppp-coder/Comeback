package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ExamCatalog
import com.example.data.FocusSession
import com.example.data.GamificationCatalog
import com.example.data.RegainPresets
import com.example.data.RoutinePresetPack
import com.example.data.RoutineTask
import com.example.data.StudentExtrasStore
import com.example.data.UserProfile
import com.example.ui.ActiveTimerState
import com.example.ui.theme.ComeBackHeroGradient
import com.example.ui.theme.ComeBackLogoIcon
import com.example.ui.theme.ComeBackOnPrimary
import com.example.ui.theme.ComeBackThemeSelectorBar
import com.example.ui.theme.ComeBackTrackColor
import com.example.ui.theme.RegainCoral
import com.example.ui.theme.RegainCyan
import com.example.ui.theme.RegainEmerald
import com.example.ui.theme.RegainEmeraldBright
import com.example.ui.theme.RegainGold
import com.example.ui.theme.RegainPurple
import com.example.util.DeviceFocusHelper
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyDashboardScreen(
    profile: UserProfile,
    routines: List<RoutineTask>,
    sessions: List<FocusSession>,
    timerState: ActiveTimerState,
    onOpenExamSwitcher: () -> Unit,
    onStartRoutineFocus: (RoutineTask) -> Unit,
    onStartOrPauseInlineTimer: () -> Unit,
    onStopActiveSession: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onToggleRoutineComplete: (RoutineTask) -> Unit,
    onDeleteRoutine: (String) -> Unit,
    onApplyPresetPack: (RoutinePresetPack) -> Unit,
    onAddCustomRoutine: (
        title: String,
        subject: String,
        period: String,
        blockType: String,
        startTime: String,
        durationMinutes: Long,
        notifyOnStart: Boolean,
        autoStartNow: Boolean
    ) -> Unit,
    onSaveProfileAndBlocker: (
        displayName: String,
        strictBlock: Boolean,
        dailyNotify: Boolean
    ) -> Unit,
    onSignOut: () -> Unit,
    onNotifyTriggered: (String) -> Unit,
    onClaimMilestoneOrStreakCheckIn: (String?) -> Unit = {},
    onStartBadgeChallengeFocus: (com.example.data.BadgeMilestone) -> Unit = {},
    onNavigateToRewards: () -> Unit = {},
    onTriggerBlockInterceptorPreview: (String) -> Unit = {},
    onCheckAutoStartRoutine: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedPeriodFilter by remember { mutableStateOf("ALL") }
    var showAddRoutineDialog by remember { mutableStateOf(false) }

    // Upper-left Study Details / Channels / Account sheet & Upper-right Profile / Settings sheet
    var showUpperLeftStudyDetails by remember { mutableStateOf(false) }
    var showUpperRightProfileSettings by remember { mutableStateOf(false) }
    var extrasRefreshTick by remember { mutableIntStateOf(0) }

    val studentExtras = remember(profile.userId, profile.examTarget, extrasRefreshTick) {
        StudentExtrasStore.load(context, profile.userId.ifBlank { "default_user" }, profile.examTarget)
    }

    // Live clock updating every second + automatic routine schedule check
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        var tick = 0
        while (true) {
            delay(1000L)
            currentTimeMillis = System.currentTimeMillis()
            tick++
            if (tick % 3 == 0) {
                onCheckAutoStartRoutine()
            }
        }
    }
    val liveClockFormatted = remember(currentTimeMillis) {
        SimpleDateFormat("hh:mm:ss a • EEE, MMM d", Locale.US).format(Date(currentTimeMillis))
    }

    val filteredRoutines = remember(routines, selectedPeriodFilter) {
        if (selectedPeriodFilter == "ALL") {
            routines
        } else {
            routines.filter { it.period == selectedPeriodFilter }
        }
    }

    val nextUpcomingTask = remember(routines) {
        routines.firstOrNull { !it.isCompletedToday } ?: routines.firstOrNull()
    }

    val plannedStudyMinutesToday = remember(routines) {
        routines.filter { it.blockType == "STUDY" }.sumOf { it.durationMinutes }.toInt().coerceAtLeast(1)
    }
    val completedStudyMinutesToday = remember(routines, sessions) {
        val fromRoutines = routines.filter { it.blockType == "STUDY" }.sumOf { it.completedMinutesToday }.toInt()
        val fromSessions = sessions.take(6).sumOf { it.actualMinutes }.toInt()
        maxOf(fromRoutines, fromSessions)
    }
    val completedTasksCount = remember(routines) {
        routines.count { it.isCompletedToday }
    }

    val presetsForExam = remember(profile.examTarget) {
        RegainPresets.getPresetsForExam(profile.examTarget)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("daily_dashboard_list"),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Upper-Left ComeBack Logo & Study Guide + Center Exam Switcher + Upper-Right Profile & Settings Button
        item {
            UnifiedTopHeaderBar(
                profile = profile,
                extraDetails = studentExtras,
                onOpenUpperLeftStudyDetails = { showUpperLeftStudyDetails = true },
                onOpenExamSwitcher = onOpenExamSwitcher,
                onOpenUpperRightProfile = { showUpperRightProfileSettings = true }
            )
        }

        // 1b. Multi-Theme Switcher Strip (Logo Dark, Light Mode, Black & White Dark/Light, Sapphire & Mint)
        item {
            ComeBackThemeSelectorBar(
                onThemeChanged = { option ->
                    onNotifyTriggered("Theme set to '${option.label}' — ${option.subtitle}")
                }
            )
        }

        // 1c. Android System Settings Permissions Quick-Access Bar (Usage Access, Overlay Shield, Notifications & Battery)
        item {
            var permTick by remember { mutableIntStateOf(0) }
            val perms = remember(permTick) { DeviceFocusHelper.checkPermissions(context) }
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RegainEmerald.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .testTag("dashboard_android_permissions_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (perms.grantedCount >= 4) RegainEmeraldBright else RegainGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ANDROID SETTINGS PERMISSIONS (${perms.grantedCount}/${perms.totalCount} ACTIVE)",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (perms.grantedCount >= 4) RegainEmeraldBright else RegainGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Usage Access • Display Over Apps • Routine Alerts • Battery",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                DeviceFocusHelper.grantAllPermissionsViaSettings(context)
                                DeviceFocusHelper.openUsageAccessSettings(context)
                                permTick++
                                onNotifyTriggered("Opened Android Usage Access Settings & activated Comeback Device Shield!")
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("dashboard_open_android_settings_button")
                        ) {
                            Text("Android Settings", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = onNavigateToTimer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RegainEmerald,
                                contentColor = ComeBackOnPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Shield Hub", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Live Real-Time Clock & 1-Tap "Plan & Auto-Start Schedule From Right Now" Bar
        item {
            LiveTimeAndScheduleNowBar(
                liveClockText = liveClockFormatted,
                examTarget = profile.examTarget,
                onPlanFromNow = {
                    val cal = Calendar.getInstance()
                    val hour = cal.get(Calendar.HOUR_OF_DAY)
                    val min = cal.get(Calendar.MINUTE)
                    val startStr = String.format(Locale.US, "%02d:%02d", hour, min)
                    val period = when (hour) {
                        in 4..11 -> "MORNING"
                        in 12..16 -> "AFTERNOON"
                        in 17..21 -> "EVENING"
                        else -> "CUSTOM"
                    }
                    val firstSub = ExamCatalog.getTrack(profile.examTarget).subjects.firstOrNull() ?: "Physics"
                    onAddCustomRoutine(
                        "$firstSub Auto-Timer Routine",
                        firstSub,
                        period,
                        "STUDY",
                        startStr,
                        45L,
                        true,
                        true
                    )
                },
                onOpenCustomRoutineModal = {
                    showAddRoutineDialog = true
                }
            )
        }

        // 3. Hero Spotlight + Inline Compact Focus Timer & App Shield Controls
        item {
            NextUpcomingOrActiveSpotlightCard(
                timerState = timerState,
                nextTask = nextUpcomingTask,
                examTarget = profile.examTarget,
                blockedAppCount = profile.blockedPackages.size,
                onStartTaskFocus = { task ->
                    onStartRoutineFocus(task)
                    DeviceFocusHelper.sendAutoStartedRoutineNotification(
                        context = context,
                        task = task,
                        blockedAppCount = profile.blockedPackages.size.coerceAtLeast(1)
                    )
                    onNotifyTriggered("Started '${task.title}'! Timer running, notification sent & ${profile.blockedPackages.size} apps blocked.")
                },
                onStartOrPauseInline = onStartOrPauseInlineTimer,
                onQuickFocusStart = onNavigateToTimer,
                onEndActiveSession = onStopActiveSession,
                onTestAppBlockerDuringRoutine = {
                    if (!timerState.isRunning && nextUpcomingTask != null) {
                        onStartRoutineFocus(nextUpcomingTask)
                    }
                    onTriggerBlockInterceptorPreview("Instagram (Reels & Feed)")
                },
                onSendNotification = { task ->
                    val sent = DeviceFocusHelper.sendRoutineReminderNotification(context, task)
                    if (sent) {
                        onNotifyTriggered("Sent device notification for '${task.title}' (${task.startTime})!")
                    } else {
                        onNotifyTriggered("Reminder queued for '${task.title}' (${task.startTime}).")
                    }
                }
            )
        }

        // 4. Study Streak & Milestone System (Consecutive Days Goal Tracker + 'Early Bird' & 'Deep Work Master' Digital Badges)
        item {
            StudyStreakAndMilestoneDashboardHub(
                profile = profile,
                plannedStudyMinutes = maxOf(plannedStudyMinutesToday, profile.dailyGoalMinutes.toInt()),
                completedStudyMinutes = completedStudyMinutesToday,
                totalRoutineBlocks = routines.size,
                completedBlocks = completedTasksCount,
                expectedScore = studentExtras.expectedScore,
                maxScore = studentExtras.maxScore,
                targetYear = studentExtras.targetExamYear,
                onClaimMilestoneOrStreakCheckIn = onClaimMilestoneOrStreakCheckIn,
                onStartBadgeChallengeFocus = onStartBadgeChallengeFocus,
                onNavigateToRewards = onNavigateToRewards
            )
        }

        // 5. My Study Channels Quick-Launch Strip
        item {
            StudyChannelsQuickStrip(
                channels = studentExtras.studyChannels,
                onAddChannelClick = { showUpperLeftStudyDetails = true },
                onStartChannelFocus = { ch ->
                    onAddCustomRoutine(
                        "${ch.channelName}: ${ch.teacherOrTopic}",
                        ch.teacherOrTopic,
                        "MORNING",
                        "STUDY",
                        "Now",
                        60L,
                        true,
                        true
                    )
                }
            )
        }

        // 6. Regain Preset Routine Packs (Morning, Afternoon, Evening)
        item {
            PresetPacksRow(
                examTarget = profile.examTarget,
                packs = presetsForExam,
                onApplyPack = onApplyPresetPack
            )
        }

        // 7. Continuous Daily Timeline Filter Bar + Add Block Button
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Continuous Daily Study Timeline",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Morning • Afternoon • Evening • Custom Routine",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { showAddRoutineDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RegainEmerald,
                            contentColor = ComeBackOnPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_routine_task_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Block",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "ALL" to "All (${routines.size})",
                        "MORNING" to "Morning",
                        "AFTERNOON" to "Afternoon",
                        "EVENING" to "Evening",
                        "CUSTOM" to "Custom"
                    )
                    filters.forEach { (key, label) ->
                        val selected = selectedPeriodFilter == key
                        FilterChip(
                            selected = selected,
                            onClick = { selectedPeriodFilter = key },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RegainEmerald.copy(alpha = 0.22f),
                                selectedLabelColor = RegainEmeraldBright
                            ),
                            modifier = Modifier.testTag("period_filter_$key")
                        )
                    }
                }
            }
        }

        // 8. Continuous Timeline Routine Cards or Empty State
        if (filteredRoutines.isEmpty()) {
            item {
                EmptyRoutineCard(
                    examTarget = profile.examTarget,
                    onLoadDefaultPreset = {
                        presetsForExam.firstOrNull()?.let(onApplyPresetPack)
                    },
                    onOpenCustomModal = { showAddRoutineDialog = true }
                )
            }
        } else {
            items(filteredRoutines, key = { it.id.ifBlank { it.title + it.startTime } }) { task ->
                RoutineScheduleBlockCard(
                    task = task,
                    isNextUp = nextUpcomingTask?.id == task.id && !task.isCompletedToday,
                    onStartFocus = {
                        onStartRoutineFocus(task)
                        DeviceFocusHelper.sendAutoStartedRoutineNotification(
                            context = context,
                            task = task,
                            blockedAppCount = profile.blockedPackages.size.coerceAtLeast(1)
                        )
                        onNotifyTriggered("Started '${task.title}' (${task.durationMinutes}m)! Timer running & ${profile.blockedPackages.size} apps blocked.")
                    },
                    onTestAppBlock = {
                        onStartRoutineFocus(task)
                        onTriggerBlockInterceptorPreview("Instagram (Reels & Feed)")
                    },
                    onToggleComplete = { onToggleRoutineComplete(task) },
                    onSendReminder = {
                        val sent = DeviceFocusHelper.sendRoutineReminderNotification(context, task)
                        if (sent) {
                            onNotifyTriggered("Daily routine notification sent for '${task.title}'!")
                        } else {
                            onNotifyTriggered("Reminder triggered for '${task.title}' (${task.startTime}).")
                        }
                    },
                    onDelete = { if (task.id.isNotBlank()) onDeleteRoutine(task.id) }
                )
            }
        }

        // 9. Continuous Study Session History Log ("create a history + continuous timeline")
        item {
            StudyHistoryLogSection(sessions = sessions)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddRoutineDialog) {
        val subjects = ExamCatalog.getTrack(profile.examTarget).subjects + listOf("Break / Recharge", "Daily Wellness")
        AddRoutineBlockDialog(
            availableSubjects = subjects,
            onDismiss = { showAddRoutineDialog = false },
            onConfirm = { title, subject, period, blockType, startTime, duration, notify, autoStartNow ->
                showAddRoutineDialog = false
                onAddCustomRoutine(title, subject, period, blockType, startTime, duration, notify, autoStartNow)
            }
        )
    }

    if (showUpperLeftStudyDetails) {
        StudyDetailsAndAccountDialog(
            profile = profile,
            extraDetails = studentExtras,
            onDismiss = { showUpperLeftStudyDetails = false },
            onAddChannel = { chName, topic ->
                StudentExtrasStore.addStudyChannel(
                    context = context,
                    userId = profile.userId.ifBlank { "default_user" },
                    examTarget = profile.examTarget,
                    channelName = chName,
                    teacherOrTopic = topic
                )
                extrasRefreshTick++
                onNotifyTriggered("Added study channel '$chName' to your hub!")
            },
            onRemoveChannel = { chId ->
                StudentExtrasStore.removeStudyChannel(
                    context = context,
                    userId = profile.userId.ifBlank { "default_user" },
                    examTarget = profile.examTarget,
                    channelId = chId
                )
                extrasRefreshTick++
            },
            onStartChannelStudy = { ch ->
                onAddCustomRoutine(
                    "${ch.channelName}: ${ch.teacherOrTopic}",
                    ch.teacherOrTopic,
                    "MORNING",
                    "STUDY",
                    "Now",
                    60L,
                    true,
                    true
                )
            },
            onOpenExamSwitcher = onOpenExamSwitcher,
            onSignOut = onSignOut
        )
    }

    if (showUpperRightProfileSettings) {
        ProfileAndSettingsDialog(
            profile = profile,
            extraDetails = studentExtras,
            onDismiss = { showUpperRightProfileSettings = false },
            onSaveProfileAndExtras = { name, mobile, age, yr, score, avatarIdx, avatarUri, strict, notify ->
                StudentExtrasStore.saveProfileExtras(
                    context = context,
                    userId = profile.userId.ifBlank { "default_user" },
                    examTarget = profile.examTarget,
                    mobileNumber = mobile,
                    ageYears = age,
                    targetExamYear = yr,
                    expectedScore = score,
                    avatarPresetIndex = avatarIdx,
                    customAvatarUri = avatarUri
                )
                extrasRefreshTick++
                onSaveProfileAndBlocker(name, strict, notify)
                onNotifyTriggered("Saved Profile: Target $score/${studentExtras.maxScore} ($yr) • Age $age yrs")
            },
            onSignOut = onSignOut
        )
    }
}

@Composable
private fun UnifiedTopHeaderBar(
    profile: UserProfile,
    extraDetails: com.example.data.StudentExtraDetails,
    onOpenUpperLeftStudyDetails: () -> Unit,
    onOpenExamSwitcher: () -> Unit,
    onOpenUpperRightProfile: () -> Unit
) {
    val avatarColor = ScholarAvatarColors[extraDetails.avatarPresetIndex % ScholarAvatarColors.size]

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // UPPER LEFT: ComeBack Logo, Study Guide & Account Menu
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenUpperLeftStudyDetails)
                .border(1.dp, RegainEmerald.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .testTag("top_left_study_details_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComeBackLogoIcon(size = 34.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Comeback",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily Routine",
                        style = MaterialTheme.typography.labelMedium,
                        color = RegainEmeraldBright
                    )
                }
            }
        }

        // CENTER: Active Exam Track + Target Score Pill
        Surface(
            color = RegainEmerald.copy(alpha = 0.14f),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenExamSwitcher)
                .border(1.dp, RegainEmerald.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .testTag("switch_exam_chip")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "Switch Exam",
                        tint = RegainEmeraldBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.examTarget} (${extraDetails.targetExamYear})",
                        style = MaterialTheme.typography.labelLarge,
                        color = RegainEmeraldBright
                    )
                }
                Text(
                    text = "Goal: ${extraDetails.expectedScore}/${extraDetails.maxScore} • Age ${extraDetails.ageYears}",
                    style = MaterialTheme.typography.labelMedium,
                    color = RegainGold
                )
            }
        }

        // UPPER RIGHT: Profile Avatar, Mobile Number, Age & Settings
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenUpperRightProfile)
                .border(1.dp, avatarColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .testTag("top_right_profile_settings_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(avatarColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.displayName.take(2).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = avatarColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Profile & Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun LiveTimeAndScheduleNowBar(
    liveClockText: String,
    examTarget: String,
    onPlanFromNow: () -> Unit,
    onOpenCustomRoutineModal: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RegainCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = RegainCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "LIVE TIME: $liveClockText",
                            style = MaterialTheme.typography.labelMedium,
                            color = RegainCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Custom daily routines auto-start timer, notify you & block other apps!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPlanFromNow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainCyan,
                        contentColor = ComeBackOnPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("plan_from_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto-Start Now", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onOpenCustomRoutineModal,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("custom_daily_routine_builder_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = RegainEmeraldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Custom Routine", style = MaterialTheme.typography.labelLarge, color = RegainEmeraldBright)
                }
            }
        }
    }
}

@Composable
private fun NextUpcomingOrActiveSpotlightCard(
    timerState: ActiveTimerState,
    nextTask: RoutineTask?,
    examTarget: String,
    blockedAppCount: Int,
    onStartTaskFocus: (RoutineTask) -> Unit,
    onStartOrPauseInline: () -> Unit,
    onQuickFocusStart: () -> Unit,
    onEndActiveSession: () -> Unit,
    onTestAppBlockerDuringRoutine: () -> Unit,
    onSendNotification: (RoutineTask) -> Unit
) {
    val mins = timerState.remainingSeconds / 60
    val secs = timerState.remainingSeconds % 60

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = if (timerState.isRunning) {
                        listOf(RegainGold, RegainEmeraldBright)
                    } else {
                        listOf(RegainEmerald, RegainCyan)
                    }
                ),
                shape = RoundedCornerShape(22.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(colors = ComeBackHeroGradient)
                )
                .padding(18.dp)
        ) {
            Column {
                // Side-by-side layout: Left = Upcoming/Active Task Details, Right = Live Focus Timer Dock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Surface(
                            color = if (timerState.isRunning) RegainGold.copy(alpha = 0.2f) else RegainEmerald.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (timerState.isRunning) Icons.Default.Shield else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (timerState.isRunning) RegainGold else RegainEmeraldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (timerState.isRunning) {
                                        "SHIELD LOCKED • ${timerState.period}"
                                    } else if (nextTask != null) {
                                        "NEXT UP • ${nextTask.period} (${nextTask.startTime})"
                                    } else {
                                        "FOCUS TIMER & SHIELD"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (timerState.isRunning) RegainGold else RegainEmeraldBright
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (timerState.isRunning) {
                                timerState.sessionTitle
                            } else {
                                nextTask?.title ?: "$examTarget Deep Focus Block"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (timerState.isRunning) {
                                "${timerState.subject} • $blockedAppCount apps blocked (${timerState.blockedDistractionCount} Attempts Stopped)"
                            } else if (nextTask != null) {
                                "${nextTask.subject} • ${nextTask.durationMinutes}m • Blocks $blockedAppCount apps while studying"
                            } else {
                                "Blocks $blockedAppCount distracting apps for $examTarget"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Side Focus Timer Readout Box
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, RegainEmerald.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%02d:%02d", mins, secs),
                            style = MaterialTheme.typography.headlineMedium,
                            color = RegainEmeraldBright,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (timerState.isRunning && !timerState.isPaused) "FOCUSING" else "AUTO-TIMER",
                            style = MaterialTheme.typography.labelMedium,
                            color = RegainCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Routine App Blocker Status & 1-Tap Test Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RegainEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = RegainEmeraldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Routine App Blocker: $blockedAppCount Apps Shielded (Reels, Shorts, Social)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = onTestAppBlockerDuringRoutine,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("dashboard_test_app_blocker_button")
                        ) {
                            Text(
                                text = "Test App Block",
                                style = MaterialTheme.typography.labelMedium,
                                color = RegainGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timerState.isRunning) {
                        Button(
                            onClick = onStartOrPauseInline,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RegainEmerald,
                                contentColor = ComeBackOnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (timerState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (timerState.isPaused) "Resume" else "Pause", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onEndActiveSession,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("end_active_session_dashboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                tint = RegainCoral,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("End & Log", color = RegainCoral)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (nextTask != null) {
                                    onStartTaskFocus(nextTask)
                                } else {
                                    onStartOrPauseInline()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RegainEmerald,
                                contentColor = ComeBackOnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("start_next_task_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (nextTask != null) "Start Focus (${nextTask.durationMinutes}m)" else "Start Focus Now",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = onQuickFocusStart,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Shield & Timer")
                        }
                        if (nextTask != null) {
                            IconButton(
                                onClick = { onSendNotification(nextTask) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .testTag("notify_next_task_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Send Routine Notification",
                                    tint = RegainCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyAdherenceAndMilestonesCard(
    profile: UserProfile,
    plannedStudyMinutes: Int,
    completedStudyMinutes: Int,
    totalRoutineBlocks: Int,
    completedBlocks: Int,
    expectedScore: Int,
    maxScore: Int,
    targetYear: Int
) {
    val progress = (completedStudyMinutes.toFloat() / plannedStudyMinutes.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 8.dp.toPx()
                        drawArc(
                            color = ComeBackTrackColor,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(RegainEmerald, RegainCyan, RegainEmeraldBright)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.titleMedium,
                            color = RegainEmeraldBright,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Streak: ${profile.currentStreak}d",
                            style = MaterialTheme.typography.titleMedium,
                            color = RegainGold
                        )
                        Text(
                            text = "• ${profile.xpPoints} XP",
                            style = MaterialTheme.typography.titleMedium,
                            color = RegainCyan
                        )
                    }
                    Text(
                        text = "${completedStudyMinutes}m focused / ${plannedStudyMinutes}m planned ($completedBlocks/$totalRoutineBlocks blocks)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Target: $expectedScore/$maxScore in ${profile.examTarget} $targetYear",
                        style = MaterialTheme.typography.labelMedium,
                        color = RegainEmeraldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Study Streak & Milestone Badges Strip ('Early Bird' / 'Dawn Scholar', 'Deep Work Master', 'Perfect Week', '100 Hours Studied')
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GamificationCatalog.badges.forEach { badge ->
                    val unlocked = badge.checkUnlocked(profile) || profile.unlockedBadges.contains(badge.id)
                    Surface(
                        color = if (unlocked) RegainGold.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.border(
                            1.dp,
                            if (unlocked) RegainGold.copy(alpha = 0.6f) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = if (unlocked) RegainGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = badge.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (unlocked) RegainGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyChannelsQuickStrip(
    channels: List<com.example.data.StudyChannelItem>,
    onAddChannelClick: () -> Unit,
    onStartChannelFocus: (com.example.data.StudyChannelItem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = RegainCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "My Study Channels & Lecture Hub",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            TextButton(onClick = onAddChannelClick) {
                Text("+ Add Your Channel", color = RegainEmeraldBright)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            channels.forEach { ch ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onStartChannelFocus(ch) }
                        .border(1.dp, RegainCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = ch.channelName,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ch.teacherOrTopic,
                                style = MaterialTheme.typography.bodyMedium,
                                color = RegainCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Schedule channel study",
                            tint = RegainEmeraldBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyHistoryLogSection(
    sessions: List<FocusSession>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = RegainGold
                )
                Column {
                    Text(
                        text = "Completed Study History Log (${sessions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Every completed focus session & earned XP recorded in real time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (sessions.isEmpty()) {
                Text(
                    text = "No completed sessions yet today. Tap 'Start Focus' above or check off a routine block to log your first session!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                sessions.take(6).forEach { session ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${session.subject} • ${session.period} • ${session.dateKey}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${session.actualMinutes}m / ${session.plannedMinutes}m",
                                style = MaterialTheme.typography.labelLarge,
                                color = RegainEmeraldBright
                            )
                            Text(
                                text = "+${session.xpEarned} XP",
                                style = MaterialTheme.typography.labelMedium,
                                color = RegainGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetPacksRow(
    examTarget: String,
    packs: List<RoutinePresetPack>,
    onApplyPack: (RoutinePresetPack) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = RegainGold,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Comeback : Daily Routine • $examTarget Presets (1-Tap Load)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            packs.forEach { pack ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(280.dp)
                        .border(1.dp, RegainEmerald.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = pack.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pack.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${pack.items.size} blocks • ${pack.totalStudyMinutes / 60}h ${pack.totalStudyMinutes % 60}m",
                                style = MaterialTheme.typography.labelMedium,
                                color = RegainCyan
                            )
                            TextButton(
                                onClick = { onApplyPack(pack) },
                                modifier = Modifier.testTag("apply_preset_${pack.id}")
                            ) {
                                Text("Apply Preset", color = RegainEmeraldBright, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineScheduleBlockCard(
    task: RoutineTask,
    isNextUp: Boolean,
    onStartFocus: () -> Unit,
    onTestAppBlock: () -> Unit = {},
    onToggleComplete: () -> Unit,
    onSendReminder: () -> Unit,
    onDelete: () -> Unit
) {
    val blockAccent = when (task.blockType) {
        "BREAK" -> RegainGold
        "DAILY_ROUTINE" -> RegainPurple
        else -> when (task.period) {
            "MORNING" -> RegainEmeraldBright
            "AFTERNOON" -> RegainCyan
            "EVENING" -> RegainPurple
            else -> RegainEmerald
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isNextUp) {
                blockAccent.copy(alpha = 0.11f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isNextUp) 1.5.dp else 1.dp,
                color = if (isNextUp) blockAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("toggle_routine_${task.id}")
                ) {
                    Icon(
                        imageVector = if (task.isCompletedToday) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.RadioButtonUnchecked
                        },
                        contentDescription = "Mark routine complete",
                        tint = if (task.isCompletedToday) RegainEmeraldBright else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = blockAccent.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${task.startTime} • ${task.period}",
                                style = MaterialTheme.typography.labelMedium,
                                color = blockAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${task.durationMinutes} min",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        if (isNextUp) {
                            Surface(
                                color = RegainGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "UP NEXT",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = RegainGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.isCompletedToday) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (task.isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${task.subject} • ${task.blockType.replace("_", " ")} • ${task.presetName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onSendReminder,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Trigger daily notification for this task",
                        tint = if (task.notifyOnStart) RegainCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete routine task",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (!task.isCompletedToday) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onTestAppBlock,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = RegainGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Shields Apps",
                            style = MaterialTheme.typography.labelMedium,
                            color = RegainGold
                        )
                    }
                    Button(
                        onClick = onStartFocus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blockAccent.copy(alpha = 0.2f),
                            contentColor = blockAccent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Timer & Shield (${task.durationMinutes}m)",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRoutineCard(
    examTarget: String,
    onLoadDefaultPreset: () -> Unit,
    onOpenCustomModal: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = RegainGold,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Your $examTarget Routine is Empty",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Load a complete Morning, Afternoon & Evening preset for $examTarget or create your own custom study block.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onLoadDefaultPreset,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Load $examTarget Preset", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onOpenCustomModal,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Custom Block")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddRoutineBlockDialog(
    availableSubjects: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        subject: String,
        period: String,
        blockType: String,
        startTime: String,
        durationMinutes: Long,
        notifyOnStart: Boolean,
        autoStartNow: Boolean
    ) -> Unit
) {
    val nowTimeStr = remember {
        SimpleDateFormat("HH:mm", Locale.US).format(Date())
    }
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf(availableSubjects.firstOrNull() ?: "Physics") }
    var period by remember { mutableStateOf("CUSTOM") }
    var blockType by remember { mutableStateOf("STUDY") }
    var startTime by remember { mutableStateOf(nowTimeStr) }
    var durationStr by remember { mutableStateOf("45") }
    var notifyOnStart by remember { mutableStateOf(true) }
    var autoStartTimer by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Create Custom Daily Routine",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Set your routine time, auto-start timer, app blocker & daily notification",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Title (e.g., Morning Formula Revision)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_routine_title_input")
                )

                Text(
                    text = "Time of Day Slot",
                    style = MaterialTheme.typography.labelLarge,
                    color = RegainEmeraldBright
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("MORNING", "AFTERNOON", "EVENING", "CUSTOM").forEach { slot ->
                        FilterChip(
                            selected = period == slot,
                            onClick = {
                                period = slot
                                startTime = when (slot) {
                                    "MORNING" -> "06:30"
                                    "AFTERNOON" -> "14:30"
                                    "EVENING" -> "19:00"
                                    else -> nowTimeStr
                                }
                            },
                            label = { Text(slot) }
                        )
                    }
                }

                Text(
                    text = "Block Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = RegainCyan
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("STUDY" to "Study Block", "DAILY_ROUTINE" to "Daily Routine", "BREAK" to "Break").forEach { (key, label) ->
                        FilterChip(
                            selected = blockType == key,
                            onClick = { blockType = key },
                            label = { Text(label) }
                        )
                    }
                }

                Text(
                    text = "Subject / Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = RegainGold
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableSubjects.take(6).forEach { sub ->
                        FilterChip(
                            selected = subject == sub,
                            onClick = { subject = sub },
                            label = { Text(sub) }
                        )
                    }
                }

                // Quick Start Time Presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        nowTimeStr to "Now ($nowTimeStr)",
                        "06:30" to "06:30",
                        "09:00" to "09:00",
                        "14:30" to "14:30",
                        "19:00" to "19:00",
                        "21:30" to "21:30"
                    ).forEach { (tVal, tLabel) ->
                        FilterChip(
                            selected = startTime == tVal,
                            onClick = { startTime = tVal },
                            label = { Text(tLabel) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start (HH:mm)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = durationStr,
                        onValueChange = { durationStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Minutes") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Auto-Start Timer & Block Other Apps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = autoStartTimer,
                        onCheckedChange = { autoStartTimer = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = RegainEmeraldBright)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Send Daily Notification Alert",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = notifyOnStart,
                        onCheckedChange = { notifyOnStart = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = RegainCyan)
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val mins = durationStr.toLongOrNull()?.coerceIn(5L, 360L) ?: 45L
                        val cleanTitle = title.ifBlank { "$subject ${period.lowercase().replaceFirstChar { it.uppercase() }} Routine" }
                        onConfirm(cleanTitle, subject, period, blockType, startTime.ifBlank { nowTimeStr }, mins, notifyOnStart, false)
                    }
                ) {
                    Text("Save Schedule")
                }
                Button(
                    onClick = {
                        val mins = durationStr.toLongOrNull()?.coerceIn(5L, 360L) ?: 45L
                        val cleanTitle = title.ifBlank { "$subject ${period.lowercase().replaceFirstChar { it.uppercase() }} Routine" }
                        onConfirm(cleanTitle, subject, period, blockType, startTime.ifBlank { nowTimeStr }, mins, notifyOnStart, autoStartTimer)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    modifier = Modifier.testTag("dialog_save_routine_button")
                ) {
                    Text(
                        text = if (autoStartTimer) "Save & Auto-Start" else "Save Routine Block",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
