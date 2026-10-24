package com.sghore.chimtubeworld.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateListOf
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val pendingShareUrls = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            receiveShare(intent)
        } else {
            pendingShareUrls.addAll(savedInstanceState.getStringArrayList(PENDING_SHARES).orEmpty())
        }
        setContent {
            ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
                ChimtubeWorldApp(
                    pendingShareUrls = pendingShareUrls.toList(),
                    onShareHandled = { pendingShareUrls.removeAt(0) }
                )
            }
        }
    }

    // The one-argument callback receives singleTask deliveries on every supported API level.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        receiveShare(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList(PENDING_SHARES, ArrayList(pendingShareUrls))
        super.onSaveInstanceState(outState)
    }

    private fun receiveShare(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.takeIf { it.isNotEmpty() }?.let {
                pendingShareUrls.add(it)
            }
        }
        setIntent(null)
    }

    private companion object {
        const val PENDING_SHARES = "pending_share_urls"
    }
}
