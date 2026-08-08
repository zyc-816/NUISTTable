package com.example.nuisttable.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class CourseCardColorPreset {
    Rose,
    Coral,
    Orange,
    Amber,
    Lime,
    Green,
    Emerald,
    Teal,
    Cyan,
    Sky,
    Blue,
    Indigo,
    Violet,
    Purple,
    Pink
}

data class CourseCardPalette(
    val rose: Color,
    val coral: Color,
    val orange: Color,
    val amber: Color,
    val lime: Color,
    val green: Color,
    val emerald: Color,
    val teal: Color,
    val cyan: Color,
    val sky: Color,
    val blue: Color,
    val indigo: Color,
    val violet: Color,
    val purple: Color,
    val pink: Color
) {
    fun colorOf(preset: CourseCardColorPreset): Color {
        return when (preset) {
            CourseCardColorPreset.Rose -> rose
            CourseCardColorPreset.Coral -> coral
            CourseCardColorPreset.Orange -> orange
            CourseCardColorPreset.Amber -> amber
            CourseCardColorPreset.Lime -> lime
            CourseCardColorPreset.Green -> green
            CourseCardColorPreset.Emerald -> emerald
            CourseCardColorPreset.Teal -> teal
            CourseCardColorPreset.Cyan -> cyan
            CourseCardColorPreset.Sky -> sky
            CourseCardColorPreset.Blue -> blue
            CourseCardColorPreset.Indigo -> indigo
            CourseCardColorPreset.Violet -> violet
            CourseCardColorPreset.Purple -> purple
            CourseCardColorPreset.Pink -> pink
        }
    }
}

val LightCourseCardPalette = CourseCardPalette(
    rose = Color(0xFFF4D7E2),
    coral = Color(0xFFF8D8D1),
    orange = Color(0xFFF8E0C7),
    amber = Color(0xFFF6E7BA),
    lime = Color(0xFFE9EDC5),
    green = Color(0xFFDDEED6),
    emerald = Color(0xFFD2EDE2),
    teal = Color(0xFFD2EBEA),
    cyan = Color(0xFFD7EBF1),
    sky = Color(0xFFDCEAF8),
    blue = Color(0xFFDCE4F6),
    indigo = Color(0xFFE0E0F4),
    violet = Color(0xFFE8DDF4),
    purple = Color(0xFFEEDAF4),
    pink = Color(0xFFF4D8EC)
)

val DarkCourseCardPalette = CourseCardPalette(
    rose = Color(0xFF5B3F4A),
    coral = Color(0xFF65443E),
    orange = Color(0xFF6A4B39),
    amber = Color(0xFF655437),
    lime = Color(0xFF56603A),
    green = Color(0xFF445A40),
    emerald = Color(0xFF3F5A4D),
    teal = Color(0xFF3D5957),
    cyan = Color(0xFF405762),
    sky = Color(0xFF415769),
    blue = Color(0xFF424F68),
    indigo = Color(0xFF474A67),
    violet = Color(0xFF514867),
    purple = Color(0xFF574663),
    pink = Color(0xFF5D4358)
)

val LocalCourseCardPalette = staticCompositionLocalOf { LightCourseCardPalette }

@Composable
fun courseCardColorOf(preset: CourseCardColorPreset): Color {
    return LocalCourseCardPalette.current.colorOf(preset)
}
