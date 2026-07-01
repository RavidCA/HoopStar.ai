package com.starhoop.hoopstar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.starhoop.hoopstar.service.ReelGenerationService
import com.starhoop.hoopstar.ui.navigation.HoopStarNavHost
import com.starhoop.hoopstar.ui.navigation.Routes
import com.starhoop.hoopstar.ui.theme.HoopStarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingPlayUrl: String? = null
    private var pendingPlayerName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        readPlayExtras(intent)

        setContent {
            HoopStarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var handled by remember { mutableStateOf(false) }

                    HoopStarNavHost(navController = navController)

                    // אם נפתחנו מלחיצה על התראת "ריל מוכן" — ננווט לנגן
                    if (!handled && pendingPlayUrl != null) {
                        handled = true
                        val url = pendingPlayUrl!!
                        val name = pendingPlayerName ?: "Player"
                        navController.navigate(Routes.player(url, name))
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        readPlayExtras(intent)
        // אם האפליקציה כבר פתוחה, recreate כדי לטפל ב-extra החדש
        if (pendingPlayUrl != null) recreate()
    }

    private fun readPlayExtras(intent: Intent?) {
        pendingPlayUrl = intent?.getStringExtra(ReelGenerationService.EXTRA_PLAY_URL)
        pendingPlayerName = intent?.getStringExtra(ReelGenerationService.EXTRA_PLAYER_NAME)
    }
}