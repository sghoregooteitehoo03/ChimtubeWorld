package com.sghore.chimtubeworld.presentation.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateListOf
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import dagger.hilt.android.AndroidEntryPoint
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val pendingShareUrls = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setSystemBarsColor(
            window = window,
            statusBarColor = getColor(DesignSystemR.color.white),
            navigationBarColor = getColor(DesignSystemR.color.white)
        )
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

    private fun setSystemBarsColor(window: Window, statusBarColor: Int, navigationBarColor: Int) {
        if (Build.VERSION.SDK_INT >= 35) {
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                val statusBarInsets = insets.getInsets(android.view.WindowInsets.Type.statusBars())
                val navBarInsets = insets.getInsets(WindowInsets.Type.navigationBars())

                // 전체 배경을 칠해서 system bar 영역까지 덮음
                view.setBackgroundColor(statusBarColor)

                // 상태바 & 네비게이션바 각각 높이/두께만큼 별도 뷰를 추가하는 방식도 가능
                view.setPadding(
                    0,
                    statusBarInsets.top,        // status bar 높이만큼 패딩
                    0,
                    navBarInsets.bottom         // navigation bar 높이만큼 패딩
                )
                insets
            }
        } else {
            // Android 14 이하에서는 기존 방식 사용
            window.statusBarColor = statusBarColor
            window.navigationBarColor = navigationBarColor
        }
    }
}
