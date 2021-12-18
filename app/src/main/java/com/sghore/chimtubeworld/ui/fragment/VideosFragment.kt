package com.sghore.chimtubeworld.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.VideosPagingAdapter
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.databinding.FragmentVideosBinding
import com.sghore.chimtubeworld.viewmodel.videosFrag.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


// TODO:
//  . 조회수, 업로드 일 텍스트 O
//  . 클릭 시 유튜브 이동 O
@AndroidEntryPoint
class VideosFragment : Fragment(), VideosPagingAdapter.VideosItemListener {
    @Inject
    lateinit var assistedFactory: VideosViewModel.AssistedFactory
    private lateinit var videosAdapter: VideosPagingAdapter

    private val args by navArgs<VideosFragmentArgs>()
    private val mViewModel by viewModels<VideosViewModel> {
        VideosViewModel.provideFactory(
            assistedFactory,
            channelId = args.channelId,
            typeImageRes = args.typeImageRes
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentVideosBinding.inflate(inflater)
        videosAdapter = VideosPagingAdapter().apply {
            setOnItemListener(this@VideosFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.subtitleMainText.text = args.channelName
            this.videoTypeImage.setImageResource(args.typeImageRes)
            this.videoList.adapter = videosAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 비디오 아이템 클릭
    override fun onVideoClickListener(pos: Int) {
        val videoData = videosAdapter.getVideoData(pos)!!
        playVideo(videoData)
    }

    // 옵저버 설정
    private fun setObserver() {
        mViewModel.vidoes.observe(viewLifecycleOwner) { pagingData ->
            videosAdapter.submitData(lifecycle, pagingData)
        }
    }

    // 영상 화면으로 이동
    private fun playVideo(video: Video) {
        val packageName = if (args.typeImageRes == R.drawable.ic_youtube) {
            "com.google.android.youtube" // Youtube 패키지
        } else {
            "tv.twitch.android.viewer" // Twitch 패키지
        }

        // 해당 패키지가 휴대폰에 설치되어 있을 때
        if (isPackageInstalled(packageName)) {
            // 앱으로 영상 실행
            requireActivity().startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(video.url))
                    .setPackage(packageName)
            )
        } else { // 해당 패키지가 휴대폰에 설치되어 없을 때
            CustomTabsIntent.Builder().let {
                ResourcesCompat.getDrawable(resources, R.drawable.ic_back, null)?.toBitmap()
                    ?.let { bitmap ->
                        it.setCloseButtonIcon(bitmap)
                    }

                it.build().launchUrl(requireContext(), Uri.parse(video.url))
            }
        }
    }

    // 패키지 존재여부 확인
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            requireActivity().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: NameNotFoundException) {
            false
        }
    }
}