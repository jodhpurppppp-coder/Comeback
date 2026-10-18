package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

private fun simpleIcon(name: String, block: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit): ImageVector {
    return materialIcon(name = name) {
        materialPath(pathBuilder = block)
    }
}

val Icons.Filled.AccessTime: ImageVector by lazy {
    simpleIcon("Filled.AccessTime") {
        moveTo(11.99f, 2.0f)
        curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
        curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
        reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
        close()
        moveTo(12.5f, 7.0f)
        horizontalLineTo(11.0f)
        verticalLineToRelative(6.0f)
        lineToRelative(5.25f, 3.15f)
        lineToRelative(0.75f, -1.23f)
        lineToRelative(-4.5f, -2.67f)
        close()
    }
}

val Icons.Filled.Analytics: ImageVector by lazy {
    simpleIcon("Filled.Analytics") {
        moveTo(19.0f, 3.0f)
        horizontalLineTo(5.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(5.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(9.0f, 17.0f)
        horizontalLineTo(7.0f)
        verticalLineToRelative(-5.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(5.0f)
        close()
        moveTo(13.0f, 17.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(3.0f)
        close()
        moveTo(13.0f, 12.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(17.0f, 17.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineTo(7.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(10.0f)
        close()
    }
}

val Icons.Filled.AppBlocking: ImageVector by lazy {
    simpleIcon("Filled.AppBlocking") {
        moveTo(18.0f, 8.0f)
        curveToRelative(-2.21f, 0.0f, -4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        reflectiveCurveToRelative(4.0f, -1.79f, 4.0f, -4.0f)
        reflectiveCurveToRelative(-1.79f, -4.0f, -4.0f, -4.0f)
        close()
        moveTo(15.5f, 12.0f)
        curveToRelative(0.0f, -1.38f, 1.12f, -2.5f, 2.5f, -2.5f)
        curveToRelative(0.42f, 0.0f, 0.8f, 0.11f, 1.15f, 0.29f)
        lineToRelative(-3.36f, 3.36f)
        curveToRelative(-0.18f, -0.35f, -0.29f, -0.73f, -0.29f, -1.15f)
        close()
        moveTo(18.0f, 14.5f)
        curveToRelative(-0.42f, 0.0f, -0.8f, -0.11f, -1.15f, -0.29f)
        lineToRelative(3.36f, -3.36f)
        curveToRelative(0.18f, 0.35f, 0.29f, 0.73f, 0.29f, 1.15f)
        curveToRelative(0.0f, 1.38f, -1.12f, 2.5f, -2.5f, 2.5f)
        close()
        moveTo(17.0f, 18.0f)
        horizontalLineTo(7.0f)
        verticalLineTo(6.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(1.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(3.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineTo(7.0f)
        curveTo(5.9f, 1.0f, 5.0f, 1.9f, 5.0f, 3.0f)
        verticalLineToRelative(18.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(10.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineToRelative(-4.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(1.0f)
        close()
    }
}

val Icons.Filled.AutoAwesome: ImageVector by lazy {
    simpleIcon("Filled.AutoAwesome") {
        moveTo(19.0f, 9.0f)
        lineToRelative(1.25f, -2.75f)
        lineTo(23.0f, 5.0f)
        lineToRelative(-2.75f, -1.25f)
        lineTo(19.0f, 1.0f)
        lineToRelative(-1.25f, 2.75f)
        lineTo(15.0f, 5.0f)
        lineToRelative(2.75f, 1.25f)
        lineTo(19.0f, 9.0f)
        close()
        moveTo(11.5f, 9.5f)
        lineTo(9.0f, 4.0f)
        lineTo(6.5f, 9.5f)
        lineTo(1.0f, 12.0f)
        lineToRelative(5.5f, 2.5f)
        lineTo(9.0f, 20.0f)
        lineToRelative(2.5f, -5.5f)
        lineTo(17.0f, 12.0f)
        lineToRelative(-5.5f, -2.5f)
        close()
    }
}

val Icons.Filled.AutoGraph: ImageVector by lazy {
    simpleIcon("Filled.AutoGraph") {
        moveTo(14.06f, 9.94f)
        lineTo(12.0f, 9.0f)
        lineToRelative(2.06f, -0.94f)
        lineTo(15.0f, 6.0f)
        lineToRelative(0.94f, 2.06f)
        lineTo(18.0f, 9.0f)
        lineToRelative(-2.06f, 0.94f)
        lineTo(15.0f, 12.0f)
        lineToRelative(-0.94f, -2.06f)
        close()
        moveTo(4.0f, 14.0f)
        lineToRelative(1.5f, -2.0f)
        lineToRelative(4.5f, 4.5f)
        lineToRelative(6.5f, -6.5f)
        lineTo(22.0f, 15.5f)
        verticalLineTo(18.0f)
        horizontalLineTo(4.0f)
        verticalLineTo(14.0f)
        close()
    }
}

val Icons.Filled.Bolt: ImageVector by lazy {
    simpleIcon("Filled.Bolt") {
        moveTo(11.0f, 21.0f)
        horizontalLineToRelative(-1.0f)
        lineToRelative(1.0f, -7.0f)
        horizontalLineTo(7.5f)
        curveToRelative(-0.58f, 0.0f, -0.57f, -0.32f, -0.38f, -0.66f)
        lineTo(13.0f, 3.0f)
        horizontalLineToRelative(1.0f)
        lineToRelative(-1.0f, 7.0f)
        horizontalLineToRelative(3.5f)
        curveToRelative(0.49f, 0.0f, 0.56f, 0.33f, 0.47f, 0.51f)
        lineTo(11.0f, 21.0f)
        close()
    }
}

val Icons.Filled.CameraAlt: ImageVector by lazy {
    simpleIcon("Filled.CameraAlt") {
        moveTo(12.0f, 12.0f)
        moveToRelative(-3.2f, 0.0f)
        arcToRelative(3.2f, 3.2f, 0.0f, true, true, 6.4f, 0.0f)
        arcToRelative(3.2f, 3.2f, 0.0f, true, true, -6.4f, 0.0f)
        close()
        moveTo(9.0f, 2.0f)
        lineTo(7.17f, 4.0f)
        horizontalLineTo(4.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(16.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(6.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        horizontalLineToRelative(-3.17f)
        lineTo(15.0f, 2.0f)
        horizontalLineTo(9.0f)
        close()
    }
}

val Icons.Filled.Contrast: ImageVector by lazy {
    simpleIcon("Filled.Contrast") {
        moveTo(12.0f, 22.0f)
        curveToRelative(5.52f, 0.0f, 10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        reflectiveCurveTo(2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        close()
        moveTo(13.0f, 4.07f)
        curveToRelative(3.94f, 0.49f, 7.0f, 3.85f, 7.0f, 7.93f)
        reflectiveCurveToRelative(-3.05f, 7.44f, -7.0f, 7.93f)
        verticalLineTo(4.07f)
        close()
    }
}

val Icons.Filled.DarkMode: ImageVector by lazy {
    simpleIcon("Filled.DarkMode") {
        moveTo(12.0f, 3.0f)
        curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
        reflectiveCurveToRelative(4.03f, 9.0f, 9.0f, 9.0f)
        reflectiveCurveToRelative(9.0f, -4.03f, 9.0f, -9.0f)
        curveToRelative(0.0f, -0.46f, -0.04f, -0.92f, -0.1f, -1.36f)
        curveToRelative(-0.98f, 1.37f, -2.58f, 2.26f, -4.4f, 2.26f)
        curveToRelative(-2.98f, 0.0f, -5.4f, -2.42f, -5.4f, -5.4f)
        curveToRelative(0.0f, -1.81f, 0.89f, -3.42f, 2.26f, -4.4f)
        curveTo(12.92f, 3.04f, 12.46f, 3.0f, 12.0f, 3.0f)
        close()
    }
}

val Icons.Filled.Dashboard: ImageVector by lazy {
    simpleIcon("Filled.Dashboard") {
        moveTo(3.0f, 13.0f)
        horizontalLineToRelative(8.0f)
        verticalLineTo(3.0f)
        horizontalLineTo(3.0f)
        verticalLineToRelative(10.0f)
        close()
        moveTo(3.0f, 21.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-6.0f)
        horizontalLineTo(3.0f)
        verticalLineToRelative(6.0f)
        close()
        moveTo(13.0f, 21.0f)
        horizontalLineToRelative(8.0f)
        verticalLineTo(11.0f)
        horizontalLineToRelative(-8.0f)
        verticalLineToRelative(10.0f)
        close()
        moveTo(13.0f, 3.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(8.0f)
        verticalLineTo(3.0f)
        horizontalLineToRelative(-8.0f)
        close()
    }
}

val Icons.Filled.DeleteOutline: ImageVector by lazy {
    simpleIcon("Filled.DeleteOutline") {
        moveTo(6.0f, 19.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(7.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(8.0f, 9.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(10.0f)
        horizontalLineTo(8.0f)
        verticalLineTo(9.0f)
        close()
        moveTo(15.5f, 4.0f)
        lineToRelative(-1.0f, -1.0f)
        horizontalLineToRelative(-5.0f)
        lineToRelative(-1.0f, 1.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(14.0f)
        verticalLineTo(4.0f)
        close()
    }
}

val Icons.Filled.DoNotDisturbOn: ImageVector by lazy {
    simpleIcon("Filled.DoNotDisturbOn") {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(17.0f, 13.0f)
        horizontalLineTo(7.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(2.0f)
        close()
    }
}

val Icons.Filled.EditCalendar: ImageVector by lazy {
    simpleIcon("Filled.EditCalendar") {
        moveTo(12.0f, 22.0f)
        horizontalLineTo(5.0f)
        curveToRelative(-1.11f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
        lineTo(3.01f, 6.0f)
        curveToRelative(0.0f, -1.1f, 0.88f, -2.0f, 1.99f, -2.0f)
        horizontalLineToRelative(1.0f)
        verticalLineTo(2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(8.0f)
        verticalLineTo(2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(1.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
        verticalLineToRelative(6.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(10.0f)
        horizontalLineToRelative(7.0f)
        verticalLineTo(22.0f)
        close()
    }
}

val Icons.Filled.EmojiEvents: ImageVector by lazy {
    simpleIcon("Filled.EmojiEvents") {
        moveTo(19.0f, 5.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineTo(3.0f)
        horizontalLineTo(7.0f)
        verticalLineToRelative(2.0f)
        horizontalLineTo(5.0f)
        curveTo(3.9f, 5.0f, 3.0f, 5.9f, 3.0f, 7.0f)
        verticalLineToRelative(1.0f)
        curveToRelative(0.0f, 2.55f, 1.92f, 4.63f, 4.39f, 4.94f)
        curveToRelative(0.63f, 1.5f, 1.98f, 2.63f, 3.61f, 2.96f)
        verticalLineTo(19.0f)
        horizontalLineTo(7.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(10.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(-3.1f)
        curveToRelative(1.63f, -0.33f, 2.98f, -1.46f, 3.61f, -2.96f)
        curveTo(19.08f, 12.63f, 21.0f, 10.55f, 21.0f, 8.0f)
        verticalLineTo(7.0f)
        curveTo(21.0f, 5.9f, 20.1f, 5.0f, 19.0f, 5.0f)
        close()
    }
}

val Icons.Filled.History: ImageVector by lazy {
    simpleIcon("Filled.History") {
        moveTo(13.0f, 3.0f)
        curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
        horizontalLineTo(1.0f)
        lineToRelative(3.89f, 3.89f)
        lineToRelative(0.07f, 0.14f)
        lineTo(9.0f, 12.0f)
        horizontalLineTo(6.0f)
        curveToRelative(0.0f, -3.87f, 3.13f, -7.0f, 7.0f, -7.0f)
        reflectiveCurveToRelative(7.0f, 3.13f, 7.0f, 7.0f)
        reflectiveCurveToRelative(-3.13f, 7.0f, -7.0f, 7.0f)
        curveToRelative(-1.93f, 0.0f, -3.68f, -0.79f, -4.94f, -2.06f)
        lineToRelative(-1.42f, 1.42f)
        curveTo(8.27f, 19.99f, 10.51f, 21.0f, 13.0f, 21.0f)
        curveToRelative(4.97f, 0.0f, 9.0f, -4.03f, 9.0f, -9.0f)
        reflectiveCurveTo(17.97f, 3.0f, 13.0f, 3.0f)
        close()
        moveTo(12.0f, 8.0f)
        verticalLineToRelative(5.0f)
        lineToRelative(4.28f, 2.54f)
        lineToRelative(0.72f, -1.21f)
        lineToRelative(-3.5f, -2.08f)
        verticalLineTo(8.0f)
        horizontalLineTo(12.0f)
        close()
    }
}

val Icons.Filled.Insights: ImageVector by lazy {
    simpleIcon("Filled.Insights") {
        moveTo(21.0f, 8.0f)
        curveToRelative(-1.45f, 0.0f, -2.26f, 1.44f, -1.93f, 2.51f)
        lineToRelative(-3.55f, 3.56f)
        curveToRelative(-0.3f, -0.09f, -0.74f, -0.09f, -1.04f, 0.0f)
        lineToRelative(-2.55f, -2.55f)
        curveTo(12.27f, 10.45f, 11.46f, 9.0f, 10.0f, 9.0f)
        curveToRelative(-1.45f, 0.0f, -2.27f, 1.44f, -1.93f, 2.52f)
        lineToRelative(-4.56f, 4.55f)
        curveTo(2.44f, 15.74f, 1.0f, 16.55f, 1.0f, 18.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        curveToRelative(1.45f, 0.0f, 2.26f, -1.44f, 1.93f, -2.51f)
        lineToRelative(4.55f, -4.56f)
        curveToRelative(0.3f, 0.09f, 0.74f, 0.09f, 1.04f, 0.0f)
        lineToRelative(2.55f, 2.55f)
        curveToRelative(-0.33f, 1.07f, 0.48f, 2.52f, 1.93f, 2.52f)
        curveToRelative(1.45f, 0.0f, 2.26f, -1.44f, 1.93f, -2.51f)
        lineToRelative(3.56f, -3.55f)
        curveTo(21.55f, 12.26f, 23.0f, 11.45f, 23.0f, 10.0f)
        curveTo(23.0f, 8.9f, 22.1f, 8.0f, 21.0f, 8.0f)
        close()
    }
}

val Icons.Filled.Layers: ImageVector by lazy {
    simpleIcon("Filled.Layers") {
        moveTo(11.99f, 18.54f)
        lineToRelative(-7.37f, -5.73f)
        lineTo(3.0f, 14.07f)
        lineToRelative(9.0f, 7.0f)
        lineToRelative(9.0f, -7.0f)
        lineToRelative(-1.63f, -1.27f)
        lineToRelative(-7.38f, 5.74f)
        close()
        moveTo(12.0f, 16.0f)
        lineToRelative(7.36f, -5.73f)
        lineTo(21.0f, 9.0f)
        lineToRelative(-9.0f, -7.0f)
        lineToRelative(-9.0f, 7.0f)
        lineToRelative(1.63f, 1.27f)
        lineTo(12.0f, 16.0f)
        close()
    }
}

val Icons.Filled.LightMode: ImageVector by lazy {
    simpleIcon("Filled.LightMode") {
        moveTo(12.0f, 7.0f)
        curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
        reflectiveCurveToRelative(2.24f, 5.0f, 5.0f, 5.0f)
        reflectiveCurveToRelative(5.0f, -2.24f, 5.0f, -5.0f)
        reflectiveCurveTo(14.76f, 7.0f, 12.0f, 7.0f)
        close()
        moveTo(2.0f, 13.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(20.0f, 13.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(11.0f, 2.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(2.0f)
        horizontalLineToRelative(-2.0f)
        close()
        moveTo(11.0f, 20.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-2.0f)
        close()
    }
}

val Icons.Filled.LocalFireDepartment: ImageVector by lazy {
    simpleIcon("Filled.LocalFireDepartment") {
        moveTo(12.0f, 12.9f)
        lineToRelative(-2.13f, 2.09f)
        curveTo(9.31f, 15.55f, 9.0f, 16.28f, 9.0f, 17.06f)
        curveTo(9.0f, 18.68f, 10.35f, 20.0f, 12.0f, 20.0f)
        reflectiveCurveToRelative(3.0f, -1.32f, 3.0f, -2.94f)
        curveToRelative(0.0f, -0.78f, -0.31f, -1.52f, -0.87f, -2.07f)
        lineTo(12.0f, 12.9f)
        close()
        moveTo(16.0f, 6.0f)
        lineToRelative(-0.44f, 0.55f)
        curveTo(14.38f, 8.02f, 12.0f, 7.19f, 12.0f, 5.3f)
        verticalLineTo(2.0f)
        curveToRelative(0.0f, 0.0f, -8.0f, 4.0f, -8.0f, 11.0f)
        curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
        reflectiveCurveToRelative(8.0f, -3.58f, 8.0f, -8.0f)
        curveTo(20.0f, 9.5f, 18.3f, 7.46f, 16.0f, 6.0f)
        close()
    }
}

val Icons.Filled.MusicNote: ImageVector by lazy {
    simpleIcon("Filled.MusicNote") {
        moveTo(12.0f, 3.0f)
        verticalLineToRelative(10.55f)
        curveToRelative(-0.59f, -0.34f, -1.27f, -0.55f, -2.0f, -0.55f)
        curveToRelative(-2.21f, 0.0f, -4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        reflectiveCurveToRelative(4.0f, -1.79f, 4.0f, -4.0f)
        verticalLineTo(7.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(3.0f)
        horizontalLineToRelative(-6.0f)
        close()
    }
}

val Icons.Filled.NotificationsActive: ImageVector by lazy {
    simpleIcon("Filled.NotificationsActive") {
        moveTo(7.58f, 4.08f)
        lineTo(6.15f, 2.65f)
        curveTo(3.75f, 4.48f, 2.17f, 7.3f, 2.03f, 10.5f)
        horizontalLineToRelative(2.0f)
        curveToRelative(0.15f, -2.65f, 1.51f, -4.97f, 3.55f, -6.42f)
        close()
        moveTo(19.97f, 10.5f)
        horizontalLineToRelative(2.0f)
        curveToRelative(-0.15f, -3.2f, -1.73f, -6.02f, -4.12f, -7.85f)
        lineToRelative(-1.42f, 1.43f)
        curveToRelative(2.02f, 1.45f, 3.39f, 3.77f, 3.54f, 6.42f)
        close()
        moveTo(18.0f, 11.0f)
        curveToRelative(0.0f, -3.07f, -1.64f, -5.64f, -4.5f, -6.32f)
        verticalLineTo(4.0f)
        curveToRelative(0.0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
        reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
        verticalLineToRelative(0.68f)
        curveTo(7.63f, 5.36f, 6.0f, 7.92f, 6.0f, 11.0f)
        verticalLineToRelative(5.0f)
        lineToRelative(-2.0f, 2.0f)
        verticalLineToRelative(1.0f)
        horizontalLineToRelative(16.0f)
        verticalLineToRelative(-1.0f)
        lineToRelative(-2.0f, -2.0f)
        verticalLineToRelative(-5.0f)
        close()
        moveTo(12.0f, 22.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        horizontalLineToRelative(-4.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
        close()
    }
}

val Icons.Filled.Palette: ImageVector by lazy {
    simpleIcon("Filled.Palette") {
        moveTo(12.0f, 3.0f)
        curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
        reflectiveCurveToRelative(4.03f, 9.0f, 9.0f, 9.0f)
        curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
        curveToRelative(0.0f, -0.39f, -0.15f, -0.74f, -0.39f, -1.01f)
        curveToRelative(-0.23f, -0.26f, -0.38f, -0.61f, -0.38f, -0.99f)
        curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
        horizontalLineTo(16.0f)
        curveToRelative(2.76f, 0.0f, 5.0f, -2.24f, 5.0f, -5.0f)
        curveToRelative(0.0f, -4.42f, -4.03f, -8.0f, -9.0f, -8.0f)
        close()
    }
}

val Icons.Filled.Pause: ImageVector by lazy {
    simpleIcon("Filled.Pause") {
        moveTo(6.0f, 19.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(5.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(14.0f, 5.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(5.0f)
        horizontalLineToRelative(-4.0f)
        close()
    }
}

val Icons.Filled.PhoneAndroid: ImageVector by lazy {
    simpleIcon("Filled.PhoneAndroid") {
        moveTo(16.0f, 1.0f)
        horizontalLineTo(8.0f)
        curveTo(6.34f, 1.0f, 5.0f, 2.34f, 5.0f, 4.0f)
        verticalLineToRelative(16.0f)
        curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.66f, 0.0f, 3.0f, -1.34f, 3.0f, -3.0f)
        verticalLineTo(4.0f)
        curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
        close()
        moveTo(14.0f, 21.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(-1.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(1.0f)
        close()
        moveTo(17.25f, 18.0f)
        horizontalLineTo(6.75f)
        verticalLineTo(4.0f)
        horizontalLineToRelative(10.5f)
        verticalLineToRelative(14.0f)
        close()
    }
}

val Icons.Filled.PlayCircleOutline: ImageVector by lazy {
    simpleIcon("Filled.PlayCircleOutline") {
        moveTo(10.0f, 16.5f)
        lineToRelative(6.0f, -4.5f)
        lineToRelative(-6.0f, -4.5f)
        verticalLineToRelative(9.0f)
        close()
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.59f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.59f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.59f, 8.0f, -8.0f, 8.0f)
        close()
    }
}

val Icons.Filled.QueryStats: ImageVector by lazy {
    Icons.Filled.Insights
}

val Icons.Filled.RadioButtonUnchecked: ImageVector by lazy {
    simpleIcon("Filled.RadioButtonUnchecked") {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(12.0f, 20.0f)
        curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
        reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
        reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
        reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
        close()
    }
}

val Icons.Filled.Schedule: ImageVector by lazy {
    Icons.Filled.AccessTime
}

val Icons.Filled.School: ImageVector by lazy {
    simpleIcon("Filled.School") {
        moveTo(5.0f, 13.18f)
        verticalLineToRelative(4.0f)
        lineTo(12.0f, 21.0f)
        lineToRelative(7.0f, -3.82f)
        verticalLineToRelative(-4.0f)
        lineTo(12.0f, 17.0f)
        lineToRelative(-7.0f, -3.82f)
        close()
        moveTo(12.0f, 3.0f)
        lineTo(1.0f, 9.0f)
        lineToRelative(11.0f, 6.0f)
        lineToRelative(9.0f, -4.91f)
        verticalLineTo(17.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(9.0f)
        lineTo(12.0f, 3.0f)
        close()
    }
}

val Icons.Filled.Science: ImageVector by lazy {
    simpleIcon("Filled.Science") {
        moveTo(19.8f, 18.4f)
        lineTo(14.0f, 10.67f)
        verticalLineTo(6.5f)
        lineToRelative(1.35f, -1.69f)
        curveTo(15.61f, 4.48f, 15.38f, 4.0f, 14.96f, 4.0f)
        horizontalLineTo(9.04f)
        curveTo(8.62f, 4.0f, 8.39f, 4.48f, 8.65f, 4.81f)
        lineTo(10.0f, 6.5f)
        verticalLineToRelative(4.17f)
        lineTo(4.2f, 18.4f)
        curveTo(3.71f, 19.06f, 4.18f, 20.0f, 5.0f, 20.0f)
        horizontalLineToRelative(14.0f)
        curveTo(19.82f, 20.0f, 20.29f, 19.06f, 19.8f, 18.4f)
        close()
    }
}

val Icons.Filled.Security: ImageVector by lazy {
    simpleIcon("Filled.Security") {
        moveTo(12.0f, 1.0f)
        lineTo(3.0f, 5.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 5.55f, 3.84f, 10.74f, 9.0f, 12.0f)
        curveToRelative(5.16f, -1.26f, 9.0f, -6.45f, 9.0f, -12.0f)
        verticalLineTo(5.0f)
        lineToRelative(-9.0f, -4.0f)
        close()
        moveTo(12.0f, 11.99f)
        horizontalLineToRelative(7.0f)
        curveToRelative(-0.53f, 4.12f, -3.28f, 7.79f, -7.0f, 8.94f)
        verticalLineTo(12.0f)
        horizontalLineTo(5.0f)
        verticalLineTo(6.3f)
        lineToRelative(7.0f, -3.11f)
        verticalLineToRelative(8.8f)
        close()
    }
}

val Icons.Filled.Shield: ImageVector by lazy {
    simpleIcon("Filled.Shield") {
        moveTo(12.0f, 1.0f)
        lineTo(3.0f, 5.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 5.55f, 3.84f, 10.74f, 9.0f, 12.0f)
        curveToRelative(5.16f, -1.26f, 9.0f, -6.45f, 9.0f, -12.0f)
        verticalLineTo(5.0f)
        lineTo(12.0f, 1.0f)
        close()
    }
}

val Icons.Filled.Stars: ImageVector by lazy {
    simpleIcon("Filled.Stars") {
        moveTo(11.99f, 2.0f)
        curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
        curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
        reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
        close()
        moveTo(16.23f, 18.0f)
        lineTo(12.0f, 15.45f)
        lineTo(7.77f, 18.0f)
        lineToRelative(1.12f, -4.81f)
        lineToRelative(-3.73f, -3.23f)
        lineToRelative(4.92f, -0.42f)
        lineTo(12.0f, 5.0f)
        lineToRelative(1.92f, 4.53f)
        lineToRelative(4.92f, 0.42f)
        lineToRelative(-3.73f, 3.23f)
        lineTo(16.23f, 18.0f)
        close()
    }
}

val Icons.Filled.Stop: ImageVector by lazy {
    simpleIcon("Filled.Stop") {
        moveTo(6.0f, 6.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(12.0f)
        horizontalLineTo(6.0f)
        close()
    }
}

val Icons.Filled.VerifiedUser: ImageVector by lazy {
    simpleIcon("Filled.VerifiedUser") {
        moveTo(12.0f, 1.0f)
        lineTo(3.0f, 5.0f)
        verticalLineToRelative(6.0f)
        curveToRelative(0.0f, 5.55f, 3.84f, 10.74f, 9.0f, 12.0f)
        curveToRelative(5.16f, -1.26f, 9.0f, -6.45f, 9.0f, -12.0f)
        verticalLineTo(5.0f)
        lineToRelative(-9.0f, -4.0f)
        close()
        moveTo(10.0f, 17.0f)
        lineToRelative(-4.0f, -4.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(10.0f, 14.17f)
        lineToRelative(6.59f, -6.59f)
        lineTo(18.0f, 9.0f)
        lineToRelative(-8.0f, 8.0f)
        close()
    }
}

val Icons.Filled.VideoLibrary: ImageVector by lazy {
    simpleIcon("Filled.VideoLibrary") {
        moveTo(4.0f, 6.0f)
        horizontalLineTo(2.0f)
        verticalLineToRelative(14.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineTo(4.0f)
        verticalLineTo(6.0f)
        close()
        moveTo(20.0f, 2.0f)
        horizontalLineTo(8.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(12.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(4.0f)
        curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
        close()
        moveTo(12.0f, 14.5f)
        verticalLineToRelative(-9.0f)
        lineToRelative(6.0f, 4.5f)
        lineToRelative(-6.0f, 4.5f)
        close()
    }
}

val Icons.Filled.WbSunny: ImageVector by lazy {
    Icons.Filled.LightMode
}

val Icons.Filled.WorkspacePremium: ImageVector by lazy {
    simpleIcon("Filled.WorkspacePremium") {
        moveTo(9.68f, 13.69f)
        lineTo(12.0f, 11.93f)
        lineToRelative(2.31f, 1.76f)
        lineToRelative(-0.88f, -2.85f)
        lineTo(15.75f, 9.0f)
        horizontalLineToRelative(-2.84f)
        lineTo(12.0f, 6.19f)
        lineTo(11.09f, 9.0f)
        horizontalLineTo(8.25f)
        lineToRelative(2.31f, 1.84f)
        lineTo(9.68f, 13.69f)
        close()
        moveTo(20.0f, 10.0f)
        curveToRelative(0.0f, -4.42f, -3.58f, -8.0f, -8.0f, -8.0f)
        reflectiveCurveToRelative(-8.0f, 3.58f, -8.0f, 8.0f)
        curveToRelative(0.0f, 2.03f, 0.76f, 3.87f, 2.0f, 5.28f)
        verticalLineTo(23.0f)
        lineToRelative(6.0f, -2.0f)
        lineToRelative(6.0f, 2.0f)
        verticalLineToRelative(-7.72f)
        curveTo(19.24f, 13.87f, 20.0f, 12.03f, 20.0f, 10.0f)
        close()
    }
}
