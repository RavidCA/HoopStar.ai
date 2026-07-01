package com.starhoop.hoopstar.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.starhoop.hoopstar.BuildConfig
import com.starhoop.hoopstar.R
import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstar.data.local.db.SavedReelEntity
import com.starhoop.hoopstar.data.repository.SavedReelsRepository
import com.starhoop.hoopstar.domain.usecase.ComposeReelUseCase
import com.starhoop.hoopstar.domain.usecase.ExtractReelUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReelGenerationService : Service() {

    @Inject lateinit var extractReel: ExtractReelUseCase
    @Inject lateinit var composeReel: ComposeReelUseCase
    @Inject lateinit var savedReels: SavedReelsRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val jobId = intent?.getIntExtra(EXTRA_JOB_ID, -1) ?: -1
        val teamId = intent?.getIntExtra(EXTRA_TEAM_ID, -1) ?: -1
        val playerId = intent?.getIntExtra(EXTRA_PLAYER_ID, -1) ?: -1
        val playerName = intent?.getStringExtra(EXTRA_PLAYER_NAME) ?: "Player"

        ReelNotifications.ensureChannels(this)
        startForeground(ReelNotifications.PROGRESS_NOTIF_ID, buildProgressNotification(playerName, "Extracting clips..."))

        scope.launch {
            runGeneration(jobId, teamId, playerId, playerName)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private suspend fun runGeneration(jobId: Int, teamId: Int, playerId: Int, playerName: String) {
        // שלב 1: extract
        when (val ex = extractReel(jobId, playerId)) {
            is DataResult.Success -> {
                updateProgress(playerName, "Composing reel...")
                // שלב 2: compose
                when (val co = composeReel(jobId, ex.data.reelId)) {
                    is DataResult.Success -> {
                        val rawUrl = co.data.downloadUrl ?: ex.data.downloadUrl
                        val url = normalizeUrl(rawUrl)
                        if (url != null) {
                            savedReels.save(
                                SavedReelEntity(
                                    playerId = playerId, playerName = playerName,
                                    teamId = teamId, jobId = jobId,
                                    reelId = ex.data.reelId,
                                    composedReelId = co.data.composedReelId,
                                    downloadUrl = url,
                                    clipCount = ex.data.clipCount,
                                    durationSec = co.data.totalDurationSec ?: ex.data.totalDurationSec
                                )
                            )
                            showDone(playerName, url)
                        } else {
                            showFailed(playerName, "No video URL returned")
                        }
                    }
                    is DataResult.Error -> showFailed(playerName, co.message)
                }
            }
            is DataResult.Error -> {
                if (ex.code == 422) showNoClips(playerName)
                else showFailed(playerName, ex.message)
            }
        }
    }

    private fun normalizeUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return if (url.startsWith("http")) url
        else BuildConfig.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
    }

    // --- התראות ---

    private fun buildProgressNotification(playerName: String, text: String): Notification =
        NotificationCompat.Builder(this, ReelNotifications.CHANNEL_PROGRESS)
            .setContentTitle("Generating reel for $playerName")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setProgress(0, 0, true) // אינדטרמיננטי
            .setContentIntent(openAppIntent())
            .build()

    private fun updateProgress(playerName: String, text: String) {
        if (!canNotify()) return
        NotificationManagerCompat.from(this)
            .notify(ReelNotifications.PROGRESS_NOTIF_ID, buildProgressNotification(playerName, text))
    }

    private fun showDone(playerName: String, url: String) {
        val notif = NotificationCompat.Builder(this, ReelNotifications.CHANNEL_DONE)
            .setContentTitle("Reel ready! \uD83C\uDFC0")
            .setContentText("$playerName's highlight reel is ready to watch")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(playReelIntent(url, playerName))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        if (canNotify()) NotificationManagerCompat.from(this).notify(ReelNotifications.DONE_NOTIF_ID, notif)
    }

    private fun showNoClips(playerName: String) {
        val notif = NotificationCompat.Builder(this, ReelNotifications.CHANNEL_DONE)
            .setContentTitle("No highlights found")
            .setContentText("The AI didn't find standout moments for $playerName")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent())
            .build()
        if (canNotify()) NotificationManagerCompat.from(this).notify(ReelNotifications.DONE_NOTIF_ID, notif)
    }

    private fun showFailed(playerName: String, reason: String) {
        val notif = NotificationCompat.Builder(this, ReelNotifications.CHANNEL_DONE)
            .setContentTitle("Reel generation failed")
            .setContentText("$playerName: $reason")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent())
            .build()
        if (canNotify()) NotificationManagerCompat.from(this).notify(ReelNotifications.DONE_NOTIF_ID, notif)
    }

    // --- Intents ---

    private fun openAppIntent(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return PendingIntent.getActivity(this, 0, intent, pendingFlags())
    }

    private fun playReelIntent(url: String, playerName: String): PendingIntent {
        // פותח את MainActivity עם deep-link לנגן
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            putExtra(EXTRA_PLAY_URL, url)
            putExtra(EXTRA_PLAYER_NAME, playerName)
        }
        return PendingIntent.getActivity(this, 1, intent, pendingFlags())
    }

    private fun pendingFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
    }

    private fun canNotify(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    override fun onDestroy() {
        super.onDestroy()
        // מנקים את הקורוטינות
        try { scope.coroutineContext[kotlinx.coroutines.Job]?.cancel() } catch (_: Exception) {}
    }

    companion object {
        const val EXTRA_JOB_ID = "job_id"
        const val EXTRA_TEAM_ID = "team_id"
        const val EXTRA_PLAYER_ID = "player_id"
        const val EXTRA_PLAYER_NAME = "player_name"
        const val EXTRA_PLAY_URL = "play_url"

        fun start(context: Context, jobId: Int, teamId: Int, playerId: Int, playerName: String) {
            val intent = Intent(context, ReelGenerationService::class.java).apply {
                putExtra(EXTRA_JOB_ID, jobId)
                putExtra(EXTRA_TEAM_ID, teamId)
                putExtra(EXTRA_PLAYER_ID, playerId)
                putExtra(EXTRA_PLAYER_NAME, playerName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}