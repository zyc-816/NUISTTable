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
    rose = Color(0xFFF0CDD7),
    coral = Color(0xFFF3D0C4),
    orange = Color(0xFFF4D8BF),
    amber = Color(0xFFF4E0B8),
    lime = Color(0xFFE7E4BA),
    green = Color(0xFFD5E7CC),
    emerald = Color(0xFFCCE7DA),
    teal = Color(0xFFCBE4E2),
    cyan = Color(0xFFCEE3EA),
    sky = Color(0xFFD2E3F0),
    blue = Color(0xFFD1DCF0),
    indigo = Color(0xFFD9D8EE),
    violet = Color(0xFFE0D6EE),
    purple = Color(0xFFE7D3EB),
    pink = Color(0xFFF0D0E1)
)

val DarkCourseCardPalette = CourseCardPalette(
    rose = Color(0xFF6A4853),
    coral = Color(0xFF705046),
    orange = Color(0xFF76573F),
    amber = Color(0xFF746038),
    lime = Color(0xFF61633C),
    green = Color(0xFF4B6048),
    emerald = Color(0xFF456257),
    teal = Color(0xFF42615F),
    cyan = Color(0xFF47606A),
    sky = Color(0xFF4B6172),
    blue = Color(0xFF4C5D73),
    indigo = Color(0xFF56596F),
    violet = Color(0xFF5C5770),
    purple = Color(0xFF63556E),
    pink = Color(0xFF6A5467)
)

val LocalCourseCardPalette = staticCompositionLocalOf { LightCourseCardPalette }

@Composable
fun courseCardColorOf(preset: CourseCardColorPreset): Color {
    return LocalCourseCardPalette.current.colorOf(preset)
}
