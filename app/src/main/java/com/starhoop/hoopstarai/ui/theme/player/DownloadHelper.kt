package com.starhoop.hoopstar.ui.player

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object DownloadHelper {
    fun download(context: Context, url: String, fileName: String = "hoopstar_reel.mp4") {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("הורדת ריל היילייטים")
                .addRequestHeader("ngrok-skip-browser-warning", "true")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(context, "ההורדה החלה...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "ההורדה נכשלה: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}