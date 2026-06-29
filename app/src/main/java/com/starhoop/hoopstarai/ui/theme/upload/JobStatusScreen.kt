package com.starhoop.hoopstar.ui.upload

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starhoop.hoopstar.core.UiState
import com.starhoop.hoopstar.core.formatDuration
import com.starhoop.hoopstar.domain.model.Job
import com.starhoop.hoopstar.domain.model.JobStatus
import com.starhoop.hoopstar.ui.components.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobStatusScreen(
    onBack: () -> Unit,
    onGoToMapping: (jobId: Int, teamId: Int) -> Unit,
    viewModel: JobStatusViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("עיבוד משחק", style = MaterialTheme.typography.titleLarge) },
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
            when (val s = state.job) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                is UiState.Error -> ErrorState(s.message, onRetry = viewModel::refresh)
                is UiState.Success -> JobStatusContent(
                    job = s.data,
                    canceling = state.canceling,
                    onCancel = viewModel::cancel,
                    onGoToMapping = onGoToMapping
                )
                is UiState.Empty -> Unit
            }
        }
    }
}

@Composable
private fun JobStatusContent(
    job: Job,
    canceling: Boolean,
    onCancel: () -> Unit,
    onGoToMapping: (Int, Int) -> Unit
) {
    val progress = ((job.progressPercent ?: 0.0) / 100.0).toFloat().coerceIn(0f, 1f)
    val animated by animateFloatAsState(progress, tween(600), label = "progress")

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            when (job.status) {
                JobStatus.COMPLETED -> Icon(Icons.Default.CheckCircle, null,
                    Modifier.size(120.dp), tint = MaterialTheme.colorScheme.primary)
                JobStatus.FAILED -> Icon(Icons.Default.ErrorOutline, null,
                    Modifier.size(120.dp), tint = MaterialTheme.colorScheme.error)
                JobStatus.CANCELED -> Icon(Icons.Default.ErrorOutline, null,
                    Modifier.size(120.dp), tint = MaterialTheme.colorScheme.outline)
                else -> {
                    CircularProgressIndicator(
                        progress = { animated },
                        modifier = Modifier.size(200.dp),
                        strokeWidth = 10.dp,
                        strokeCap = StrokeCap.Round,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(animated * 100).toInt()}%",
                            fontSize = 40.sp, fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        StatusPill(job.status)
        Spacer(Modifier.height(8.dp))
        Text(job.sourceFilename ?: "משחק #${job.jobId}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface)

        Spacer(Modifier.height(28.dp))

        // מטריקות
        if (job.status == JobStatus.PROCESSING || job.status == JobStatus.PENDING || job.status == JobStatus.COMPLETED) {
            StatRow("פריימים", "${job.processedFrames ?: 0} / ${job.totalFrames ?: "?"}")
            StatRow("קצב עיבוד", job.throughputFps?.let { "%.1f FPS".format(it) } ?: "—")
            StatRow("משך עיבוד", formatDuration(job.processingDurationSec))
        }
        if (job.status == JobStatus.FAILED && job.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(job.errorMessage, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.weight(1f))

        when (job.status) {
            JobStatus.PENDING, JobStatus.PROCESSING -> OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !canceling
            ) {
                Text(if (canceling) "מבטל..." else "בטל עיבוד")
            }
            JobStatus.COMPLETED -> com.starhoop.hoopstar.ui.components.HoopPrimaryButton(
                text = "המשך למיפוי שחקנים",
                onClick = { onGoToMapping(job.jobId, job.teamId ?: -1) }
            )
            else -> Unit
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface)
    }
}