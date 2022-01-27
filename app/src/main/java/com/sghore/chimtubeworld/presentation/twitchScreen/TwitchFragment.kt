package com.sghore.chimtubeworld.presentation.twitchScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.databinding.FragmentTwitchBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TwitchFragment : Fragment() {
    private val mViewModel by viewModels<TwitchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentTwitchBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    TwitchScreen(
                        viewModel = mViewModel,
                        onMainChannelClick = {
                            moveTwitchChannel(it!!)
                        },
                        onTwitchCrewChannelClick = {
                            moveFragmentToVideos(it!!)
                        }
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    // 트위치 채널로 이동
    private fun moveTwitchChannel(channelData: Channel) {
        OpenOtherApp(requireActivity()).openTwitch(
            packageName = Contents.TWITCH_CHANNEL_PACKAGE_NAME + channelData.name,
            url = channelData.url
        )
    }

    // 동영상 리스트 화면으로 이동
    private fun moveFragmentToVideos(channelData: Channel) {
        val directions = TwitchFragmentDirections
            .actionTwitchFragmentToVideosFragment(
                channelName = channelData.name,
                channelId = channelData.id,
                typeImageRes = R.drawable.twitch
            )
        findNavController().navigate(directions)
    }
}