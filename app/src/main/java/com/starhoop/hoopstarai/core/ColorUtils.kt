package com.starhoop.hoopstar.core

import androidx.compose.ui.graphics.Color
import com.starhoop.hoopstar.ui.theme.HoopOrange

/** פלטת הצבעים הקבועה לבחירת קבוצה. */
val TeamColorPalette = listOf(
    "#FF6F00", "#E53935", "#1E88E5", "#43A047",
    "#8E24AA", "#00ACC1", "#FDD835", "#F4511E",
    "#3949AB", "#EC407A", "#546E7A", "#26A69A"
)

/** ממיר hex (#RRGGBB / #AARRGGBB) ל-Color, עם fallback בטוח. */
fun parseHexColor(hex: String?, fallback: Color = HoopOrange): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val clean = hex.trim().removePrefix("#")
        val value = when (clean.length) {
            6 -> "FF$clean"
            8 -> clean
            else -> return fallback
        }
        Color(value.toLong(16))
    } catch (e: Exception) {
        fallback
    }
}

/** מחזיר שחור/לבן לפי בהירות הרקע, לקריאוּת. */
fun readableTextOn(bg: Color): Color {
    val luminance = 0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue
    return if (luminance > 0.6f) Color.Black else Color.White
}