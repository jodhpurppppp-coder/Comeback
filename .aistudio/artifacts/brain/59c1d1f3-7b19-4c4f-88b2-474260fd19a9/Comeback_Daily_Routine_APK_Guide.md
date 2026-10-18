# Comeback : Daily Routine — Debug APK & Project Guide

## 1. How to Download `app-debug.apk` (25 MB)

Your compiled Android installation package (**`app-debug.apk`**) has been built and verified. You can download it using any of the following methods in Google AI Studio:

1. **Via the AI Studio Settings / Export Menu (Recommended for 1-Click APK Download):**
   - Click the **Settings (Gear icon)** or **Export / Download** button in the top-right toolbar of Google AI Studio.
   - Select **Download APK** (or **Export Project as ZIP**), which serves the compiled `app-debug.apk` binary directly to your device.

2. **Via the Code / File Explorer Tab:**
   - Open the **Code** tab in AI Studio.
   - Locate **`app-debug.apk`** in the root directory (`/app-debug.apk`), inside **`/Artifacts/app-debug.apk`**, or inside **`/.build-outputs/app-debug.apk`**.
   - Right-click or use the file action menu to download it, or export the workspace ZIP to get both the full Android Studio source code and the pre-compiled `app-debug.apk`.

---

## 2. Build & Verification Details

- **App Name:** `Comeback : Daily Routine`
- **Application ID:** `com.aistudio.regainstudy.kxmprq`
- **APK File Size:** `25,799,724 bytes` (~25.8 MB)
- **Signing Config:** Android Debug Keystore (`debug.keystore` — ready to install on any Android device with *Install unknown apps* enabled)
- **Locations in Workspace:**
  - `/app-debug.apk` (Project root)
  - `/Artifacts/app-debug.apk` (`Artifacts` folder in code explorer)
  - `/.build-outputs/app-debug.apk` (Platform build output directory)
  - `/app/build/outputs/apk/debug/app-debug.apk` (Standard Gradle debug output)

---

## 3. Complete Feature & Source File Manifest

- **`app/src/main/java/com/example/MainActivity.kt`** — App entry point, authentication gate (Quick Student Login + Google Sign-In), Exam Selection onboarding (`Boards: Class 9, 10, 11, 12` & `Competitive: NEET UG, JEE`), navigation shell, Profile & Exam Goal drawer, and global App Blocker Shield interceptor.
- **`app/src/main/java/com/example/ui/RegainViewModel.kt`** — Real-time Firestore + local persistence state management, automatic routine clock watcher (`checkAndAutoStartScheduledRoutine`), countdown focus timer with XP & streak awards, and live foreground app blocker monitor.
- **`app/src/main/java/com/example/ui/screens/DashboardAndRoutineScreen.kt`** — Daily Focus Dashboard, Next Upcoming Task spotlight, Live App Blocker status strip, Custom Daily Routine Builder (`Study`, `Habit`, `Break`) with **Auto-Start Timer** & **Notification Alert** toggles, and continuous daily schedule timeline (`Morning`, `Afternoon`, `Evening`).
- **`app/src/main/java/com/example/ui/screens/FocusTimerAndShieldScreen.kt`** — Deep Focus Timer, Custom App Blocker Shield list (Instagram, YouTube Shorts, Games, Social + custom package addition), Short-Form Video Shield (`Reels & Shorts`), and direct Android Settings permission launchers (`Usage Access`, `Display Over Other Apps`, `Notification Policy`, `DND`, `Battery`).
- **`app/src/main/java/com/example/ui/screens/GamificationScreen.kt`** — Study Streak tracker, XP levels, and Milestone Badges (`Early Bird`, `Deep Work Master`, `Perfect Week`, `100 Hours Studied`).
- **`app/src/main/java/com/example/ui/screens/AnalyticsProgressScreen.kt`** — Subject-wise study distribution, Daily/Weekly/Monthly study duration charts, Planned vs. Actual comparison, and Peak Productivity insights.
- **`app/src/main/java/com/example/ui/theme/Theme.kt`** — 5 custom themes matching the Comeback logo (`ComeBack Logo Dark`, `ComeBack Logo Light`, `Pure Black & White Monochrome`, `AMOLED Pitch Black`, `Daylight Paper`).
- **`app/src/main/java/com/example/util/DeviceFocusHelper.kt`** — Real Android `UsageStatsManager`, `Settings.canDrawOverlays`, `NotificationManager`, `PowerManager`, foreground app detection, and routine alert notifications.
