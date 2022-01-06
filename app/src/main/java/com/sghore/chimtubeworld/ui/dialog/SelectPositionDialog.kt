package com.sghore.chimtubeworld.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sghore.chimtubeworld.adapter.VideoPositionAdapter
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.databinding.DialogSelectPositionBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectPositionDialog(
    private val packageName: String? = null,
    private val videoData: Video? = null
) : DialogFragment(), VideoPositionAdapter.VideoPositionItemListener {
    private lateinit var videoPositionAdapter: VideoPositionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 데이터가 유효하지 않을 때
        if (packageName == null || videoData == null) {
            dismiss()
            return null
        }

        // 인스턴스 설정
        val binding = DialogSelectPositionBinding.inflate(inflater)
        videoPositionAdapter = VideoPositionAdapter(
            videoData.bookmarks
                .toMutableList()
        ).apply {
            setOnItemListener(this@SelectPositionDialog)
        }

        // 바인딩 설정
        with(binding) {
            this.videoPositionList.adapter = videoPositionAdapter
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    // 아이템 클릭 이벤트
    override fun onItemClickListener(pos: Int) {
        val bookmark = videoPositionAdapter.getItem(pos)
        val seconds = getSecondsFromPosition(bookmark?.videoPosition ?: -32400000)
        val url = getUrlWithSeconds(seconds)

        openApplication(url)
        dismiss()
    }

    // 다른 어플리케이션으로 영상을 실행
    private fun openApplication(url: String) {
        if (packageName!! == Contents.YOUTUBE_PACKAGE_NAME) {
            OpenOtherApp(requireContext()).openYoutube(
                packageName,
                url
            )
        } else {
            OpenOtherApp(requireContext()).openTwitch(
                packageName,
                url
            )
        }
    }

    // 시간이 포함된 url을 반환
    private fun getUrlWithSeconds(seconds: String): String {
        val url = videoData!!.url

        return if (url.contains("?")) {
            // 쿼리문이 존재할 때
            "${url}&t=$seconds"
        } else {
            // 쿼리문이 존재하지 않을 때
            "${url}?t=$seconds"
        }
    }

    // 시간 데이터를 초로 변환
    private fun getSecondsFromPosition(videoPosition: Long) =
        "${(videoPosition + 32400000) / 1000}s"
}