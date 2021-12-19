package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.VideosPagingAdapter
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.databinding.FragmentVideosBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.viewmodel.videosFrag.VideosViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    // 영상을 실행시킴
    private fun playVideo(video: Video) {
        if (args.typeImageRes == R.drawable.ic_youtube) {
            val packageName = Contents.YOUTUBE_PACKAGE_NAME // Youtube 패키지

            // 유튜브 앱이나 웹으로 이동
            OpenOtherApp(requireContext()).openYoutube(
                packageName = packageName,
                url = video.url
            )
        } else {
            // Twitch Video 패키지
            val packageName = Contents.TWITCH_VIDEO_PACKAGE_NAME + video.id

            // 트위치 앱이나 웹으로 이동
            OpenOtherApp(requireContext()).openTwitch(
                packageName = packageName,
                url = video.url,
            )
        }
    }
}