package com.starhoop.hoopstar.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.core.parseHexColor
import com.starhoop.hoopstar.core.readableTextOn
import com.starhoop.hoopstar.domain.model.Player
import com.starhoop.hoopstar.ui.components.EmptyState
import com.starhoop.hoopstar.ui.components.ErrorState
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton
import com.starhoop.hoopstar.ui.components.HoopTextField
import com.starhoop.hoopstar.ui.components.JerseyChip
import com.starhoop.hoopstar.ui.components.PlayerAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RosterScreen(
    onBack: () -> Unit,
    onOpenGames: (Int) -> Unit,
    viewModel: RosterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val team = (state.team as? UiState.Success)?.data
    val accent = parseHexColor(team?.color)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(32.dp).clip(CircleShape)
                                .background(accent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                com.starhoop.hoopstar.core.TeamLogos.iconFor(team?.logoUrl),
                                contentDescription = null, tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(team?.name ?: "סגל", style = MaterialTheme.typography.titleLarge)
                    }
                },
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
        floatingActionButton = {
            if (state.isOwner) {
                FloatingActionButton(
                    onClick = viewModel::openAdd,
                    containerColor = accent,
                    contentColor = readableTextOn(accent)
                ) { Icon(Icons.Default.Add, contentDescription = "הוסף שחקן") }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // כפתור "משחקים"
            team?.let {
                Button(
                    onClick = { onOpenGames(it.teamId) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp).height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent.copy(alpha = 0.18f),
                        contentColor = accent
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.SportsBasketball, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("משחקים והיילייטים", style = MaterialTheme.typography.labelLarge)
                }
            }

            Box(Modifier.fillMaxSize()) {
                when (val s = state.team) {
                    is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    is UiState.Error -> ErrorState(s.message, onRetry = viewModel::load)
                    is UiState.Empty -> Unit
                    is UiState.Success -> {
                        if (s.data.players.isEmpty()) {
                            EmptyState(
                                icon = Icons.Default.PersonOutline,
                                title = "אין שחקנים בסגל",
                                subtitle = if (state.isOwner) "לחץ על + כדי להוסיף שחקן ראשון."
                                else "לקבוצה הזו עדיין אין שחקנים."
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 96.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(s.data.players, key = { it.playerId }) { player ->
                                    PlayerRow(player, accent, state.isOwner,
                                        onEdit = { viewModel.openEdit(player) },
                                        onDelete = { viewModel.requestDelete(player) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    state.editor?.let { editor ->
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = viewModel::closeEditor,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            PlayerEditorSheet(editor, viewModel)
        }
    }

    state.confirmDelete?.let { player ->
        AlertDialog(
            onDismissRequest = viewModel::cancelDelete,
            title = { Text("מחיקת שחקן") },
            text = { Text("למחוק את ${player.fullName} (#${player.jerseyNumber})?") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text("מחק", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = viewModel::cancelDelete) { Text("ביטול") } }
        )
    }
}

@Composable
private fun PlayerRow(
    player: Player, accent: Color, isOwner: Boolean,
    onEdit: () -> Unit, onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JerseyChip(player.jerseyNumber, accent)
        Spacer(Modifier.width(12.dp))
        PlayerAvatar(player.fullName, player.photoUrl, accent)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(player.fullName, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface)
            if (player.birthYear != null) {
                Text("שנתון ${player.birthYear}", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (isOwner) {
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "אפשרויות")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("עריכה") },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { menuOpen = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text("מחיקה") },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = { menuOpen = false; onDelete() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerEditorSheet(editor: PlayerEditorForm, viewModel: RosterViewModel) {
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Text(
            if (editor.isEdit) "עריכת שחקן" else "שחקן חדש",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(20.dp))
        HoopTextField(editor.jersey, viewModel::onJersey, "מספר חולצה (0-99)",
            keyboardType = KeyboardType.Number, isError = editor.error != null)
        Spacer(Modifier.height(14.dp))
        HoopTextField(editor.fullName, viewModel::onName, "שם מלא", isError = editor.error != null)
        Spacer(Modifier.height(14.dp))
        HoopTextField(editor.birthYear, viewModel::onBirthYear, "שנת לידה (אופציונלי)",
            keyboardType = KeyboardType.Number)
        Spacer(Modifier.height(14.dp))
        HoopTextField(editor.photoUrl, viewModel::onPhotoUrl, "קישור לתמונה (אופציונלי)")

        if (editor.error != null) {
            Spacer(Modifier.height(10.dp))
            Text(editor.error, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(24.dp))
        HoopPrimaryButton(
            if (editor.isEdit) "שמור שינויים" else "הוסף שחקן",
            viewModel::submitEditor, loading = editor.loading
        )
        Spacer(Modifier.height(12.dp))
    }
}