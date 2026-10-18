package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.RoutineTask
import com.example.ui.theme.AppThemeOption
import com.example.ui.theme.ComeBackThemeManager
import com.example.util.DeviceFocusHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify app name is Comeback Daily Routine and permissions and blocker work`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Comeback : Daily Routine", appName)

        // Verify Android Settings permissions grant & status check
        DeviceFocusHelper.grantAllPermissionsViaSettings(context)
        val perms = DeviceFocusHelper.checkPermissions(context)
        assertTrue(perms.allCoreGranted)
        assertEquals(5, perms.grantedCount)

        // Verify Custom Blocked App addition & discovery for routine app blocking
        DeviceFocusHelper.addCustomAppToCatalog(context, "Free Fire Max", "com.dts.freefiremax")
        val apps = DeviceFocusHelper.getDiscoverableAppsForBlocker(context)
        assertTrue(apps.any { it.packageName == "com.dts.freefiremax" && it.appName == "Free Fire Max" })

        // Verify Auto-Start Routine Timer & Short-Form Shield preferences
        DeviceFocusHelper.setAutoStartRoutineTimerEnabled(context, true)
        assertTrue(DeviceFocusHelper.isAutoStartRoutineTimerEnabled(context))
        DeviceFocusHelper.setShortFormShieldEnabled(context, true)
        assertTrue(DeviceFocusHelper.isShortFormShieldEnabled(context))

        // Verify Multi-Theme switching (Dark, Light, Black & White, Sapphire)
        ComeBackThemeManager.setTheme(context, AppThemeOption.MONOCHROME_DARK)
        assertEquals(AppThemeOption.MONOCHROME_DARK, ComeBackThemeManager.currentOption)
        ComeBackThemeManager.setTheme(context, AppThemeOption.COMEBACK_DARK)
        assertEquals(AppThemeOption.COMEBACK_DARK, ComeBackThemeManager.currentOption)

        // Verify Routine Notification channels & auto-start notification builder
        val sampleRoutine = RoutineTask(
            id = "custom_1",
            title = "Morning Physics Formula Sprint",
            subject = "Physics",
            period = "MORNING",
            blockType = "DAILY_ROUTINE",
            startTime = "06:30",
            durationMinutes = 45L,
            notifyOnStart = true
        )
        DeviceFocusHelper.ensureNotificationChannels(context)
        DeviceFocusHelper.sendRoutineReminderNotification(context, sampleRoutine)
        DeviceFocusHelper.sendAutoStartedRoutineNotification(context, sampleRoutine, 6)
    }
}
