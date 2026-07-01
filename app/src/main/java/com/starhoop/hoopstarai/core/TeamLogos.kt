package com.starhoop.hoopstar.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * סמלי קבוצה. שומרים מזהה טקסט ב-logo_url של ה-backend (למשל "logo:bolt"),
 * וממפים אותו לאייקון בצד הקליינט.
 */
data class TeamLogo(val id: String, val icon: ImageVector)

object TeamLogos {
    const val PREFIX = "logo:"

    val all = listOf(
        TeamLogo("basketball", Icons.Default.SportsBasketball),
        TeamLogo("star", Icons.Default.Star),
        TeamLogo("bolt", Icons.Default.Bolt),
        TeamLogo("fire", Icons.Default.LocalFireDepartment),
        TeamLogo("flame", Icons.Default.Whatshot),
        TeamLogo("shield", Icons.Default.Shield),
        TeamLogo("paw", Icons.Default.Pets),
        TeamLogo("flash", Icons.Default.FlashOn),
        TeamLogo("anchor", Icons.Default.Anchor),
        TeamLogo("diamond", Icons.Default.Diamond),
    )

    fun encode(id: String): String = "$PREFIX$id"

    /** מחזיר את האייקון לפי הערך שב-logo_url, או ברירת מחדל (כדורסל). */
    fun iconFor(logoUrl: String?): ImageVector {
        val id = logoUrl?.removePrefix(PREFIX)
        return all.firstOrNull { it.id == id }?.icon ?: Icons.Default.SportsBasketball
    }

    fun idFromUrl(logoUrl: String?): String? =
        logoUrl?.takeIf { it.startsWith(PREFIX) }?.removePrefix(PREFIX)
}