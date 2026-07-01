package com.starhoop.hoopstar.ui.highlights

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ShareHelper {
    /**
     * משתף קישור לריל בוואטסאפ. אם וואטסאפ לא מותקן — נופל ל-share chooser כללי.
     */
    fun shareToWhatsApp(context: Context, reelUrl: String, playerName: String) {
        val message = "Check out $playerName's highlight reel \uD83C\uDFC0\n$reelUrl"
        val whatsapp = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            context.startActivity(whatsapp)
        } catch (e: Exception) {
            // וואטסאפ לא מותקן — share כללי
            val fallback = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            try {
                context.startActivity(Intent.createChooser(fallback, "Share reel"))
            } catch (e2: Exception) {
                Toast.makeText(context, "Could not share: ${e2.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}