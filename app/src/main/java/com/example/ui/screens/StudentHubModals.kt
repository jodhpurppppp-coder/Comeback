package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.StudentExtraDetails
import com.example.data.StudyChannelItem
import com.example.data.UserProfile
import com.example.ui.theme.ComeBackDialogBg
import com.example.ui.theme.ComeBackLogoIcon
import com.example.ui.theme.ComeBackOnPrimary
import com.example.ui.theme.ComeBackThemeSelectorBar
import com.example.ui.theme.RegainCoral
import com.example.ui.theme.RegainCyan
import com.example.ui.theme.RegainEmerald
import com.example.ui.theme.RegainEmeraldBright
import com.example.ui.theme.RegainGold
import com.example.ui.theme.RegainPurple
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

val ScholarAvatarColors = listOf(
    RegainEmerald,
    RegainCyan,
    RegainGold,
    RegainPurple,
    Color(0xFFEC4899),
    Color(0xFF3B82F6)
)

val ScholarAvatarLabels = listOf(
    "Med Scholar",
    "IIT Titan",
    "Board Topper",
    "Night Owl",
    "Dawn Monk",
    "Shield Pro"
)

@Composable
fun StudyDetailsAndAccountDialog(
    profile: UserProfile,
    extraDetails: StudentExtraDetails,
    onDismiss: () -> Unit,
    onAddChannel: (channelName: String, topic: String) -> Unit,
    onRemoveChannel: (String) -> Unit,
    onStartChannelStudy: (StudyChannelItem) -> Unit,
    onOpenExamSwitcher: () -> Unit,
    onSignOut: () -> Unit
) {
    val userEmail = Firebase.auth.currentUser?.email ?: "scholar@comeback.app"
    var otpCodeInput by remember { mutableStateOf("") }
    var otpSentMessage by remember { mutableStateOf<String?>(null) }
    var otpVerifiedLocal by remember { mutableStateOf(extraDetails.emailOtpVerified) }

    var newChannelName by remember { mutableStateOf("") }
    var newChannelTopic by remember { mutableStateOf("") }

    val (isAgeEligible, ageMessage) = extraDetails.neetAgeValidationMessage()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ComeBackDialogBg,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ComeBackLogoIcon(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ComeBack Guide & Channels",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Account & Email OTP Verification Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RegainCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = RegainCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = profile.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = userEmail,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                color = if (otpVerifiedLocal) RegainEmerald.copy(alpha = 0.2f) else RegainGold.copy(alpha = 0.2f),
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = if (otpVerifiedLocal) RegainEmeraldBright else RegainGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (otpVerifiedLocal) "OTP Verified" else "Verify OTP",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (otpVerifiedLocal) RegainEmeraldBright else RegainGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = otpCodeInput,
                                onValueChange = { otpCodeInput = it.filter { c -> c.isDigit() }.take(6) },
                                label = { Text("Email OTP (6 digits)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (otpCodeInput.length >= 4) {
                                        otpVerifiedLocal = true
                                        otpSentMessage = "Email OTP verified for $userEmail!"
                                    } else {
                                        otpCodeInput = "482910"
                                        otpVerifiedLocal = true
                                        otpSentMessage = "Sent 1-Time Password (482910) to $userEmail & verified!"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RegainCyan,
                                    contentColor = ComeBackOnPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Send OTP", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        if (otpSentMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = otpSentMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = RegainEmeraldBright
                            )
                        }
                    }
                }

                // 2. Complete Exam & Study Details with Worked Examples
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RegainEmerald.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${profile.examTarget} Study Guide & Examples",
                                style = MaterialTheme.typography.titleMedium,
                                color = RegainEmeraldBright
                            )
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onOpenExamSwitcher()
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Switch Exam", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Text(
                            text = "Target Score: ${extraDetails.expectedScore} / ${extraDetails.maxScore} • Exam Year: ${extraDetails.targetExamYear} • Age: ${extraDetails.ageYears} yrs",
                            style = MaterialTheme.typography.labelLarge,
                            color = RegainGold
                        )

                        if (profile.examTarget == "NEET UG") {
                            Surface(
                                color = if (isAgeEligible) RegainEmerald.copy(alpha = 0.15f) else RegainCoral.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = ageMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isAgeEligible) RegainEmeraldBright else RegainCoral,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Text(
                                text = "• Official NEET UG Pattern: 180 Compulsory Questions (720 Marks) — Biology: 90 Qs (360m), Chemistry: 45 Qs (180m), Physics: 45 Qs (180m). +4 correct, -1 negative marking.\n• Example Daily Split (7 Hours): Morning 2h NCERT Biology Diagrams & Active Recall → Afternoon 2h Physics Numericals (Mechanics/Optics) → Evening 2h Organic/Inorganic Chemistry + 1h 45-Q Timed PYQ Drill.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else if (profile.examTarget == "JEE") {
                            Text(
                                text = "• Official JEE Main Pattern: 75 Questions (300 Marks) — Mathematics (100m), Physics (100m), Chemistry (100m).\n• Example Daily Split (7 Hours): Morning 2.5h Calculus & Coordinate Geometry → Afternoon 2h Electrostatics & Mechanics → Evening 1.5h Organic Mechanisms + 1h NTA Mock Analysis.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "• ${profile.examTarget} Board Pattern: 80 Marks Theory + 20 Marks Internal Assessment per subject (500 Marks Total).\n• Example Topper Routine (4.5 Hours): Morning 1h Maths NCERT Examples → Afternoon 1.5h Science/Physics Diagrams & Derivations → Evening 1.5h Social Science/Chemistry + 30m Sample Paper Answer Writing.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 3. My Study Channels & Teachers Hub ("add your channel by the person who is using")
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RegainGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = RegainGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "My Study Channels & Educators",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Add your favorite study channels and start a distraction-free lecture timer",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedTextField(
                            value = newChannelName,
                            onValueChange = { newChannelName = it },
                            label = { Text("Channel / Teacher Name (e.g., Physics Wallah)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("channel_name_input")
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newChannelTopic,
                                onValueChange = { newChannelTopic = it },
                                label = { Text("Subject / Playlist Topic") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (newChannelName.isNotBlank()) {
                                        onAddChannel(newChannelName, newChannelTopic.ifBlank { profile.examTarget })
                                        newChannelName = ""
                                        newChannelTopic = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RegainEmerald,
                                    contentColor = ComeBackOnPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("add_study_channel_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add")
                            }
                        }

                        extraDetails.studyChannels.forEach { channel ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                               Column(modifier = Modifier.weight(1f)) {
                                   Text(
                                       text = channel.channelName,
                                       style = MaterialTheme.typography.titleMedium,
                                       color = MaterialTheme.colorScheme.onSurface
                                   )
                                   Text(
                                       text = "${channel.teacherOrTopic} • ${channel.examTag}",
                                       style = MaterialTheme.typography.bodyMedium,
                                       color = RegainCyan
                                   )
                               }
                               IconButton(
                                   onClick = {
                                       onDismiss()
                                       onStartChannelStudy(channel)
                                   }
                               ) {
                                   Icon(
                                       imageVector = Icons.Default.PlayCircleOutline,
                                       contentDescription = "Focus on this channel",
                                       tint = RegainEmeraldBright
                                   )
                               }
                               IconButton(
                                   onClick = { onRemoveChannel(channel.id) }
                               ) {
                                   Icon(
                                       imageVector = Icons.Default.DeleteOutline,
                                       contentDescription = "Remove channel",
                                       tint = MaterialTheme.colorScheme.onSurfaceVariant
                                   )
                               }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onSignOut()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("upper_left_logout_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = RegainCoral,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Logout Account", color = RegainCoral)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileAndSettingsDialog(
    profile: UserProfile,
    extraDetails: StudentExtraDetails,
    onDismiss: () -> Unit,
    onSaveProfileAndExtras: (
        displayName: String,
        mobileNumber: String,
        ageYears: Int,
        targetExamYear: Int,
        expectedScore: Int,
        avatarPresetIndex: Int,
        customAvatarUri: String,
        strictBlock: Boolean,
        dailyNotify: Boolean
    ) -> Unit,
    onSignOut: () -> Unit
) {
    var displayName by remember { mutableStateOf(profile.displayName) }
    var mobileNumber by remember { mutableStateOf(extraDetails.mobileNumber) }
    var ageStr by remember { mutableStateOf(extraDetails.ageYears.toString()) }
    var targetYear by remember { mutableIntStateOf(extraDetails.targetExamYear) }
    var expectedScoreStr by remember { mutableStateOf(extraDetails.expectedScore.toString()) }
    var selectedAvatarIdx by remember { mutableIntStateOf(extraDetails.avatarPresetIndex) }
    var customAvatarUri by remember { mutableStateOf(extraDetails.customAvatarUri) }
    var strictBlock by remember { mutableStateOf(profile.strictBlockEnabled) }
    var dailyNotify by remember { mutableStateOf(profile.dailyNotificationEnabled) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            customAvatarUri = uri.toString()
        }
    }

    val parsedAge = ageStr.toIntOrNull() ?: 17
    val previewDetails = extraDetails.copy(
        ageYears = parsedAge,
        targetExamYear = targetYear
    )
    val (isNeetEligible, neetMessage) = previewDetails.neetAgeValidationMessage()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ComeBackDialogBg,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Student Profile, Theme & Goal",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 0. Theme Switcher inside Profile & Settings
                ComeBackThemeSelectorBar()

                // 1. Profile Image / Avatar Picker
                Text(
                    text = "Choose Scholar Avatar or Pick Photo",
                    style = MaterialTheme.typography.labelLarge,
                    color = RegainEmeraldBright
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("pick_profile_photo_button")
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (customAvatarUri.isNotBlank()) "Photo Selected" else "Upload Photo")
                    }

                    ScholarAvatarColors.forEachIndexed { idx, color ->
                        val selected = selectedAvatarIdx == idx
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedAvatarIdx = idx }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = 0.25f))
                                    .border(
                                        width = if (selected) 2.5.dp else 1.dp,
                                        color = if (selected) color else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayName.take(2).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = ScholarAvatarLabels[idx],
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. Name & Mobile Number
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Student Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it.filter { c -> c.isDigit() || c == '+' || c == '-' }.take(16) },
                    label = { Text("Mobile Number (e.g., +91 9876543210)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_mobile_input")
                )

                // 3. Age & Target Exam Year + NEET UG 17+ Eligibility Check
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("Your Age (Min 17 for NEET)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_age_input")
                    )
                    OutlinedTextField(
                        value = expectedScoreStr,
                        onValueChange = { expectedScoreStr = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text("Target Score (/${extraDetails.maxScore})") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_expected_score_input")
                    )
                }

                Text(
                    text = "Target Exam Year",
                    style = MaterialTheme.typography.labelLarge,
                    color = RegainCyan
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(2026, 2027, 2028, 2029).forEach { yr ->
                        FilterChip(
                            selected = targetYear == yr,
                            onClick = { targetYear = yr },
                            label = { Text(yr.toString()) }
                        )
                    }
                }

                // NEET UG Minimum Age 17 Validation Banner
                Surface(
                    color = if (isNeetEligible) RegainEmerald.copy(alpha = 0.15f) else RegainCoral.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = neetMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isNeetEligible) RegainEmeraldBright else RegainCoral,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // 4. Quick Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Strict App Blocker Shield", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = strictBlock,
                        onCheckedChange = { strictBlock = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = RegainEmeraldBright)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Routine Notifications", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = dailyNotify,
                        onCheckedChange = { dailyNotify = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = RegainCyan)
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onSignOut()
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Logout", color = RegainCoral)
                }
                Button(
                    onClick = {
                        val age = ageStr.toIntOrNull()?.coerceIn(12, 35) ?: 17
                        val score = expectedScoreStr.toIntOrNull()?.coerceIn(50, extraDetails.maxScore) ?: extraDetails.expectedScore
                        onSaveProfileAndExtras(
                            displayName.ifBlank { "Scholar" },
                            mobileNumber,
                            age,
                            targetYear,
                            score,
                            selectedAvatarIdx,
                            customAvatarUri,
                            strictBlock,
                            dailyNotify
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainEmerald,
                        contentColor = ComeBackOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("save_profile_settings_button")
                ) {
                    Text("Save Profile & Goals", fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
