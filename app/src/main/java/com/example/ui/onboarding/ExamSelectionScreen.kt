package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ExamCatalog
import com.example.data.ExamCategoryOption
import com.example.data.ExamSubTrackInfo
import com.example.data.StudentExtraDetails
import com.example.data.StudentExtrasStore
import com.example.ui.theme.ComeBackBackdropGradient
import com.example.ui.theme.ComeBackLogoIcon
import com.example.ui.theme.ComeBackOnPrimary
import com.example.ui.theme.ComeBackThemeSelectorBar
import com.example.ui.theme.RegainCoral
import com.example.ui.theme.RegainCyan
import com.example.ui.theme.RegainEmerald
import com.example.ui.theme.RegainEmeraldBright
import com.example.ui.theme.RegainGold
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamSelectionScreen(
    initialName: String,
    initialCategory: String = "UNSET",
    initialTarget: String = "Not Selected",
    isModalSwitcher: Boolean = false,
    onDismissModal: () -> Unit = {},
    onConfirmSelection: (
        displayName: String,
        examCategory: String,
        examTarget: String,
        dailyGoalMinutes: Long,
        applyStarterPreset: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    val uid = Firebase.auth.currentUser?.uid ?: "default_user"

    var displayName by remember(initialName) {
        mutableStateOf(initialName.ifBlank { "Scholar" })
    }
    var selectedCategory by remember(initialCategory) {
        mutableStateOf(
            if (initialCategory in listOf("BOARDS", "COMPETITIVE")) initialCategory else "BOARDS"
        )
    }
    var selectedTarget by remember(initialTarget) {
        mutableStateOf(
            if (initialTarget != "Not Selected" && initialTarget.isNotBlank()) initialTarget else "Class 10"
        )
    }

    val savedExtras = remember(selectedTarget) {
        StudentExtrasStore.load(context, uid, selectedTarget)
    }

    var mobileNumber by remember(savedExtras) { mutableStateOf(savedExtras.mobileNumber) }
    var ageInput by remember(savedExtras) { mutableStateOf(savedExtras.ageYears.toString()) }
    var targetExamYear by remember(savedExtras) { mutableIntStateOf(savedExtras.targetExamYear) }
    var expectedScoreInput by remember(selectedTarget) {
        val defaultScore = when (selectedTarget) {
            "NEET UG" -> "680"
            "JEE" -> "250"
            else -> "475"
        }
        mutableStateOf(defaultScore)
    }

    val currentTrackInfo = remember(selectedTarget) {
        ExamCatalog.getTrack(selectedTarget)
    }

    var dailyGoalMinutes by remember(selectedTarget) {
        mutableFloatStateOf(currentTrackInfo.recommendedDailyMinutes.toFloat())
    }
    var seedPresetPack by remember { mutableStateOf(true) }

    val parsedAge = ageInput.toIntOrNull() ?: 17
    val maxMarks = when (selectedTarget) {
        "NEET UG" -> 720
        "JEE" -> 300
        else -> 500
    }
    val (isNeetEligible, neetEligibilityText) = remember(parsedAge, targetExamYear) {
        StudentExtraDetails(ageYears = parsedAge, targetExamYear = targetExamYear).neetAgeValidationMessage()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = ComeBackBackdropGradient)
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 580.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Top bar with ComeBack Logo + Branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ComeBackLogoIcon(size = 42.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Comeback : Daily Routine",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "STEP 1 • PERSONALIZE YOUR EXAM ROUTINE",
                            style = MaterialTheme.typography.labelMedium,
                            color = RegainEmeraldBright
                        )
                    }
                }

                if (isModalSwitcher) {
                    IconButton(
                        onClick = onDismissModal,
                        modifier = Modifier.testTag("close_exam_switcher_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close exam selector",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ComeBackThemeSelectorBar()

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Which exam are you preparing for?",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Choose Boards (Classes 9, 10, 11, 12) or Competitive Exams (NEET UG & JEE). Set your expected marks, exam year & age eligibility.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Student Name & Mobile Number Row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("aspirant_name_input")
                )
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it.filter { c -> c.isDigit() || c == '+' }.take(15) },
                    label = { Text("Mobile (Optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "1. SELECT EXAM CATEGORY",
                style = MaterialTheme.typography.labelMedium,
                color = RegainCyan
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Two Main Category Cards: 1. Boards  2. Competitive Exam
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExamCategoryOption.entries.forEach { option ->
                    val isSelected = selectedCategory == option.key
                    val accentColor = if (option == ExamCategoryOption.BOARDS) RegainCyan else RegainEmerald
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                selectedCategory = option.key
                                val defaultSub = if (option.key == "BOARDS") "Class 10" else "NEET UG"
                                selectedTarget = defaultSub
                                dailyGoalMinutes = ExamCatalog.getTrack(defaultSub).recommendedDailyMinutes.toFloat()
                            }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .testTag("exam_category_${option.key.lowercase()}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                accentColor.copy(alpha = 0.14f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(accentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (option == ExamCategoryOption.BOARDS) {
                                            Icons.AutoMirrored.Filled.MenuBook
                                        } else {
                                            Icons.Default.EmojiEvents
                                        },
                                        contentDescription = null,
                                        tint = accentColor
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = accentColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.badgeText,
                                style = MaterialTheme.typography.labelMedium,
                                color = accentColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Sub-tracks: Boards -> Class 9, 10, 11, 12 | Competitive -> NEET UG, JEE
            val subTracks = ExamCatalog.getTracksForCategory(selectedCategory)
            Text(
                text = if (selectedCategory == "BOARDS") {
                    "2. CHOOSE YOUR BOARD CLASS (CLASS 9, 10, 11 OR 12)"
                } else {
                    "2. CHOOSE YOUR COMPETITIVE EXAM (NEET UG OR JEE)"
                },
                style = MaterialTheme.typography.labelMedium,
                color = RegainEmeraldBright
            )
            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    subTracks.forEach { track ->
                        SubTrackCard(
                            track = track,
                            isSelected = selectedTarget == track.targetName,
                            onClick = {
                                selectedTarget = track.targetName
                                dailyGoalMinutes = track.recommendedDailyMinutes.toFloat()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Expected Score, Exam Year & NEET UG Minimum Age 17 Check
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RegainGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "3. YOUR $selectedTarget TARGET SCORE, EXAM YEAR & AGE",
                        style = MaterialTheme.typography.labelMedium,
                        color = RegainGold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = expectedScoreInput,
                            onValueChange = { expectedScoreInput = it.filter { c -> c.isDigit() }.take(3) },
                            label = { Text("Expected Score (/ $maxMarks)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("onboarding_expected_score_input")
                        )
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it.filter { c -> c.isDigit() }.take(2) },
                            label = { Text("Your Age (Min 17 for NEET)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("onboarding_age_input")
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Exam Year:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        listOf(2026, 2027, 2028, 2029).forEach { yr ->
                            FilterChip(
                                selected = targetExamYear == yr,
                                onClick = { targetExamYear = yr },
                                label = { Text(yr.toString()) }
                            )
                        }
                    }

                    if (selectedTarget == "NEET UG") {
                        Surface(
                            color = if (isNeetEligible) RegainEmerald.copy(alpha = 0.16f) else RegainCoral.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = neetEligibilityText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isNeetEligible) RegainEmeraldBright else RegainCoral,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Selected Track Details & Daily Goal Slider
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentTrackInfo.headline,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Subjects included in your preset routine:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = RegainGold.copy(alpha = 0.18f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${(dailyGoalMinutes.roundToInt() / 60)}h ${(dailyGoalMinutes.roundToInt() % 60)}m / day",
                                style = MaterialTheme.typography.labelLarge,
                                color = RegainGold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentTrackInfo.subjects.forEach { subject ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.border(
                                    1.dp,
                                    RegainEmerald.copy(alpha = 0.35f),
                                    RoundedCornerShape(10.dp)
                                )
                            ) {
                                Text(
                                    text = subject,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = RegainEmeraldBright,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Daily Focus Goal: ${dailyGoalMinutes.roundToInt()} minutes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = dailyGoalMinutes,
                        onValueChange = { dailyGoalMinutes = it },
                        valueRange = 60f..600f,
                        steps = 17,
                        colors = SliderDefaults.colors(
                            thumbColor = RegainEmeraldBright,
                            activeTrackColor = RegainEmerald
                        ),
                        modifier = Modifier.testTag("daily_goal_slider")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { seedPresetPack = !seedPresetPack }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = seedPresetPack,
                            onCheckedChange = { seedPresetPack = it },
                            colors = CheckboxDefaults.colors(checkedColor = RegainEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Load Comeback : Daily Routine's Morning, Afternoon & Evening Preset for ${currentTrackInfo.targetName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    val cleanExpected = expectedScoreInput.toIntOrNull()?.coerceIn(50, maxMarks) ?: (maxMarks * 9 / 10)
                    StudentExtrasStore.saveProfileExtras(
                        context = context,
                        userId = uid,
                        examTarget = selectedTarget,
                        mobileNumber = mobileNumber,
                        ageYears = parsedAge.coerceIn(12, 35),
                        targetExamYear = targetExamYear,
                        expectedScore = cleanExpected,
                        avatarPresetIndex = savedExtras.avatarPresetIndex,
                        customAvatarUri = savedExtras.customAvatarUri
                    )
                    onConfirmSelection(
                        displayName.ifBlank { "Scholar" },
                        selectedCategory,
                        selectedTarget,
                        dailyGoalMinutes.roundToInt().toLong(),
                        seedPresetPack
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainEmerald,
                    contentColor = ComeBackOnPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("confirm_exam_selection_button")
            ) {
                Text(
                    text = "Launch My $selectedTarget Comeback Routine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SubTrackCard(
    track: ExamSubTrackInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accent = if (track.categoryKey == "COMPETITIVE") RegainEmeraldBright else RegainCyan
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("exam_subtrack_${track.targetName.replace(" ", "_")}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                accent.copy(alpha = 0.13f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (track.categoryKey == "COMPETITIVE") {
                        Icons.Default.Science
                    } else {
                        Icons.AutoMirrored.Filled.MenuBook
                    },
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = track.targetName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = accent.copy(alpha = 0.18f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = track.highlightTag,
                            style = MaterialTheme.typography.labelMedium,
                            color = accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = track.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = accent,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
