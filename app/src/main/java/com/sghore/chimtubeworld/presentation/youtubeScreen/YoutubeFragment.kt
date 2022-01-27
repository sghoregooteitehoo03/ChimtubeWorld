package com.sghore.chimtubeworld.presentation.youtubeScreen

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
import com.sghore.chimtubeworld.databinding.FragmentYoutubeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YoutubeFragment : Fragment() {
    private val mViewModel by viewModels<YoutubeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentYoutubeBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    YoutubeScreen(
                        viewModel = mViewModel,
                        onClick = {
                            moveFragmentToVideos(it!!)
                        }
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    private fun moveFragmentToVideos(channelData: Channel) {
        val directions = YoutubeFragmentDirections
            .actionYoutubeFragmentToVideosFragment(
                channelName = channelData.name,
                channelId = channelData.id.split("|")[1],
                typeImageRes = R.drawable.youtube
            )
        findNavController().navigate(directions)
    }
}