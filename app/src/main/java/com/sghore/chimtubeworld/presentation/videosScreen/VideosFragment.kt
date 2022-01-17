package com.sghore.chimtubeworld.presentation.videosScreen

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.VideosPagingAdapter
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.databinding.FragmentVideosBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.presentation.selectBookmarkScreen.SelectBookmarkDialog
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideosFragment : Fragment(), VideosPagingAdapter.VideosItemListener, View.OnClickListener {
    @Inject
    lateinit var assistedFactory: VideosViewModel.AssistedFactory
    private lateinit var binding: FragmentVideosBinding
    private lateinit var videosAdapter: VideosPagingAdapter

    private val args by navArgs<VideosFragmentArgs>()
    private val gViewModel by activityViewModels<GlobalViewModel>()
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
        binding = FragmentVideosBinding.inflate(inflater)
        videosAdapter = VideosPagingAdapter().apply {
            setOnItemListener(this@VideosFragment)
            addLoadStateListener { loadState ->
                mViewModel.isLoading.value = loadState.source.refresh is LoadState.Loading
            }
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@VideosFragment

            this.subtitleMainText.text = args.channelName
            this.videoTypeImage.setImageResource(args.typeImageRes)
            with(this.videoList) {
                adapter = videosAdapter
                setHasFixedSize(true)
            }

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

    // 북마크 아이템 클릭
    override fun onBookmarkClickListener(videoPos: Int, bookmarkPos: Int) {
        val directions = VideosFragmentDirections.actionVideosFragmentToEditBookmarkFragment(
            args.typeImageRes,
            bookmarkPos
        )

        gViewModel.videoData.value = videosAdapter.getVideoData(videoPos)
        findNavController().navigate(directions)
    }

    // 뷰 클릭 이벤트
    override fun onClick(view: View) {
        when (view.id) {
            R.id.help_btn -> {
                val tooltip = Balloon.Builder(requireContext())
                    .setPaddingLeft(10)
                    .setPaddingRight(10)
                    .setPaddingTop(6)
                    .setPaddingBottom(6)
                    .setCornerRadius(8f)
                    .setTextSize(14f)
                    .setTextGravity(Gravity.START)
                    .setBackgroundColor(Color.parseColor("#588CE1"))
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setText(getString(R.string.video_tip))
                    .build()

                tooltip.showAlignBottom(binding.helpBtn)
            }
            else -> {}
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        mViewModel.vidoes.observe(viewLifecycleOwner) { pagingData ->
            videosAdapter.submitData(lifecycle, pagingData)
        }
        gViewModel.refreshList.observe(viewLifecycleOwner) { isRefresh ->
            if (isRefresh) {
                videosAdapter.refresh()
                gViewModel.refreshList.value = false
            }
        }
    }

    // 영상을 실행시킴
    private fun playVideo(video: Video) {
        val packageName = if (args.typeImageRes == R.drawable.ic_youtube) {
            // Youtube 패키지
            val youtubePackage = Contents.YOUTUBE_PACKAGE_NAME
            if (video.bookmarks.isEmpty()) {
                // 북마크가 없으면 바로 실행
                OpenOtherApp(requireContext())
                    .openYoutube(youtubePackage, video.url)
            }

            youtubePackage
        } else {
            // Twitch Video 패키지
            val twitchPackage = Contents.TWITCH_VIDEO_PACKAGE_NAME + video.id
            if (video.bookmarks.isEmpty()) {
                // 북마크가 없으면 바로 실행
                OpenOtherApp(requireContext())
                    .openTwitch(twitchPackage, video.url)
            }

            twitchPackage
        }

        if (video.bookmarks.isNotEmpty()) {
            // 북마크가 존재하면 선택 화면 표시
            SelectBookmarkDialog(packageName, video).show(
                requireActivity().supportFragmentManager,
                "SelectPositionDialog"
            )
        }
    }
}