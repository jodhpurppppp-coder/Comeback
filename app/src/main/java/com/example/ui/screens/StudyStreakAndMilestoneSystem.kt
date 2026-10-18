package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.BadgeMilestone
import com.example.data.GamificationCatalog
import com.example.data.UserProfile
import com.example.ui.theme.ComeBackOnPrimary
import com.example.ui.theme.ComeBackTrackColor
import com.example.ui.theme.RegainCoral
import com.example.ui.theme.RegainCyan
import com.example.ui.theme.RegainEmerald
import com.example.ui.theme.RegainEmeraldBright
import com.example.ui.theme.RegainGold
import com.example.ui.theme.RegainPurple

private fun iconForBadge(badgeId: String): ImageVector {
    return when (badgeId) {
        "early_bird" -> Icons.Default.WbSunny
        "deep_work_master" -> Icons.Default.Bolt
        "streak_3", "perfect_week" -> Icons.Default.LocalFireDepartment
        "shield_commander" -> Icons.Default.Shield
        "first_focus" -> Icons.Default.Stars
        else -> Icons.Default.WorkspacePremium
    }
}

private fun accentForBadge(badgeId: String): Color {
    return when (badgeId) {
        "early_bird" -> RegainGold
        "deep_work_master" -> RegainCyan
        "streak_3", "perfect_week" -> RegainCoral
        "shield_commander" -> RegainPurple
        else -> RegainEmeraldBright
    }
}

@Composable
fun StudyStreakAndMilestoneDashboardHub(
    profile: UserProfile,
    plannedStudyMinutes: Int,
    completedStudyMinutes: Int,
    totalRoutineBlocks: Int,
    completedBlocks: Int,
    expectedScore: Int,
    maxScore: Int,
    targetYear: Int,
    onClaimMilestoneOrStreakCheckIn: (String?) -> Unit,
    onStartBadgeChallengeFocus: (BadgeMilestone) -> Unit,
    onNavigateToRewards: () -> Unit
) {
    val progress = (completedStudyMinutes.toFloat() / plannedStudyMinutes.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()
    val isDailyGoalMet = completedStudyMinutes >= plannedStudyMinutes || percentage >= 100
    val remainingForGoal = (plannedStudyMinutes - completedStudyMinutes).coerceAtLeast(0)

    var selectedBadgeForModal by remember { mutableStateOf<BadgeMilestone?>(null) }

    val unlockedBadgesCount = remember(profile) {
        GamificationCatalog.badges.count { it.checkUnlocked(profile) || profile.unlockedBadges.contains(it.id) }
    }

    val nextTargetBadge = remember(profile) {
        GamificationCatalog.badges.firstOrNull { !it.checkUnlocked(profile) && !profile.unlockedBadges.contains(it.id) }
            ?: GamificationCatalog.badges.first()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RegainGold.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
            .testTag("study_streak_milestone_hub_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Row: Study Streak Flame + Daily Goal Status + Check-In Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RegainGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Study Streak Flame",
                            tint = RegainGold,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${profile.currentStreak}-Day Study Streak",
                                style = MaterialTheme.typography.titleLarge,
                                color = RegainGold,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = RegainEmerald.copy(alpha = 0.18f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "Best: ${profile.longestStreak}d",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = RegainEmeraldBright,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${profile.perfectDaysCount} Perfect Goal Days • ${profile.xpPoints} XP • ${profile.regainCoins} Coins",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { onClaimMilestoneOrStreakCheckIn(null) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainGold,
                        contentColor = ComeBackOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("daily_streak_checkin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Goal Check-In",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. 7-Day Consecutive Study Goal Tracker Strip
            val weekLabels = listOf("Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7")
            val activeStreakDays = profile.currentStreak.toInt().coerceIn(1, 7)

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Consecutive Daily Study Goal Tracker",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isDailyGoalMet) {
                                "Goal Met Today!"
                            } else {
                                "${remainingForGoal}m to lock today's goal"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isDailyGoalMet) RegainEmeraldBright else RegainCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekLabels.forEachIndexed { idx, label ->
                            val dayCompleted = idx < activeStreakDays
                            val isCurrentDay = idx == (activeStreakDays - 1).coerceAtLeast(0)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                dayCompleted -> RegainGold.copy(alpha = 0.24f)
                                                else -> ComeBackTrackColor
                                            }
                                        )
                                        .border(
                                            width = if (isCurrentDay) 1.5.dp else 1.dp,
                                            color = when {
                                                isCurrentDay -> RegainEmeraldBright
                                                dayCompleted -> RegainGold
                                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (dayCompleted) {
                                            Icons.Default.LocalFireDepartment
                                        } else {
                                            Icons.Default.Schedule
                                        },
                                        contentDescription = label,
                                        tint = if (dayCompleted) RegainGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (dayCompleted) RegainGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 3. Today's Study Goal Adherence Ring + Next Milestone Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(76.dp),
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
                            text = "Goal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${completedStudyMinutes}m / ${plannedStudyMinutes}m Daily Goal ($completedBlocks/$totalRoutineBlocks blocks)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Target: $expectedScore/$maxScore in ${profile.examTarget} $targetYear",
                        style = MaterialTheme.typography.labelMedium,
                        color = RegainEmeraldBright
                    )
                    val nextProgress = nextTargetBadge.progressFraction(profile)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next Milestone: ${nextTargetBadge.title}",
                            style = MaterialTheme.typography.labelMedium,
                            color = RegainCyan
                        )
                        Text(
                            text = "+${nextTargetBadge.rewardXp} XP",
                            style = MaterialTheme.typography.labelMedium,
                            color = RegainGold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { nextProgress },
                        color = RegainCyan,
                        trackColor = ComeBackTrackColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                }
            }

            // 4. Digital Milestone Badges Showcase ('Early Bird', 'Deep Work Master', 'Perfect Week', etc.)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = RegainGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Digital Milestone Badges ($unlockedBadgesCount/${GamificationCatalog.badges.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                TextButton(
                    onClick = onNavigateToRewards,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("view_all_milestones_button")
                ) {
                    Text("All Rewards", color = RegainEmeraldBright, style = MaterialTheme.typography.labelLarge)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GamificationCatalog.badges.forEach { badge ->
                    val unlocked = badge.checkUnlocked(profile) || profile.unlockedBadges.contains(badge.id)
                    val badgeFraction = if (unlocked) 1f else badge.progressFraction(profile)
                    val badgeAccent = accentForBadge(badge.id)

                    Surface(
                        color = if (unlocked) {
                            badgeAccent.copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .width(168.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedBadgeForModal = badge }
                            .border(
                                width = if (unlocked) 1.5.dp else 1.dp,
                                color = if (unlocked) badgeAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("dashboard_badge_${badge.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (unlocked) badgeAccent.copy(alpha = 0.25f) else ComeBackTrackColor
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (unlocked) iconForBadge(badge.id) else Icons.Default.Lock,
                                        contentDescription = badge.title,
                                        tint = if (unlocked) badgeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Surface(
                                    color = if (unlocked) RegainEmerald.copy(alpha = 0.22f) else ComeBackTrackColor,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = if (unlocked) "EARNED" else "${(badgeFraction * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (unlocked) RegainEmeraldBright else badgeAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = badge.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = badge.progressText(profile),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (unlocked) badgeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            LinearProgressIndicator(
                                progress = { badgeFraction },
                                color = if (unlocked) RegainEmeraldBright else badgeAccent,
                                trackColor = ComeBackTrackColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Badge Milestone Detail & Challenge Modal
    selectedBadgeForModal?.let { badge ->
        val isUnlocked = badge.checkUnlocked(profile) || profile.unlockedBadges.contains(badge.id)
        val badgeAccent = accentForBadge(badge.id)
        val progress = if (isUnlocked) 1f else badge.progressFraction(profile)

        AlertDialog(
            onDismissRequest = { selectedBadgeForModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeAccent.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconForBadge(badge.id),
                            contentDescription = badge.title,
                            tint = badgeAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isUnlocked) "Digital Badge Unlocked" else "Milestone Challenge • ${badge.requirementLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isUnlocked) RegainEmeraldBright else badgeAccent
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = badge.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Milestone Progress",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = badge.progressText(profile),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = badgeAccent
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                color = badgeAccent,
                                trackColor = ComeBackTrackColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                text = "Milestone Reward: +${badge.rewardXp} Focus XP & +${badge.rewardCoins} ComeBack Coins",
                                style = MaterialTheme.typography.labelMedium,
                                color = RegainGold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val target = badge
                            selectedBadgeForModal = null
                            onStartBadgeChallengeFocus(target)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_badge_challenge_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start ${badge.recommendedMinutes}m '${badge.title}' Focus Block")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = badge.id
                        selectedBadgeForModal = null
                        onClaimMilestoneOrStreakCheckIn(id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    modifier = Modifier.testTag("claim_badge_milestone_button")
                ) {
                    Text(
                        text = if (isUnlocked) "Claimed • Boost +${badge.rewardXp} XP" else "Verify & Unlock Badge",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBadgeForModal = null }) {
                    Text("Close")
                }
            }
        )
    }
}
