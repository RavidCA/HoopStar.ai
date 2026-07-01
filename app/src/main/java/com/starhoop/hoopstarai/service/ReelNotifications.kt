package com.starhoop.hoopstar.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object ReelNotifications {
    const val CHANNEL_PROGRESS = "reel_progress"
    const val CHANNEL_DONE = "reel_done"

    const val PROGRESS_NOTIF_ID = 4201
    const val DONE_NOTIF_ID = 4202

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val progress = NotificationChannel(
                CHANNEL_PROGRESS, "Reel Generation",
                NotificationManager.IMPORTANCE_LOW // בלי צליל בזמן עבודה
            ).apply { description = "Shows progress while generating a highlight reel" }

            val done = NotificationChannel(
                CHANNEL_DONE, "Reel Ready",
                NotificationManager.IMPORTANCE_HIGH // צליל + הקפצה כשמוכן
            ).apply { description = "Notifies when a highlight reel is ready" }

            mgr.createNotificationChannel(progress)
            mgr.createNotificationChannel(done)
        }
    }
}