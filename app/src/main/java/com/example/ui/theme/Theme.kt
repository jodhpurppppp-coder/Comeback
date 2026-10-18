package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RegainFocusTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        ComeBackThemeManager.init(context)
    }

    val option = ComeBackThemeManager.currentOption
    val p = ComeBackThemeManager.palette

    val colorScheme = if (option.isLight) {
        lightColorScheme(
            primary = p.primaryAccent,
            onPrimary = p.onPrimaryButton,
            primaryContainer = p.primaryContainer,
            onPrimaryContainer = p.primaryBright,
            secondary = p.secondaryAccent,
            onSecondary = p.onPrimaryButton,
            secondaryContainer = p.surfaceVariant,
            onSecondaryContainer = p.textPrimary,
            tertiary = p.highlightAccent,
            onTertiary = p.onPrimaryButton,
            tertiaryContainer = p.surfaceElevated,
            onTertiaryContainer = p.textPrimary,
            background = p.background,
            onBackground = p.textPrimary,
            surface = p.surface,
            onSurface = p.textPrimary,
            surfaceVariant = p.surfaceElevated,
            onSurfaceVariant = p.textSecondary,
            outline = p.border,
            error = p.alertAccent,
            onError = Color.White
        )
    } else {
        darkColorScheme(
            primary = p.primaryAccent,
            onPrimary = p.onPrimaryButton,
            primaryContainer = p.primaryContainer,
            onPrimaryContainer = p.primaryBright,
            secondary = p.secondaryAccent,
            onSecondary = p.onPrimaryButton,
            secondaryContainer = p.surfaceVariant,
            onSecondaryContainer = p.textPrimary,
            tertiary = p.highlightAccent,
            onTertiary = p.onPrimaryButton,
            tertiaryContainer = p.surfaceElevated,
            onTertiaryContainer = p.textPrimary,
            background = p.background,
            onBackground = p.textPrimary,
            surface = p.surface,
            onSurface = p.textPrimary,
            surfaceVariant = p.surfaceElevated,
            onSurfaceVariant = p.textSecondary,
            outline = p.border,
            error = p.alertAccent,
            onError = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    RegainFocusTheme(content = content)
}

/**
 * Renders the "Comeback : Daily Routine" Logo using @drawable/img_comeback_logo
 * with high-contrast monochrome adaptation when Black & White theme is active.
 */
@Composable
fun ComeBackLogoIcon(
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val option = ComeBackThemeManager.currentOption
    val isMono = option == AppThemeOption.MONOCHROME_DARK || option == AppThemeOption.MONOCHROME_LIGHT

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.24f))
            .border(
                width = 1.dp,
                color = if (isMono) Color.White.copy(alpha = 0.4f) else Color(0xFF1EA1FF).copy(alpha = 0.55f),
                shape = RoundedCornerShape(size * 0.24f)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_comeback_logo),
            contentDescription = "Comeback : Daily Routine Logo",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            colorFilter = if (isMono) {
                androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                    androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }
                )
            } else {
                null
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Interactive 1-Tap Theme Switcher Bar allowing the user to switch between:
 * - Logo Dark (Matches uploaded ComeBack logo)
 * - Light Mode
 * - Black & White (Dark)
 * - Black & White (Light)
 * - Sapphire & Mint (Color Combo)
 */
@Composable
fun ComeBackThemeSelectorBar(
    modifier: Modifier = Modifier,
    onThemeChanged: (AppThemeOption) -> Unit = {}
) {
    val context = LocalContext.current
    val current = ComeBackThemeManager.currentOption

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = RegainEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "APPEARANCE & THEME (${current.label.uppercase()})",
                    style = MaterialTheme.typography.labelMedium,
                    color = RegainEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Tap to switch",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppThemeOption.entries.forEach { option ->
                val selected = current == option
                val icon = when (option) {
                    AppThemeOption.COMEBACK_DARK -> Icons.Default.DarkMode
                    AppThemeOption.LIGHT_MODE -> Icons.Default.LightMode
                    AppThemeOption.MONOCHROME_DARK, AppThemeOption.MONOCHROME_LIGHT -> Icons.Default.Contrast
                    AppThemeOption.SAPPHIRE_MINT -> Icons.Default.Palette
                }

                Surface(
                    color = if (selected) {
                        RegainEmerald.copy(alpha = 0.20f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            ComeBackThemeManager.setTheme(context, option)
                            onThemeChanged(option)
                        }
                        .border(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) RegainEmerald else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("theme_option_${option.id.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = option.label,
                            tint = if (selected) RegainEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) RegainEmerald else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
