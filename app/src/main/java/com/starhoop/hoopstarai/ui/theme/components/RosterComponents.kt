package com.starhoop.hoopstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.starhoop.hoopstar.core.readableTextOn

@Composable
fun JerseyChip(number: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .widthIn(min = 40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "#$number",
            color = readableTextOn(color),
            fontWeight = FontWeight.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun PlayerAvatar(name: String, photoUrl: String?, accent: Color, size: androidx.compose.ui.unit.Dp = 46.dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(accent.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = name,
                modifier = Modifier.size(size).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = accent,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}