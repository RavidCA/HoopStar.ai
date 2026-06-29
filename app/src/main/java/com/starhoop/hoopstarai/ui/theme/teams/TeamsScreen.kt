package com.starhoop.hoopstar.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.starhoop.hoopstar.ui.components.ListSkeleton
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.starhoop.hoopstar.core.TeamColorPalette
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.core.parseHexColor
import com.starhoop.hoopstar.core.readableTextOn
import com.starhoop.hoopstar.domain.model.Team
import com.starhoop.hoopstar.ui.components.EmptyState
import com.starhoop.hoopstar.ui.components.ErrorState
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton
import com.starhoop.hoopstar.ui.components.HoopTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    onTeamClick: (Int) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: TeamsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // טוען מחדש בכל חזרה למסך (כדי לרענן ספירת שחקנים אחרי roster)
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
                        Text("הקבוצות שלי", style = MaterialTheme.typography.titleLarge)
                        if (state.coachName.isNotBlank()) {
                            Text("שלום, ${state.coachName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onLoggedOut() }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "התנתקות")
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
                text = { Text("קבוצה חדשה") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state.teams) {
                is UiState.Loading -> ListSkeleton(rows = 5, rowHeight = 86.dp)
                is UiState.Empty -> EmptyState(
                    icon = Icons.Default.Groups,
                    title = "עדיין אין קבוצות",
                    subtitle = "לחץ על \"קבוצה חדשה\" כדי להתחיל לבנות סגל."
                )
                is UiState.Error -> ErrorState(s.message, onRetry = viewModel::load)
                is UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.data, key = { it.teamId }) { team ->
                        TeamCard(team) { onTeamClick(team.teamId) }
                    }
                }
            }
        }
    }

    if (state.showCreate) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = viewModel::closeCreate,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            CreateTeamSheet(state.create, viewModel)
        }
    }
}

@Composable
private fun TeamCard(team: Team, onClick: () -> Unit) {
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
        Column(Modifier.weight(1f).padding(16.dp)) {
            Text(team.name, style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(
                buildString {
                    if (team.season.isNotBlank()) append("עונה ${team.season}  ·  ")
                    append("${team.players.size} שחקנים")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            Modifier.padding(end = 16.dp).size(40.dp).clip(CircleShape).background(accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = accent)
        }
    }
}

@Composable
private fun CreateTeamSheet(form: CreateTeamForm, viewModel: TeamsViewModel) {
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Text("קבוצה חדשה", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(20.dp))
        HoopTextField(form.name, viewModel::onName, "שם הקבוצה", isError = form.error != null)
        Spacer(Modifier.height(14.dp))
        HoopTextField(form.season, viewModel::onSeason, "עונה")
        Spacer(Modifier.height(20.dp))
        Text("צבע הקבוצה", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // שתי שורות של צבעים
        }
        ColorPalette(selected = form.color, onSelect = viewModel::onColor)

        if (form.error != null) {
            Spacer(Modifier.height(10.dp))
            Text(form.error, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(24.dp))
        HoopPrimaryButton("צור קבוצה", viewModel::submitCreate, loading = form.loading)
        Spacer(Modifier.height(12.dp))
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
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .clickable { onSelect(hex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = "נבחר",
                                tint = readableTextOn(color)
                            )
                        }
                    }
                }
            }
        }
    }
}