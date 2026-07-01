package com.starhoop.hoopstar.ui.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.core.jobStatusLabel
import com.starhoop.hoopstar.domain.model.Job
import com.starhoop.hoopstar.domain.model.JobStatus
import com.starhoop.hoopstar.ui.components.EmptyState
import com.starhoop.hoopstar.ui.components.ErrorState
import com.starhoop.hoopstar.ui.components.HoopPrimaryButton
import com.starhoop.hoopstar.ui.components.ListSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onBack: () -> Unit,
    onOpenJob: (Int) -> Unit,
    viewModel: VideosViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.onFileSelected(uri.toString(), uri.lastPathSegment)
    }

    LaunchedEffect(state.newJobId) {
        state.newJobId?.let { jobId ->
            viewModel.consumeNavigation()
            onOpenJob(jobId)
        }
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Games", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // אזור העלאה
            UploadBox(
                fileName = state.selectedFileName,
                uploading = state.uploading,
                error = state.uploadError,
                onPick = { picker.launch("video/*") },
                onUpload = viewModel::upload,
                onClear = viewModel::clearSelection
            )

            Text(
                "Game History",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp)
            )

            Box(Modifier.fillMaxSize()) {
                when (val s = state.jobs) {
                    is UiState.Loading -> ListSkeleton(rows = 4, rowHeight = 64.dp)
                    is UiState.Empty -> EmptyState(
                        icon = Icons.Default.SportsBasketball,
                        title = "No games yet",
                        subtitle = "Upload a game video to start processing."
                    )
                    is UiState.Error -> ErrorState(s.message, onRetry = viewModel::loadJobs)
                    is UiState.Success -> LazyColumn(
                        contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(s.data, key = { it.jobId }) { job ->
                            JobRow(job) { onOpenJob(job.jobId) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UploadBox(
    fileName: String?,
    uploading: Boolean,
    error: String?,
    onPick: () -> Unit,
    onUpload: () -> Unit,
    onClear: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .clickable(enabled = !uploading) { onPick() }
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    if (fileName == null) Icons.Default.CloudUpload else Icons.Default.VideoFile,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    fileName ?: "Select a game video (MP4 / MOV / AVI / MKV)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                if (fileName == null) {
                    Text("Up to 1.5GB", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium)
        }

        if (fileName != null) {
            Spacer(Modifier.height(14.dp))
            HoopPrimaryButton(
                text = if (uploading) "Uploading..." else "Upload & Process",
                onClick = onUpload,
                loading = uploading
            )
            if (!uploading) {
                Spacer(Modifier.height(6.dp))
                Text("Choose another video", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onClear() }.padding(4.dp))
            }
        }
    }
}

@Composable
private fun JobRow(job: Job, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(job.sourceFilename ?: "Game #${job.jobId}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Spacer(Modifier.height(4.dp))
            Text("ID  #${job.jobId}", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        StatusPill(job.status)
    }
}

@Composable
fun StatusPill(status: JobStatus) {
    val (bg, fg) = when (status) {
        JobStatus.COMPLETED -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        JobStatus.PROCESSING, JobStatus.PENDING ->
            MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        JobStatus.FAILED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.outline to MaterialTheme.colorScheme.onSurface
    }
    Box(
        Modifier.clip(RoundedCornerShape(20.dp)).background(bg).padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(jobStatusLabel(status), color = fg, style = MaterialTheme.typography.labelLarge)
    }
}