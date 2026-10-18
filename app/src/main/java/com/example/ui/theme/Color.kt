package com.example.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class AppThemeOption(
    val id: String,
    val label: String,
    val subtitle: String,
    val isLight: Boolean
) {
    COMEBACK_DARK(
        id = "COMEBACK_DARK",
        label = "Logo Dark",
        subtitle = "Matches ComeBack Logo • Midnight Navy, Sky Blue & Mint",
        isLight = false
    ),
    LIGHT_MODE(
        id = "LIGHT_MODE",
        label = "Light Mode",
        subtitle = "Daylight Clarity • Crisp White, Cobalt Blue & Aqua Mint",
        isLight = true
    ),
    MONOCHROME_DARK(
        id = "MONOCHROME_DARK",
        label = "Black & White (Dark)",
        subtitle = "Pure Monochrome • Obsidian Black & High-Contrast White",
        isLight = false
    ),
    MONOCHROME_LIGHT(
        id = "MONOCHROME_LIGHT",
        label = "Black & White (Light)",
        subtitle = "Editorial Paper • Pure White & Jet Black Ink",
        isLight = true
    ),
    SAPPHIRE_MINT(
        id = "SAPPHIRE_MINT",
        label = "Sapphire & Mint",
        subtitle = "Vibrant Combo • Deep Royal Sapphire, Electric Cyan & Mint",
        isLight = false
    );

    companion object {
        fun fromId(id: String?): AppThemeOption {
            return entries.find { it.id == id } ?: COMEBACK_DARK
        }
    }
}

data class ComeBackPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceVariant: Color,
    val primaryAccent: Color,      // ComeBack Logo Electric Blue (or Monochrome Primary)
    val primaryBright: Color,      // ComeBack Logo Glowing Mint-Aqua
    val primaryContainer: Color,
    val onPrimaryButton: Color,
    val secondaryAccent: Color,    // ComeBack Logo Sky Cyan
    val highlightAccent: Color,    // ComeBack Logo Mint / Highlight
    val purpleAccent: Color,
    val alertAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val trackBackground: Color,
    val dialogBackground: Color,
    val heroGradient: List<Color>,
    val screenBackdropGradient: List<Color>
)

object ComeBackThemeManager {
    private const val PREF_NAME = "comeback_theme_prefs"
    private const val KEY_THEME = "selected_theme_option"

    var currentOption by mutableStateOf(AppThemeOption.COMEBACK_DARK)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_THEME, AppThemeOption.COMEBACK_DARK.id)
        currentOption = AppThemeOption.fromId(saved)
    }

    fun setTheme(context: Context, option: AppThemeOption) {
        currentOption = option
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, option.id)
            .apply()
    }

    val palette: ComeBackPalette
        get() = when (currentOption) {
            AppThemeOption.COMEBACK_DARK -> ComeBackPalette(
                background = Color(0xFF050914),
                surface = Color(0xFF0C1426),
                surfaceElevated = Color(0xFF121E36),
                surfaceVariant = Color(0xFF172642),
                primaryAccent = Color(0xFF1EA1FF),       // Uploaded Logo Electric Blue
                primaryBright = Color(0xFF4EE6B1),       // Uploaded Logo Mint-Aqua
                primaryContainer = Color(0xFF0C3B66),
                onPrimaryButton = Color(0xFF031022),
                secondaryAccent = Color(0xFF00C6FF),     // Uploaded Logo Cyan-Blue
                highlightAccent = Color(0xFF38BDF8),     // Bright Sky Highlight
                purpleAccent = Color(0xFF6366F1),
                alertAccent = Color(0xFFFB7185),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFF94A3B8),
                border = Color(0xFF1E3252),
                trackBackground = Color(0xFF16243B),
                dialogBackground = Color(0xFF091122),
                heroGradient = listOf(
                    Color(0xFF08162E),
                    Color(0xFF0D2342),
                    Color(0xFF08222B)
                ),
                screenBackdropGradient = listOf(
                    Color(0xFF050812),
                    Color(0xFF091326),
                    Color(0xFF061A24)
                )
            )

            AppThemeOption.LIGHT_MODE -> ComeBackPalette(
                background = Color(0xFFF2F7FC),
                surface = Color(0xFFFFFFFF),
                surfaceElevated = Color(0xFFE6F0FA),
                surfaceVariant = Color(0xFFDCEAF7),
                primaryAccent = Color(0xFF0066CC),       // Crisp ComeBack Blue on Light
                primaryBright = Color(0xFF059669),       // Deep Mint-Teal for Light contrast
                primaryContainer = Color(0xFFD0E8FF),
                onPrimaryButton = Color(0xFFFFFFFF),
                secondaryAccent = Color(0xFF0284C7),     // Sky Cyan on Light
                highlightAccent = Color(0xFF0D9488),     // Aqua-Teal Highlight
                purpleAccent = Color(0xFF4F46E5),
                alertAccent = Color(0xFFE11D48),
                textPrimary = Color(0xFF071324),
                textSecondary = Color(0xFF475569),
                border = Color(0xFFCBD5E1),
                trackBackground = Color(0xFFD5E3F0),
                dialogBackground = Color(0xFFFFFFFF),
                heroGradient = listOf(
                    Color(0xFFE0F2FE),
                    Color(0xFFD1FAE5),
                    Color(0xFFEFF6FF)
                ),
                screenBackdropGradient = listOf(
                    Color(0xFFF2F7FC),
                    Color(0xFFE6F2FF),
                    Color(0xFFECFDF5)
                )
            )

            AppThemeOption.MONOCHROME_DARK -> ComeBackPalette(
                background = Color(0xFF050505),
                surface = Color(0xFF121212),
                surfaceElevated = Color(0xFF1C1C1C),
                surfaceVariant = Color(0xFF242424),
                primaryAccent = Color(0xFFFFFFFF),       // Pure White
                primaryBright = Color(0xFFE5E5E5),       // High-Contrast Silver White
                primaryContainer = Color(0xFF2E2E2E),
                onPrimaryButton = Color(0xFF050505),
                secondaryAccent = Color(0xFFD4D4D4),     // Light Gray
                highlightAccent = Color(0xFFFAFAFA),     // Crisp White
                purpleAccent = Color(0xFFA3A3A3),
                alertAccent = Color(0xFFE5E5E5),
                textPrimary = Color(0xFFFFFFFF),
                textSecondary = Color(0xFFA3A3A3),
                border = Color(0xFF333333),
                trackBackground = Color(0xFF262626),
                dialogBackground = Color(0xFF0D0D0D),
                heroGradient = listOf(
                    Color(0xFF141414),
                    Color(0xFF1F1F1F),
                    Color(0xFF121212)
                ),
                screenBackdropGradient = listOf(
                    Color(0xFF050505),
                    Color(0xFF101010),
                    Color(0xFF080808)
                )
            )

            AppThemeOption.MONOCHROME_LIGHT -> ComeBackPalette(
                background = Color(0xFFF7F7F7),
                surface = Color(0xFFFFFFFF),
                surfaceElevated = Color(0xFFEDEDED),
                surfaceVariant = Color(0xFFE5E5E5),
                primaryAccent = Color(0xFF111111),       // Pure Jet Black
                primaryBright = Color(0xFF262626),       // Deep Charcoal
                primaryContainer = Color(0xFFE0E0E0),
                onPrimaryButton = Color(0xFFFFFFFF),
                secondaryAccent = Color(0xFF404040),     // Slate Ink
                highlightAccent = Color(0xFF171717),     // Bold Black
                purpleAccent = Color(0xFF525252),
                alertAccent = Color(0xFF262626),
                textPrimary = Color(0xFF0A0A0A),
                textSecondary = Color(0xFF525252),
                border = Color(0xFFD4D4D4),
                trackBackground = Color(0xFFE0E0E0),
                dialogBackground = Color(0xFFFFFFFF),
                heroGradient = listOf(
                    Color(0xFFF0F0F0),
                    Color(0xFFE5E5E5),
                    Color(0xFFFAFAFA)
                ),
                screenBackdropGradient = listOf(
                    Color(0xFFFAFAFA),
                    Color(0xFFF0F0F0),
                    Color(0xFFFFFFFF)
                )
            )

            AppThemeOption.SAPPHIRE_MINT -> ComeBackPalette(
                background = Color(0xFF070B20),
                surface = Color(0xFF101738),
                surfaceElevated = Color(0xFF18224D),
                surfaceVariant = Color(0xFF1E2B5E),
                primaryAccent = Color(0xFF00D2FF),       // Electric Cyan
                primaryBright = Color(0xFF38F9D7),       // Neon Aqua Mint
                primaryContainer = Color(0xFF143866),
                onPrimaryButton = Color(0xFF041226),
                secondaryAccent = Color(0xFF4FACFE),     // Royal Sky Blue
                highlightAccent = Color(0xFF43E97B),     // Emerald Mint
                purpleAccent = Color(0xFFA18CD1),
                alertAccent = Color(0xFFFF6B8B),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFFA5B4FC),
                border = Color(0xFF2B3B75),
                trackBackground = Color(0xFF1B2654),
                dialogBackground = Color(0xFF0C122E),
                heroGradient = listOf(
                    Color(0xFF101B47),
                    Color(0xFF0D2B52),
                    Color(0xFF0A2E3D)
                ),
                screenBackdropGradient = listOf(
                    Color(0xFF070B20),
                    Color(0xFF0E173B),
                    Color(0xFF092234)
                )
            )
        }
}

// Reactive theme-aware color properties used across all screens
val RegainBackground: Color get() = ComeBackThemeManager.palette.background
val RegainSurface: Color get() = ComeBackThemeManager.palette.surface
val RegainSurfaceElevated: Color get() = ComeBackThemeManager.palette.surfaceElevated
val RegainSurfaceVariant: Color get() = ComeBackThemeManager.palette.surfaceVariant

val RegainEmerald: Color get() = ComeBackThemeManager.palette.primaryAccent
val RegainEmeraldBright: Color get() = ComeBackThemeManager.palette.primaryBright
val RegainEmeraldDark: Color get() = ComeBackThemeManager.palette.primaryContainer

val RegainCyan: Color get() = ComeBackThemeManager.palette.secondaryAccent
val RegainCyanSoft: Color get() = ComeBackThemeManager.palette.primaryBright

val RegainGold: Color get() = ComeBackThemeManager.palette.highlightAccent
val RegainGoldBright: Color get() = ComeBackThemeManager.palette.primaryBright

val RegainPurple: Color get() = ComeBackThemeManager.palette.purpleAccent
val RegainCoral: Color get() = ComeBackThemeManager.palette.alertAccent

val RegainTextPrimary: Color get() = ComeBackThemeManager.palette.textPrimary
val RegainTextSecondary: Color get() = ComeBackThemeManager.palette.textSecondary
val RegainBorder: Color get() = ComeBackThemeManager.palette.border

val ComeBackOnPrimary: Color get() = ComeBackThemeManager.palette.onPrimaryButton
val ComeBackTrackColor: Color get() = ComeBackThemeManager.palette.trackBackground
val ComeBackDialogBg: Color get() = ComeBackThemeManager.palette.dialogBackground
val ComeBackHeroGradient: List<Color> get() = ComeBackThemeManager.palette.heroGradient
val ComeBackBackdropGradient: List<Color> get() = ComeBackThemeManager.palette.screenBackdropGradient
