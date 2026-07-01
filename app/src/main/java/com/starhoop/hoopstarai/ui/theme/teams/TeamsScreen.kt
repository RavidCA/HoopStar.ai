package com.starhoop.hoopstar.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.starhoop.hoopstar.core.TeamColorPalette
import com.starhoop.hoopstar.core.TeamLogos
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.core.parseHexColor
import com.starhoop.hoopstar.core.readableTextOn
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.ui.components.EmptyState
import com.starhoop.hoopstar.ui.components.ErrorState
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton
import com.starhoop.hoopstar.ui.components.HoopTextField
import com.starhoop.hoopstar.ui.components.ListSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    onTeamClick: (Int) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: TeamsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) viewModel.load()
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Teams", style = MaterialTheme.typography.titleLarge)
                        if (state.coachName.isNotBlank()) {
                            Text("Hi, ${state.coachName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onLoggedOut() }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::openCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Team") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state.teams) {
                is UiState.Loading -> ListSkeleton(rows = 5, rowHeight = 86.dp)
                is UiState.Empty -> EmptyState(
                    icon = Icons.Default.Groups,
                    title = "No teams yet",
                    subtitle = "Tap \"New Team\" to start building your roster."
                )
                is UiState.Error -> ErrorState(s.message, onRetry = viewModel::load)
                is UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.data, key = { it.teamId }) { team ->
                        TeamCard(
                            team = team,
                            onClick = { onTeamClick(team.teamId) },
                            onDelete = { viewModel.requestHide(team) }
                        )
                    }
                }
            }
        }
    }

    if (state.showCreate) {
        CreateTeamDialog(state.create, viewModel)
    }

    state.confirmHide?.let { team ->
        AlertDialog(
            onDismissRequest = viewModel::cancelHide,
            icon = { Icon(Icons.Default.Delete, contentDescription = null,
                tint = MaterialTheme.colorScheme.error) },
            title = { Text("Remove Team") },
            text = { Text("Remove \"${team.name}\" from your list? This hides it on this device — the team data stays on the server.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmHide) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = viewModel::cancelHide) { Text("Cancel") } }
        )
    }
}

@Composable
private fun TeamCard(team: Team, onClick: () -> Unit, onDelete: () -> Unit) {
    val accent = parseHexColor(team.color)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(6.dp).height(86.dp).background(accent))
        Box(
            Modifier.padding(start = 14.dp).size(46.dp).clip(CircleShape).background(accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(TeamLogos.iconFor(team.logoUrl), contentDescription = null, tint = accent,
                modifier = Modifier.size(26.dp))
        }
        Column(Modifier.weight(1f).padding(14.dp)) {
            Text(team.name, style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(
                buildString {
                    if (team.season.isNotBlank()) append("Season ${team.season}  ·  ")
                    append("${team.players.size} players")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.padding(end = 6.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete team",
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CreateTeamDialog(form: CreateTeamForm, viewModel: TeamsViewModel) {
    Dialog(
        onDismissRequest = viewModel::closeCreate,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                Text("New Team", style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(20.dp))
                HoopTextField(form.name, viewModel::onName, "Team Name", isError = form.error != null)
                Spacer(Modifier.height(14.dp))
                HoopTextField(form.season, viewModel::onSeason, "Season")

                Spacer(Modifier.height(20.dp))
                Text("Team Color", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                ColorPalette(selected = form.color, onSelect = viewModel::onColor)

                Spacer(Modifier.height(20.dp))
                Text("Team Logo", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                LogoPalette(selected = form.logoId, accent = parseHexColor(form.color),
                    onSelect = viewModel::onLogo)

                if (form.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(form.error, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(24.dp))
                HoopPrimaryButton("Create Team", viewModel::submitCreate, loading = form.loading)
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = viewModel::closeCreate, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ColorPalette(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TeamColorPalette.chunked(6).forEach { rowColors ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowColors.forEach { hex ->
                    val color = parseHexColor(hex)
                    val isSelected = hex.equals(selected, ignoreCase = true)
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface, shape = CircleShape
                            )
                            .clickable { onSelect(hex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) Icon(Icons.Default.Check, contentDescription = "Selected",
                            tint = readableTextOn(color))
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoPalette(selected: String, accent: Color, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TeamLogos.all.chunked(5).forEach { rowLogos ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowLogos.forEach { logo ->
                    val isSelected = logo.id == selected
                    Box(
                        Modifier.size(48.dp).clip(CircleShape)
                            .background(if (isSelected) accent.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surfaceVariant)
                            .border(width = if (isSelected) 2.dp else 0.dp, color = accent, shape = CircleShape)
                            .clickable { onSelect(logo.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(logo.icon, contentDescription = logo.id,
                            tint = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp))
                    }
                }
            }
        }
    }
}