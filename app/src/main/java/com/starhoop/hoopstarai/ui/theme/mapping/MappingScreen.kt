package com.starhoop.hoopstar.ui.mapping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.domain.model.MappingItem
import com.starhoop.hoopstar.domain.model.MappingResult
import com.starhoop.hoopstar.domain.model.MatchRating
import com.starhoop.hoopstar.ui.components.EmptyState
import com.starhoop.hoopstar.ui.components.ErrorState
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappingScreen(
    onBack: () -> Unit,
    onContinueToHighlights: (jobId: Int, teamId: Int) -> Unit,
    viewModel: MappingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("מיפוי שחקנים", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "חזרה")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is UiState.Loading -> Column(
                    Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("מזהה שחקנים בסרטון...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                is UiState.Empty -> EmptyState(
                    icon = Icons.Default.PersonSearch,
                    title = "לא זוהו שחקנים",
                    subtitle = "ה-AI לא מצא מספרי חולצה ברורים בסרטון הזה."
                )
                is UiState.Error -> ErrorState(s.message, onRetry = viewModel::run)
                is UiState.Success -> MappingContent(
                    result = s.data,
                    onContinue = { onContinueToHighlights(viewModel.jobId, viewModel.teamId) }
                )
            }
        }
    }
}

@Composable
private fun MappingContent(result: MappingResult, onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        // סיכום ספירות
        Row(
            Modifier.fillMaxWidth().padding(16.dp, 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CountChip("גבוה", result.highCount, ratingColor(MatchRating.HIGH), Modifier.weight(1f))
            CountChip("בינוני", result.mediumCount, ratingColor(MatchRating.MEDIUM), Modifier.weight(1f))
            CountChip("נמוך", result.lowCount, ratingColor(MatchRating.LOW), Modifier.weight(1f))
            CountChip("ללא", result.noMatchCount, ratingColor(MatchRating.NO_MATCH), Modifier.weight(1f))
        }

        Text(
            "ה-AI זיהה ${result.total} שחקנים בסרטון. בדוק את ההתאמות לסגל:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(result.items) { item -> MappingRow(item) }
        }

        HoopPrimaryButton(
            text = "המשך להיילייטים",
            onClick = onContinue,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun CountChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$count", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = color)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MappingRow(item: MappingItem) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // חולצה שזוהתה
        Box(
            Modifier.clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                item.detectedJersey?.let { "#$it" } ?: "?",
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                item.suggestedPlayerName ?: "ללא התאמה",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            val conf = item.confidenceMean?.let { "ביטחון ${(it * 100).toInt()}%" } ?: ""
            val frames = item.frameCount?.let { "$it פריימים" } ?: ""
            Text(
                listOf(conf, frames).filter { it.isNotBlank() }.joinToString("  ·  "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RatingBadge(item.rating)
    }
}

@Composable
private fun RatingBadge(rating: MatchRating) {
    val color = ratingColor(rating)
    Box(
        Modifier.clip(RoundedCornerShape(20.dp)).background(color).padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(ratingLabel(rating), color = Color.Black, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ratingColor(rating: MatchRating): Color = when (rating) {
    MatchRating.HIGH -> Color(0xFF35C759)
    MatchRating.MEDIUM -> Color(0xFFFFB020)
    MatchRating.LOW -> Color(0xFFFF8A3D)
    MatchRating.NO_MATCH, MatchRating.UNKNOWN -> Color(0xFF8A8A95)
}

private fun ratingLabel(rating: MatchRating): String = when (rating) {
    MatchRating.HIGH -> "גבוה"
    MatchRating.MEDIUM -> "בינוני"
    MatchRating.LOW -> "נמוך"
    MatchRating.NO_MATCH -> "ללא"
    MatchRating.UNKNOWN -> "?"
}