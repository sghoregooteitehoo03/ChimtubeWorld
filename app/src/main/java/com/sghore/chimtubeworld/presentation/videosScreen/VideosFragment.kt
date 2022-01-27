package com.sghore.chimtubeworld.presentation.videosScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.databinding.FragmentVideosBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.presentation.selectBookmarkScreen.SelectBookmarkDialog
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideosFragment : Fragment() {
    @Inject
    lateinit var assistedFactory: VideosViewModel.AssistedFactory
    private lateinit var binding: FragmentVideosBinding

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

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    VideosScreen(
                        viewModel = mViewModel,
                        gViewModel = gViewModel,
                        channelName = args.channelName,
                        videoTypeImage = args.typeImageRes,
                        onVideoClick = ::playVideo,
                        onBookmarkClick = ::clickBookmark
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    // 영상을 실행시킴
    private fun playVideo(video: Video) {
        val packageName = if (args.typeImageRes == R.drawable.youtube) {
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

    // 북마크 클릭
    private fun clickBookmark(video: Video, bookmarkPos: Int) {
        val directions = VideosFragmentDirections.actionVideosFragmentToEditBookmarkFragment(
            args.typeImageRes,
            bookmarkPos
        )

        gViewModel.videoData.value = video
        findNavController().navigate(directions)
    }
}