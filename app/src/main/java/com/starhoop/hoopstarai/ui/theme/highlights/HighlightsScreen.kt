package com.starhoop.hoopstar.ui.highlights

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.font.FontWeight
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
    viewModel: HighlightsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val team = (state.roster as? UiState.Success)?.data
    val accent = parseHexColor(team?.color)

    // בקשת הרשאת התראות (אנדרואיד 13+)
    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* גם אם נדחה, החילוץ ימשיך — פשוט בלי התראה */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Highlights", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                is UiState.Empty -> EmptyState(Icons.Default.MovieFilter, "No players",
                    "You need a roster to generate highlights.")
                is UiState.Error -> ErrorState(s.message, onRetry = viewModel::loadRoster)
                is UiState.Success -> {
                    Column(Modifier.fillMaxSize()) {
                        Text(
                            "Select a player to generate their highlight reel:",
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

    // אחרי שליחה לרקע — מסך אישור שאפשר לצאת ממנו
    if (state.started) {
        StartedOverlay(
            playerName = state.selectedPlayer?.fullName ?: "Player",
            onDone = viewModel::clearSelection
        )
    } else if (state.selectedPlayer != null) {
        Box(
            Modifier.fillMaxSize().navigationBarsPadding().padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            HoopPrimaryButton(
                text = "Generate reel for ${state.selectedPlayer!!.fullName}",
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
            .background(if (selected) accent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant)
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
        if (selected) Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = accent)
    }
}

@Composable
private fun StartedOverlay(playerName: String, onDone: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)).padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle, null, Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(20.dp))
            Text("Generating in the background", style = MaterialTheme.typography.headlineMedium,
                color = Color.White, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text("$playerName's reel is being created. You can leave the app — we'll notify you when it's ready, and it'll be saved to the player's card.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            HoopPrimaryButton(text = "Got it", onClick = onDone, modifier = Modifier.width(200.dp))
        }
    }
}