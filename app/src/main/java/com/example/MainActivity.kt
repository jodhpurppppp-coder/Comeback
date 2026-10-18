package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.FocusSession
import com.example.data.RegainRepository
import com.example.data.RoutineTask
import com.example.data.UserProfile
import com.example.ui.RegainViewModel
import com.example.ui.UiState
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOut
import com.example.ui.onboarding.ExamSelectionScreen
import com.example.ui.screens.AnalyticsProgressScreen
import com.example.ui.screens.DailyDashboardScreen
import com.example.ui.screens.FocusTimerAndShieldScreen
import com.example.ui.screens.GamificationRewardsScreen
import com.example.ui.screens.RegainAppBlockerInterceptorDialog
import com.example.ui.theme.ComeBackOnPrimary
import com.example.ui.theme.RegainEmerald
import com.example.ui.theme.RegainEmeraldBright
import com.example.ui.theme.RegainFocusTheme
import com.example.util.DeviceFocusHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DeviceFocusHelper.ensureNotificationChannels(this)
        setContent {
            RegainFocusTheme {
                AppNavigation()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

enum class MainDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    FOCUS_SHIELD("focus_shield", "Focus & Shield", Icons.Filled.Shield, Icons.Outlined.Shield),
    ANALYTICS("analytics", "Analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics),
    REWARDS("rewards", "Rewards", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(
            onAuthSuccess = { /* AuthStateListener updates currentUser */ }
        )
    } else {
        RegainAuthenticatedApp(
            currentUserId = user.uid,
            defaultDisplayName = user.displayName ?: "Scholar"
        )
    }
}

@Composable
fun RegainAuthenticatedApp(
    currentUserId: String,
    defaultDisplayName: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: RegainViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                RegainViewModel(RegainRepository(db), currentUserId)
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val routinesState by viewModel.routinesState.collectAsStateWithLifecycle()
    val sessionsState by viewModel.sessionsState.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val showExamSwitcher by viewModel.showExamSwitcherModal.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableStateOf(MainDestination.DASHBOARD) }

    BackHandler(enabled = showExamSwitcher || currentTab != MainDestination.DASHBOARD) {
        if (showExamSwitcher) {
            viewModel.setShowExamSwitcherModal(false)
        } else {
            currentTab = MainDestination.DASHBOARD
        }
    }

    when (val pState = profileState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RegainEmeraldBright)
            }
        }
        is UiState.Error -> {
            // Even if profile query had a transient issue, allow user to configure their exam track
            ExamSelectionScreen(
                initialName = defaultDisplayName,
                onConfirmSelection = { name, cat, target, goal, applyPreset ->
                    viewModel.selectExamAndInitialize(context, name, cat, target, goal, applyPreset)
                }
            )
        }
        is UiState.Success -> {
            val profile = pState.data
            if (profile == null || profile.examCategory == "UNSET" || profile.examTarget == "Not Selected" || showExamSwitcher) {
                ExamSelectionScreen(
                    initialName = profile?.displayName ?: defaultDisplayName,
                    initialCategory = profile?.examCategory ?: "BOARDS",
                    initialTarget = profile?.examTarget ?: "Class 10",
                    isModalSwitcher = profile != null && profile.examCategory != "UNSET",
                    onDismissModal = { viewModel.setShowExamSwitcherModal(false) },
                    onConfirmSelection = { name, cat, target, goal, applyPreset ->
                        viewModel.selectExamAndInitialize(context, name, cat, target, goal, applyPreset)
                    }
                )
            } else {
                val routines: List<RoutineTask> = (routinesState as? UiState.Success)?.data ?: emptyList()
                val sessions: List<FocusSession> = (sessionsState as? UiState.Success)?.data ?: emptyList()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            MainDestination.entries.forEach { dest ->
                                val selected = currentTab == dest
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = dest },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                            contentDescription = dest.label
                                        )
                                    },
                                    label = { Text(dest.label) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ComeBackOnPrimary,
                                        selectedTextColor = RegainEmeraldBright,
                                        indicatorColor = RegainEmerald
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${dest.route}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        AnimatedVisibility(visible = statusBanner != null) {
                            statusBanner?.let { msg ->
                                Surface(
                                    color = RegainEmerald.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 6.dp)
                                        .clickable { viewModel.clearBanner() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = msg,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = RegainEmeraldBright,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Dismiss",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (currentTab) {
                                MainDestination.DASHBOARD -> {
                                    DailyDashboardScreen(
                                        profile = profile,
                                        routines = routines,
                                        sessions = sessions,
                                        timerState = timerState,
                                        onOpenExamSwitcher = { viewModel.setShowExamSwitcherModal(true) },
                                        onStartRoutineFocus = { task ->
                                            viewModel.configureTimerForRoutine(task)
                                            viewModel.startOrResumeTimer(context)
                                        },
                                        onStartOrPauseInlineTimer = {
                                            if (timerState.isRunning && !timerState.isPaused) {
                                                viewModel.pauseTimer()
                                            } else {
                                                viewModel.startOrResumeTimer(context)
                                            }
                                        },
                                        onStopActiveSession = {
                                            viewModel.completeCurrentFocusSession(context, fullCompletion = false)
                                        },
                                        onNavigateToTimer = {
                                            currentTab = MainDestination.FOCUS_SHIELD
                                        },
                                        onToggleRoutineComplete = { task ->
                                            viewModel.toggleRoutineTaskCompleted(task)
                                        },
                                        onDeleteRoutine = { id ->
                                            viewModel.deleteRoutineTask(id)
                                        },
                                        onApplyPresetPack = { pack ->
                                            viewModel.loadPresetRoutinePack(context, pack)
                                        },
                                        onAddCustomRoutine = { title, sub, period, blockType, start, dur, notify, autoStartNow ->
                                            viewModel.addCustomRoutineTask(
                                                context = context,
                                                title = title,
                                                subject = sub,
                                                period = period,
                                                blockType = blockType,
                                                startTime = start,
                                                durationMinutes = dur,
                                                examTag = profile.examTarget,
                                                notifyOnStart = notify,
                                                autoStartNow = autoStartNow
                                            )
                                        },
                                        onSaveProfileAndBlocker = { newName, strict, notify ->
                                            viewModel.selectExamAndInitialize(
                                                context = context,
                                                displayName = newName,
                                                examCategory = profile.examCategory,
                                                examTarget = profile.examTarget,
                                                dailyGoalMinutes = profile.dailyGoalMinutes,
                                                applyStarterPreset = false
                                            )
                                            viewModel.updateBlockerConfiguration(strict, notify, profile.blockedPackages)
                                        },
                                        onSignOut = {
                                            signOut(
                                                context = context,
                                                credentialManager = credentialManager,
                                                onSignOutComplete = {},
                                                scope = scope
                                            )
                                        },
                                        onNotifyTriggered = { bannerMsg ->
                                            viewModel.postBanner(bannerMsg)
                                        },
                                        onClaimMilestoneOrStreakCheckIn = { badgeId ->
                                            viewModel.claimMilestoneOrDailyStreakCheckIn(badgeId)
                                        },
                                        onStartBadgeChallengeFocus = { badge ->
                                            val firstSub = com.example.data.ExamCatalog.getTrack(profile.examTarget).subjects.firstOrNull() ?: "Physics"
                                            viewModel.configureQuickTimer(
                                                title = "${badge.title} Challenge Block",
                                                subject = firstSub,
                                                period = badge.recommendedPeriod,
                                                durationMinutes = badge.recommendedMinutes
                                            )
                                            viewModel.startOrResumeTimer(context)
                                            viewModel.postBanner("Started ${badge.recommendedMinutes}m '${badge.title}' Focus Session!")
                                        },
                                        onNavigateToRewards = {
                                            currentTab = MainDestination.REWARDS
                                        },
                                        onTriggerBlockInterceptorPreview = { appName ->
                                            viewModel.triggerAppBlockPreview(context, appName)
                                        },
                                        onCheckAutoStartRoutine = {
                                            viewModel.checkAndAutoStartScheduledRoutine(context)
                                        }
                                    )
                                }
                                MainDestination.FOCUS_SHIELD -> {
                                    FocusTimerAndShieldScreen(
                                        profile = profile,
                                        timerState = timerState,
                                        onStartOrResumeTimer = { viewModel.startOrResumeTimer(context) },
                                        onPauseTimer = { viewModel.pauseTimer() },
                                        onCompleteSession = { viewModel.completeCurrentFocusSession(context, fullCompletion = true) },
                                        onResetTimer = { viewModel.resetTimer() },
                                        onConfigureQuickTimer = { title, sub, period, mins ->
                                            viewModel.configureQuickTimer(title, sub, period, mins)
                                        },
                                        onSelectAmbientSound = { sound ->
                                            viewModel.setAmbientSound(sound)
                                        },
                                        onUpdateBlockerConfig = { strict, notify, pkgs ->
                                            viewModel.updateBlockerConfiguration(strict, notify, pkgs)
                                        },
                                        onTriggerBlockInterceptorPreview = { appName ->
                                            viewModel.triggerAppBlockPreview(context, appName)
                                        },
                                        onDismissBlockInterceptorPreview = {
                                            viewModel.dismissAppBlockPreview()
                                        }
                                    )
                                }
                                MainDestination.ANALYTICS -> {
                                    AnalyticsProgressScreen(
                                        profile = profile,
                                        routines = routines,
                                        sessions = sessions
                                    )
                                }
                                MainDestination.REWARDS -> {
                                    GamificationRewardsScreen(
                                        profile = profile,
                                        onSignOutClick = {
                                            signOut(
                                                context = context,
                                                credentialManager = credentialManager,
                                                onSignOutComplete = {},
                                                scope = scope
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        if (timerState.showAppBlockInterceptorPreview && currentTab != MainDestination.FOCUS_SHIELD) {
                            RegainAppBlockerInterceptorDialog(
                                interceptedAppName = timerState.lastInterceptedAppName,
                                activeStudyTitle = timerState.sessionTitle,
                                activeExamTarget = profile.examTarget,
                                onReturnToStudy = { viewModel.dismissAppBlockPreview() }
                            )
                        }
                    }
                }
            }
        }
    }
}
