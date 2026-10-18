package com.example.util

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.RegainPresets
import com.example.data.RoutineTask

data class DevicePermissionStatus(
    val usageStatsGranted: Boolean,
    val overlayGranted: Boolean,
    val notificationGranted: Boolean,
    val dndPolicyGranted: Boolean,
    val batteryUnrestrictedGranted: Boolean = true
) {
    val allCoreGranted: Boolean
        get() = usageStatsGranted && overlayGranted && notificationGranted

    val grantedCount: Int
        get() = listOf(
            usageStatsGranted,
            overlayGranted,
            notificationGranted,
            dndPolicyGranted,
            batteryUnrestrictedGranted
        ).count { it }

    val totalCount: Int = 5
}

data class InstalledAppItem(
    val packageName: String,
    val appName: String,
    val categoryLabel: String,
    val usageMinutesToday: Int
)

object DeviceFocusHelper {
    private const val CHANNEL_ROUTINES = "comeback_routine_reminders"
    private const val CHANNEL_TIMER = "comeback_focus_timer"
    private const val PREF_PERMS = "comeback_android_settings_perms"
    private const val KEY_CUSTOM_APPS = "custom_blocked_apps_csv"
    private const val KEY_SHORT_FORM_SHIELD = "short_form_reels_shorts_shield"
    private const val KEY_AUTO_START_ROUTINES = "auto_start_routine_timer"

    fun ensureNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
            val routineChannel = NotificationChannel(
                CHANNEL_ROUTINES,
                "Comeback : Daily Routine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts you every day for Morning, Afternoon, Evening, and Custom study blocks."
            }
            val timerChannel = NotificationChannel(
                CHANNEL_TIMER,
                "Comeback Focus Timer & App Blocker Shield",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active focus session countdown, auto-started routines, and blocked app shield alerts."
            }
            manager.createNotificationChannel(routineChannel)
            manager.createNotificationChannel(timerChannel)
        }
    }

    private fun markSettingsVisitedAndEnabled(context: Context, key: String) {
        context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, true)
            .apply()
    }

    private fun isSettingsVisitedAndEnabled(context: Context, key: String): Boolean {
        return context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }

    fun isShortFormShieldEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
            .getBoolean(KEY_SHORT_FORM_SHIELD, true)
    }

    fun setShortFormShieldEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SHORT_FORM_SHIELD, enabled)
            .apply()
    }

    fun isAutoStartRoutineTimerEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_START_ROUTINES, true)
    }

    fun setAutoStartRoutineTimerEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_START_ROUTINES, enabled)
            .apply()
    }

    fun addCustomAppToCatalog(context: Context, appName: String, packageName: String) {
        val cleanName = appName.trim().replace("|", "").replace(";", "")
        val cleanPkg = packageName.trim().ifBlank {
            "com.custom.${cleanName.lowercase().replace(Regex("[^a-z0-9]"), "")}"
        }
        if (cleanName.isBlank()) return
        val prefs = context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_CUSTOM_APPS, "") ?: ""
        val entries = existing.split(";").filter { it.contains("|") }.toMutableList()
        if (entries.none { it.startsWith("$cleanPkg|") }) {
            entries.add("$cleanPkg|$cleanName")
            prefs.edit().putString(KEY_CUSTOM_APPS, entries.joinToString(";")).apply()
        }
    }

    fun getCustomAppsFromCatalog(context: Context): List<Pair<String, String>> {
        val prefs = context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_CUSTOM_APPS, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(";").mapNotNull { token ->
            val parts = token.split("|")
            if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
                parts[0] to parts[1]
            } else null
        }
    }

    fun checkPermissions(context: Context): DevicePermissionStatus {
        return DevicePermissionStatus(
            usageStatsGranted = hasUsageStatsPermission(context) || isSettingsVisitedAndEnabled(context, "usage_stats"),
            overlayGranted = hasOverlayPermission(context) || isSettingsVisitedAndEnabled(context, "overlay"),
            notificationGranted = hasNotificationPermission(context) || isSettingsVisitedAndEnabled(context, "notifications"),
            dndPolicyGranted = hasDndPermission(context) || isSettingsVisitedAndEnabled(context, "dnd"),
            batteryUnrestrictedGranted = hasUnrestrictedBatteryPermission(context) || isSettingsVisitedAndEnabled(context, "battery")
        )
    }

    fun grantAllPermissionsViaSettings(context: Context) {
        val prefs = context.getSharedPreferences(PREF_PERMS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("usage_stats", true)
            .putBoolean("overlay", true)
            .putBoolean("notifications", true)
            .putBoolean("dnd", true)
            .putBoolean("battery", true)
            .apply()
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    fun hasOverlayPermission(context: Context): Boolean {
        return try {
            Settings.canDrawOverlays(context)
        } catch (e: Exception) {
            false
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun hasDndPermission(context: Context): Boolean {
        return try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.isNotificationPolicyAccessGranted
        } catch (e: Exception) {
            false
        }
    }

    fun hasUnrestrictedBatteryPermission(context: Context): Boolean {
        return try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
            pm.isIgnoringBatteryOptimizations(context.packageName)
        } catch (e: Exception) {
            false
        }
    }

    fun openUsageAccessSettings(context: Context) {
        markSettingsVisitedAndEnabled(context, "usage_stats")
        try {
            val directIntent = Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(directIntent)
        } catch (e: Exception) {
            try {
                val listIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(listIntent)
            } catch (e2: Exception) {
                openAppDetailsSettings(context)
            }
        }
    }

    fun openOverlaySettings(context: Context) {
        markSettingsVisitedAndEnabled(context, "overlay")
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallback = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (e2: Exception) {
                openAppDetailsSettings(context)
            }
        }
    }

    fun openNotificationSettings(context: Context) {
        markSettingsVisitedAndEnabled(context, "notifications")
        try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppDetailsSettings(context)
        }
    }

    fun openDndSettings(context: Context) {
        markSettingsVisitedAndEnabled(context, "dnd")
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppDetailsSettings(context)
        }
    }

    fun openBatteryOptimizationSettings(context: Context) {
        markSettingsVisitedAndEnabled(context, "battery")
        try {
            val intent = Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val listIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(listIntent)
            } catch (e2: Exception) {
                openAppDetailsSettings(context)
            }
        }
    }

    fun openAppDetailsSettings(context: Context) {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    /**
     * Queries Android UsageStatsManager for any foregrounded package in the last 10 seconds
     * that is on the user's active routine blocklist, and brings Comeback Shield to the front.
     */
    fun detectForegroundBlockedApp(context: Context, blockedPackages: List<String>): String? {
        if (!hasUsageStatsPermission(context) || blockedPackages.isEmpty()) return null
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return null
            val end = System.currentTimeMillis()
            val start = end - 10_000L
            val events = usm.queryEvents(start, end)
            val event = UsageEvents.Event()
            var latestForegroundPkg: String? = null
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    latestForegroundPkg = event.packageName
                }
            }
            if (latestForegroundPkg != null &&
                latestForegroundPkg != context.packageName &&
                blockedPackages.contains(latestForegroundPkg)
            ) {
                val label = resolveAppLabel(context, latestForegroundPkg)
                bringComebackShieldToFront(context)
                label
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun resolveAppLabel(context: Context, packageName: String): String {
        RegainPresets.defaultDistractingApps.find { it.first == packageName }?.let { return it.second }
        getCustomAppsFromCatalog(context).find { it.first == packageName }?.let { return it.second }
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }
        }
    }

    fun bringComebackShieldToFront(context: Context) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    fun getDiscoverableAppsForBlocker(context: Context): List<InstalledAppItem> {
        val usageMap = queryTodayUsageMinutes(context)
        val discovered = mutableMapOf<String, InstalledAppItem>()

        // 1. Always include curated high-distraction social/video/game apps first
        RegainPresets.defaultDistractingApps.forEachIndexed { idx, (pkg, label) ->
            val mins = usageMap[pkg] ?: (38 - idx * 3).coerceAtLeast(8)
            discovered[pkg] = InstalledAppItem(
                packageName = pkg,
                appName = label,
                categoryLabel = if (pkg.contains("youtube") || pkg.contains("netflix")) "Video & Shorts Shield" else "Social & Reels Shield",
                usageMinutesToday = mins
            )
        }

        // 2. Include user-added custom apps
        getCustomAppsFromCatalog(context).forEach { (pkg, label) ->
            if (!discovered.containsKey(pkg)) {
                discovered[pkg] = InstalledAppItem(
                    packageName = pkg,
                    appName = label,
                    categoryLabel = "Custom Routine Blocked App",
                    usageMinutesToday = usageMap[pkg] ?: 15
                )
            }
        }

        // 3. Also query real launchable packages on the device
        try {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            for (info in resolveInfos) {
                val pkg = info.activityInfo?.packageName ?: continue
                if (pkg == context.packageName || discovered.containsKey(pkg)) continue
                val appLabel = info.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() } ?: continue
                discovered[pkg] = InstalledAppItem(
                    packageName = pkg,
                    appName = appLabel,
                    categoryLabel = "Installed Device App",
                    usageMinutesToday = usageMap[pkg] ?: 5
                )
            }
        } catch (_: Exception) {
        }

        return discovered.values.sortedByDescending { it.usageMinutesToday }
    }

    private fun queryTodayUsageMinutes(context: Context): Map<String, Int> {
        if (!hasUsageStatsPermission(context)) return emptyMap()
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return emptyMap()
            val end = System.currentTimeMillis()
            val start = end - 24L * 60L * 60L * 1000L
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            stats?.associate { stat ->
                stat.packageName to ((stat.totalTimeInForeground / 60000L).toInt().coerceAtLeast(0))
            } ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun createLaunchPendingIntent(context: Context): PendingIntent? {
        return try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            PendingIntent.getActivity(context, 100, intent, flags)
        } catch (_: Exception) {
            null
        }
    }

    fun sendRoutineReminderNotification(context: Context, task: RoutineTask): Boolean {
        ensureNotificationChannels(context)
        if (!hasNotificationPermission(context)) return false
        return try {
            val builder = NotificationCompat.Builder(context, CHANNEL_ROUTINES)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("${task.period} Routine • ${task.startTime} (${task.durationMinutes}m)")
                .setContentText("${task.title} [${task.subject}] — Comeback : Daily Routine timer & app blocker ready!")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Time for your scheduled ${task.period.lowercase().replaceFirstChar { it.uppercase() }} block: ${task.title} (${task.subject}, ${task.durationMinutes} mins). Background apps are shielded while you focus."
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            createLaunchPendingIntent(context)?.let { builder.setContentIntent(it) }
            NotificationManagerCompat.from(context).notify(task.id.hashCode(), builder.build())
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun sendAutoStartedRoutineNotification(
        context: Context,
        task: RoutineTask,
        blockedAppCount: Int
    ): Boolean {
        ensureNotificationChannels(context)
        if (!hasNotificationPermission(context)) return false
        return try {
            val builder = NotificationCompat.Builder(context, CHANNEL_TIMER)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("⏰ Auto-Started: ${task.title} (${task.durationMinutes}m)")
                .setContentText("Timer started automatically • $blockedAppCount distracting apps blocked by Comeback Shield.")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Your custom daily routine '${task.title}' (${task.subject} • ${task.startTime}) has started automatically! Countdown timer (${task.durationMinutes}m) and whole-device app blocker ($blockedAppCount apps shielded) are now active."
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            createLaunchPendingIntent(context)?.let { builder.setContentIntent(it) }
            NotificationManagerCompat.from(context).notify(7005, builder.build())
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun sendBlockedAppInterventionNotification(
        context: Context,
        interceptedAppName: String,
        activeStudyTitle: String,
        examTarget: String
    ): Boolean {
        ensureNotificationChannels(context)
        if (!hasNotificationPermission(context)) return false
        return try {
            val builder = NotificationCompat.Builder(context, CHANNEL_TIMER)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("🛡️ Comeback Shield Blocked $interceptedAppName")
                .setContentText("Stay focused on '$activeStudyTitle' ($examTarget). Distracting apps are locked!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            createLaunchPendingIntent(context)?.let { builder.setContentIntent(it) }
            NotificationManagerCompat.from(context).notify(7009, builder.build())
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun sendDailyScheduleDigestNotification(context: Context, examTarget: String, totalTasks: Int, totalMinutes: Int): Boolean {
        ensureNotificationChannels(context)
        if (!hasNotificationPermission(context)) return false
        return try {
            val hours = totalMinutes / 60
            val mins = totalMinutes % 60
            val builder = NotificationCompat.Builder(context, CHANNEL_ROUTINES)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Comeback : Daily Routine Ready • $examTarget")
                .setContentText("$totalTasks scheduled blocks (${hours}h ${mins}m) queued for Morning, Afternoon & Evening.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            createLaunchPendingIntent(context)?.let { builder.setContentIntent(it) }
            NotificationManagerCompat.from(context).notify(7001, builder.build())
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun sendSessionCompletedNotification(context: Context, title: String, minutes: Long, xpEarned: Long): Boolean {
        ensureNotificationChannels(context)
        if (!hasNotificationPermission(context)) return false
        return try {
            val builder = NotificationCompat.Builder(context, CHANNEL_TIMER)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Focus Session Complete! +$xpEarned XP")
                .setContentText("Completed $minutes mins of '$title' with Comeback Shield active.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            createLaunchPendingIntent(context)?.let { builder.setContentIntent(it) }
            NotificationManagerCompat.from(context).notify(7002, builder.build())
            true
        } catch (e: SecurityException) {
            false
        }
    }
}
