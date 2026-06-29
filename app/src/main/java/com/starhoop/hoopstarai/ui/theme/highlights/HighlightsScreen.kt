package com.starhoop.hoopstar.ui.highlights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.core.parseHexColor
import com.starhoop.hoopstar.domain.model.Player
import com.starhoop.hoopstar.ui.components.EmptyState
import com.starhoop.hoopstar.ui.components.ErrorState
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton
import com.starhoop.hoopstar.ui.components.JerseyChip
import com.starhoop.hoopstar.ui.components.PlayerAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightsScreen(
    onBack: () -> Unit,
    onPlayReel: (url: String, playerName: String) -> Unit,
    viewModel: HighlightsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val team = (state.roster as? UiState.Success)?.data
    val accent = parseHexColor(team?.color)

    // כשמוכן — עוברים אוטומטית לנגן
    LaunchedEffect(state.phase) {
        if (state.phase == GenerationPhase.READY) {
            state.playbackUrl?.let { url ->
                onPlayReel(url, state.selectedPlayer?.fullName ?: "שחקן")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("היילייטים", style = MaterialTheme.typography.titleLarge) },
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
            when (val s = state.roster) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                is UiState.Empty -> EmptyState(Icons.Default.MovieFilter, "אין שחקנים",
                    "צריך סגל כדי לייצר היילייטים.")
                is UiState.Error -> ErrorState(s.message, onRetry = viewModel::loadRoster)
                is UiState.Success -> {
                    Column(Modifier.fillMaxSize()) {
                        Text(
                            "בחר שחקן כדי לייצר לו ריל היילייטים:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(s.data.players, key = { it.playerId }) { player ->
                                PlayerPickRow(
                                    player = player,
                                    accent = accent,
                                    selected = state.selectedPlayer?.playerId == player.playerId,
                                    onClick = { viewModel.selectPlayer(player) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // שכבת ייצור/שגיאה
    if (state.phase != GenerationPhase.IDLE && state.selectedPlayer != null) {
        GenerationOverlay(
            phase = state.phase,
            playerName = state.selectedPlayer!!.fullName,
            errorMessage = state.errorMessage,
            onRetry = viewModel::retry,
            onDismiss = viewModel::clearSelection
        )
    } else if (state.selectedPlayer != null) {
        // נבחר שחקן אבל עוד לא התחלנו — כפתור ייצור צף
        Box(Modifier.fillMaxSize().padding(16.dp), Alignment.BottomCenter) {
            HoopPrimaryButton(
                text = "ייצר ריל ל-${state.selectedPlayer!!.fullName}",
                onClick = viewModel::generate
            )
        }
    }
}

@Composable
private fun PlayerPickRow(player: Player, accent: Color, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) accent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JerseyChip(player.jerseyNumber, accent)
        Spacer(Modifier.width(12.dp))
        PlayerAvatar(player.fullName, player.photoUrl, accent)
        Spacer(Modifier.width(12.dp))
        Text(player.fullName, style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        if (selected) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = accent)
        }
    }
}

@Composable
private fun GenerationOverlay(
    phase: GenerationPhase,
    playerName: String,
    errorMessage: String?,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.82f)).padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (phase) {
                GenerationPhase.EXTRACTING, GenerationPhase.COMPOSING -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp), strokeWidth = 5.dp)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        if (phase == GenerationPhase.EXTRACTING) "מחלץ קליפים של $playerName..."
                        else "מרכיב את הריל (אינטרו, מוזיקה)...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White, textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("זה לוקח כמה דקות — אל תסגור את המסך.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                }
                GenerationPhase.NO_CLIPS -> {
                    Icon(Icons.Default.MovieFilter, null, Modifier.size(72.dp),
                        tint = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.height(20.dp))
                    Text("אין היילייטים ל-$playerName", style = MaterialTheme.typography.titleLarge,
                        color = Color.White, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text("ה-AI לא מצא רגעים בולטים של השחקן הזה בסרטון.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(onClick = onDismiss) { Text("בחר שחקן אחר") }
                }
                GenerationPhase.ERROR -> {
                    Icon(Icons.Default.ErrorOutline, null, Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(20.dp))
                    Text(errorMessage ?: "משהו השתבש", style = MaterialTheme.typography.titleMedium,
                        color = Color.White, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onDismiss) { Text("ביטול") }
                        HoopPrimaryButton(text = "נסה שוב", onClick = onRetry,
                            modifier = Modifier.width(140.dp))
                    }
                }
                else -> Unit
            }
        }
    }
}