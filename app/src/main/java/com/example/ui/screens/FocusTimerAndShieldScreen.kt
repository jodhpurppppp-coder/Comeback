package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AppBlocking
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.ExamCatalog
import com.example.data.UserProfile
import com.example.ui.ActiveTimerState
import com.example.ui.theme.ComeBackDialogBg
import com.example.ui.theme.ComeBackLogoIcon
import com.example.ui.theme.ComeBackOnPrimary
import com.example.ui.theme.ComeBackTrackColor
import com.example.ui.theme.RegainCoral
import com.example.ui.theme.RegainCyan
import com.example.ui.theme.RegainEmerald
import com.example.ui.theme.RegainEmeraldBright
import com.example.ui.theme.RegainGold
import com.example.util.DeviceFocusHelper
import com.example.util.InstalledAppItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FocusTimerAndShieldScreen(
    profile: UserProfile,
    timerState: ActiveTimerState,
    onStartOrResumeTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onCompleteSession: () -> Unit,
    onResetTimer: () -> Unit,
    onConfigureQuickTimer: (title: String, subject: String, period: String, minutes: Int) -> Unit,
    onSelectAmbientSound: (String) -> Unit,
    onUpdateBlockerConfig: (strictEnabled: Boolean, dailyNotifyEnabled: Boolean, blockedPackages: List<String>) -> Unit,
    onTriggerBlockInterceptorPreview: (String) -> Unit,
    onDismissBlockInterceptorPreview: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionRefreshTick by remember { mutableIntStateOf(0) }
    var showAddCustomAppDialog by remember { mutableStateOf(false) }
    var shortFormShield by remember(permissionRefreshTick) {
        mutableStateOf(DeviceFocusHelper.isShortFormShieldEnabled(context))
    }
    var autoStartRoutine by remember(permissionRefreshTick) {
        mutableStateOf(DeviceFocusHelper.isAutoStartRoutineTimerEnabled(context))
    }
    val permissionStatus = remember(permissionRefreshTick) {
        DeviceFocusHelper.checkPermissions(context)
    }
    val discoverableApps = remember(permissionRefreshTick) {
        DeviceFocusHelper.getDiscoverableAppsForBlocker(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionRefreshTick++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        permissionRefreshTick++
    }

    val subjects = remember(profile.examTarget) {
        ExamCatalog.getTrack(profile.examTarget).subjects
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("focus_shield_screen"),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Focus Timer Sanctuary Dial
        item {
            FocusTimerDialCard(
                timerState = timerState,
                subjects = subjects,
                onStartOrResume = onStartOrResumeTimer,
                onPause = onPauseTimer,
                onComplete = onCompleteSession,
                onReset = onResetTimer,
                onConfigureQuickTimer = onConfigureQuickTimer,
                onSelectAmbientSound = onSelectAmbientSound
            )
        }

        // 2. Device Permissions & Whole-Device Protection Status
        item {
            DevicePermissionsCenterCard(
                permissionStatus = permissionStatus,
                onRequestUsageStats = {
                    DeviceFocusHelper.openUsageAccessSettings(context)
                    permissionRefreshTick++
                },
                onRequestOverlay = {
                    DeviceFocusHelper.openOverlaySettings(context)
                    permissionRefreshTick++
                },
                onRequestNotifications = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !DeviceFocusHelper.hasNotificationPermission(context)) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        DeviceFocusHelper.openNotificationSettings(context)
                    }
                    permissionRefreshTick++
                },
                onRequestDnd = {
                    DeviceFocusHelper.openDndSettings(context)
                    permissionRefreshTick++
                },
                onRequestBatteryUnrestricted = {
                    DeviceFocusHelper.openBatteryOptimizationSettings(context)
                    permissionRefreshTick++
                },
                onGrantAllAndOpenSettings = {
                    DeviceFocusHelper.grantAllPermissionsViaSettings(context)
                    DeviceFocusHelper.openUsageAccessSettings(context)
                    permissionRefreshTick++
                },
                onRefreshStatus = { permissionRefreshTick++ }
            )
        }

        // 3. Strict App Blocker Controls & Interceptor Preview
        item {
            StrictShieldSettingsCard(
                strictEnabled = profile.strictBlockEnabled,
                shortFormShieldEnabled = shortFormShield,
                autoStartRoutineEnabled = autoStartRoutine,
                dailyNotifyEnabled = profile.dailyNotificationEnabled,
                blockedCount = profile.blockedPackages.size,
                onToggleStrict = { enabled ->
                    onUpdateBlockerConfig(enabled, profile.dailyNotificationEnabled, profile.blockedPackages)
                },
                onToggleShortFormShield = { enabled ->
                    shortFormShield = enabled
                    DeviceFocusHelper.setShortFormShieldEnabled(context, enabled)
                },
                onToggleAutoStartRoutine = { enabled ->
                    autoStartRoutine = enabled
                    DeviceFocusHelper.setAutoStartRoutineTimerEnabled(context, enabled)
                },
                onToggleDailyNotify = { enabled ->
                    onUpdateBlockerConfig(profile.strictBlockEnabled, enabled, profile.blockedPackages)
                },
                onPreviewShield = {
                    onTriggerBlockInterceptorPreview("Instagram (Reels & Feed)")
                }
            )
        }

        // 4. Header for Background App Blocker List + Add Custom App Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Routine App Blocker List (${profile.blockedPackages.size} Shielded)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Apps below are automatically blocked while studying as per your daily routine.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAddCustomAppDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("add_custom_blocked_app_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add App", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5. App Blocker Items
        items(discoverableApps, key = { it.packageName }) { appItem ->
            val isBlocked = profile.blockedPackages.contains(appItem.packageName)
            BlockableAppRow(
                app = appItem,
                isBlocked = isBlocked,
                onToggleBlocked = { shouldBlock ->
                    val updated = if (shouldBlock) {
                        (profile.blockedPackages + appItem.packageName).distinct()
                    } else {
                        profile.blockedPackages - appItem.packageName
                    }
                    onUpdateBlockerConfig(profile.strictBlockEnabled, profile.dailyNotificationEnabled, updated)
                },
                onSimulateIntercept = {
                    onTriggerBlockInterceptorPreview(appItem.appName)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddCustomAppDialog) {
        var customAppName by remember { mutableStateOf("") }
        var customPkgName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCustomAppDialog = false },
            containerColor = ComeBackDialogBg,
            title = {
                Text(
                    text = "Add App to Routine Blocker",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter the name of any distracting app or game to block automatically while studying as per your daily routine.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = customAppName,
                        onValueChange = { customAppName = it },
                        label = { Text("App Name (e.g., Free Fire, Discord, Reddit)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_blocked_app_name_input")
                    )
                    OutlinedTextField(
                        value = customPkgName,
                        onValueChange = { customPkgName = it },
                        label = { Text("Package ID (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanName = customAppName.trim().ifBlank { "Custom Game / Social App" }
                        val pkg = customPkgName.trim().ifBlank {
                            "com.custom.${cleanName.lowercase().replace(Regex("[^a-z0-9]"), "")}"
                        }
                        DeviceFocusHelper.addCustomAppToCatalog(context, cleanName, pkg)
                        val updated = (profile.blockedPackages + pkg).distinct()
                        onUpdateBlockerConfig(profile.strictBlockEnabled, profile.dailyNotificationEnabled, updated)
                        permissionRefreshTick++
                        showAddCustomAppDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    modifier = Modifier.testTag("confirm_add_custom_blocked_app_button")
                ) {
                    Text("Block During Routine", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomAppDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (timerState.showAppBlockInterceptorPreview) {
        RegainAppBlockerInterceptorDialog(
            interceptedAppName = timerState.lastInterceptedAppName,
            activeStudyTitle = timerState.sessionTitle,
            activeExamTarget = profile.examTarget,
            onReturnToStudy = onDismissBlockInterceptorPreview
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FocusTimerDialCard(
    timerState: ActiveTimerState,
    subjects: List<String>,
    onStartOrResume: () -> Unit,
    onPause: () -> Unit,
    onComplete: () -> Unit,
    onReset: () -> Unit,
    onConfigureQuickTimer: (title: String, subject: String, period: String, minutes: Int) -> Unit,
    onSelectAmbientSound: (String) -> Unit
) {
    val progress = if (timerState.totalSeconds > 0) {
        timerState.remainingSeconds.toFloat() / timerState.totalSeconds.toFloat()
    } else 1f
    val minutes = timerState.remainingSeconds / 60
    val seconds = timerState.remainingSeconds % 60

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = RegainEmerald.copy(alpha = 0.16f),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = RegainEmeraldBright,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (timerState.strictShieldActive) "STRICT APP SHIELD ON" else "STANDARD FOCUS",
                            style = MaterialTheme.typography.labelMedium,
                            color = RegainEmeraldBright
                        )
                    }
                }

                Surface(
                    color = RegainGold.copy(alpha = 0.16f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${timerState.period} • +${timerState.plannedMinutes * 4 + 50} XP",
                        style = MaterialTheme.typography.labelMedium,
                        color = RegainGold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = timerState.sessionTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Subject: ${timerState.subject}",
                style = MaterialTheme.typography.bodyMedium,
                color = RegainCyan
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Circular Countdown Dial
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 14.dp.toPx()
                    drawArc(
                        color = ComeBackTrackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(RegainEmerald, RegainCyan, RegainGold, RegainEmeraldBright)
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (timerState.isRunning && !timerState.isPaused) {
                            "DEEP FOCUS ACTIVE"
                        } else if (timerState.isPaused) {
                            "SESSION PAUSED"
                        } else {
                            "READY TO FOCUS"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = RegainEmeraldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Duration Presets (when not running)
            if (!timerState.isRunning) {
                Text(
                    text = "Duration Preset & Subject",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(25, 45, 60, 90, 120).forEach { mins ->
                        FilterChip(
                            selected = timerState.plannedMinutes == mins,
                            onClick = {
                                onConfigureQuickTimer(
                                    "${timerState.subject} Deep Study",
                                    timerState.subject,
                                    timerState.period,
                                    mins
                                )
                            },
                            label = { Text("${mins}m") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RegainEmerald.copy(alpha = 0.22f),
                                selectedLabelColor = RegainEmeraldBright
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subjects.take(5).forEach { sub ->
                        FilterChip(
                            selected = timerState.subject == sub,
                            onClick = {
                                onConfigureQuickTimer(
                                    "$sub Focus Block",
                                    sub,
                                    timerState.period,
                                    timerState.plannedMinutes
                                )
                            },
                            label = { Text(sub) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Ambient Soundscape Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = RegainCyan,
                    modifier = Modifier.size(18.dp)
                )
                listOf("Rainforest Sanctuary", "Deep Brown Noise", "40Hz Gamma Focus", "Silent Library").forEach { sound ->
                    FilterChip(
                        selected = timerState.ambientSoundLabel == sound,
                        onClick = { onSelectAmbientSound(sound) },
                        label = { Text(sound) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Timer Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (timerState.isRunning && !timerState.isPaused) {
                            onPause()
                        } else {
                            onStartOrResume()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(52.dp)
                        .testTag("timer_start_pause_button")
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning && !timerState.isPaused) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            timerState.isRunning && !timerState.isPaused -> "Pause"
                            timerState.isPaused -> "Resume"
                            else -> "Start Focus"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainGold.copy(alpha = 0.22f),
                        contentColor = RegainGold
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("timer_complete_button")
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("End & Log", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Timer")
                }
            }
        }
    }
}

@Composable
private fun DevicePermissionsCenterCard(
    permissionStatus: com.example.util.DevicePermissionStatus,
    onRequestUsageStats: () -> Unit,
    onRequestOverlay: () -> Unit,
    onRequestNotifications: () -> Unit,
    onRequestDnd: () -> Unit,
    onRequestBatteryUnrestricted: () -> Unit,
    onGrantAllAndOpenSettings: () -> Unit,
    onRefreshStatus: () -> Unit
) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Android Settings Permissions (${permissionStatus.grantedCount}/${permissionStatus.totalCount})",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Required for Comeback : Daily Routine whole-device background app blocking & alerts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = onRefreshStatus,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Recheck", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onGrantAllAndOpenSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainEmerald,
                    contentColor = ComeBackOnPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("grant_all_android_permissions_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Grant All & Open Android Settings",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            PermissionItemRow(
                icon = Icons.Default.QueryStats,
                title = "Usage Access (Detect Background Apps)",
                subtitle = "Opens Android Usage Access Settings so Comeback can detect distracting apps.",
                isGranted = permissionStatus.usageStatsGranted,
                onAction = onRequestUsageStats
            )
            Spacer(modifier = Modifier.height(10.dp))
            PermissionItemRow(
                icon = Icons.Default.Layers,
                title = "Display Over Other Apps (Focus Shield Overlay)",
                subtitle = "Opens Android Overlay Settings to show the Comeback Shield when a blocked app opens.",
                isGranted = permissionStatus.overlayGranted,
                onAction = onRequestOverlay
            )
            Spacer(modifier = Modifier.height(10.dp))
            PermissionItemRow(
                icon = Icons.Default.NotificationsActive,
                title = "Daily Routine & Study Notifications",
                subtitle = "Sends Morning, Afternoon & Evening study routine alerts every day.",
                isGranted = permissionStatus.notificationGranted,
                onAction = onRequestNotifications
            )
            Spacer(modifier = Modifier.height(10.dp))
            PermissionItemRow(
                icon = Icons.Default.DoNotDisturbOn,
                title = "Do Not Disturb Policy (Silence Distractions)",
                subtitle = "Opens Android DND Policy Settings to mute social pings while focusing.",
                isGranted = permissionStatus.dndPolicyGranted,
                onAction = onRequestDnd
            )
            Spacer(modifier = Modifier.height(10.dp))
            PermissionItemRow(
                icon = Icons.Default.CheckCircle,
                title = "Unrestricted Background Timer Service",
                subtitle = "Prevents Android battery saver from stopping your active study countdown.",
                isGranted = permissionStatus.batteryUnrestrictedGranted,
                onAction = onRequestBatteryUnrestricted
            )
        }
    }
}

@Composable
private fun PermissionItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isGranted: Boolean,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isGranted) RegainEmeraldBright else RegainGold,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (isGranted) {
            Surface(
                color = RegainEmerald.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = RegainEmeraldBright,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        } else {
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainEmerald,
                    contentColor = ComeBackOnPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Grant", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun StrictShieldSettingsCard(
    strictEnabled: Boolean,
    shortFormShieldEnabled: Boolean,
    autoStartRoutineEnabled: Boolean,
    dailyNotifyEnabled: Boolean,
    blockedCount: Int,
    onToggleStrict: (Boolean) -> Unit,
    onToggleShortFormShield: (Boolean) -> Unit,
    onToggleAutoStartRoutine: (Boolean) -> Unit,
    onToggleDailyNotify: (Boolean) -> Unit,
    onPreviewShield: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RegainEmerald.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Deep ComeBack Lock (Block Apps During Routine)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Automatically intercepts $blockedCount selected apps while studying as per your routine.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = strictEnabled,
                    onCheckedChange = onToggleStrict,
                    colors = SwitchDefaults.colors(checkedThumbColor = RegainEmeraldBright)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Short-Form Video Shield (Reels & Shorts)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Block Instagram Reels & YouTube Shorts while keeping study lectures accessible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = shortFormShieldEnabled,
                    onCheckedChange = onToggleShortFormShield,
                    colors = SwitchDefaults.colors(checkedThumbColor = RegainGold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-Start Routine Timer & App Blocker",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Automatically start countdown timer, lock apps & send notification at scheduled routine time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoStartRoutineEnabled,
                    onCheckedChange = onToggleAutoStartRoutine,
                    colors = SwitchDefaults.colors(checkedThumbColor = RegainEmeraldBright)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Routine Notification Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Notify me for Morning, Afternoon, Evening & Custom study blocks.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = dailyNotifyEnabled,
                    onCheckedChange = onToggleDailyNotify,
                    colors = SwitchDefaults.colors(checkedThumbColor = RegainCyan)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onPreviewShield,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_shield_interceptor_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = RegainEmeraldBright,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Test ComeBack Focus Shield Interceptor Screen",
                    color = RegainEmeraldBright,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BlockableAppRow(
    app: InstalledAppItem,
    isBlocked: Boolean,
    onToggleBlocked: (Boolean) -> Unit,
    onSimulateIntercept: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isBlocked) RegainCoral.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSimulateIntercept() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isBlocked) RegainCoral.copy(alpha = 0.16f) else RegainEmerald.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBlocked) Icons.Default.Lock else Icons.Default.AppBlocking,
                    contentDescription = null,
                    tint = if (isBlocked) RegainCoral else RegainEmeraldBright,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${app.categoryLabel} • ~${app.usageMinutesToday}m today • Tap to preview block",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isBlocked,
                onCheckedChange = onToggleBlocked,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RegainCoral,
                    checkedTrackColor = RegainCoral.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun RegainAppBlockerInterceptorDialog(
    interceptedAppName: String,
    activeStudyTitle: String,
    activeExamTarget: String,
    onReturnToStudy: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onReturnToStudy,
        containerColor = ComeBackDialogBg,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                ComeBackLogoIcon(size = 64.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ComeBack Shield Blocked $interceptedAppName",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Take a deep breath. You committed this time to your $activeExamTarget preparation:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Surface(
                    color = RegainEmerald.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Active Goal: $activeStudyTitle",
                        style = MaterialTheme.typography.titleMedium,
                        color = RegainEmeraldBright,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Text(
                    text = "Every distraction you resist builds your streak and brings you closer to your target rank.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RegainGold,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onReturnToStudy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainEmerald,
                    contentColor = ComeBackOnPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("return_to_study_shield_button")
            ) {
                Text("Return to Deep Focus (+5 Bonus XP)", fontWeight = FontWeight.Bold)
            }
        }
    )
}
