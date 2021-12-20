package com.sghore.chimtubeworld.other

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.sghore.chimtubeworld.R

class OpenOtherApp(
    private val context: Context
) {

    // 유튜브 열기
    fun openYoutube(packageName: String, url: String) {
        // 해당 패키지가 휴대폰에 설치되어 있을 때
        if (isPackageInstalled(packageName)) {
            // 앱으로 영상 실행
            context.startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(url))
                    .setPackage(packageName)
            )
        } else { // 해당 패키지가 휴대폰에 설치되어 있지 않을 때
            openCustomTabs(url)
        }
    }

    fun openTwitch(packageName: String, url: String) {
        // 해당 패키지가 휴대폰에 설치되어 있을 때
        if (isPackageInstalled(packageName)) {
            // 앱으로 영상 실행
            context.startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setPackage(packageName)
            )
        } else { // 해당 패키지가 휴대폰에 있지 않을 때
            openCustomTabs(url)
        }
    }

    fun openNaverWebToon(packageName: String, url: String) {
        // 해당 패키지가 휴대폰에 설치되어 있을 때
        if (isPackageInstalled(packageName)) {
            // 네이버 웹툰 실행
            context.startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setPackage(packageName)
            )
        } else { // 해당 패키지가 휴대폰에 설치되어 있지 않을 때
            openCustomTabs(url)
        }
    }

    fun openCustomTabs(url: String) {
        CustomTabsIntent.Builder().let {
            ResourcesCompat.getDrawable(context.resources, R.drawable.ic_back, null)
                ?.toBitmap()
                ?.let { bitmap ->
                    it.setCloseButtonIcon(bitmap)
                }

            it.build().launchUrl(context, Uri.parse(url))
        }
    }

    // 패키지 존재여부 확인
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}