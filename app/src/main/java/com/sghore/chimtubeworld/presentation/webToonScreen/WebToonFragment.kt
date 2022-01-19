package com.sghore.chimtubeworld.presentation.webToonScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.databinding.FragmentWebtoonBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebToonFragment : Fragment() {
    private val mViewModel by viewModels<WebToonViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentWebtoonBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    WebToonScreen(
                        viewModel = mViewModel,
                        onWebToonClick = {
                            OpenOtherApp(requireContext()).openNaverWebToon(
                                Contents.NAVER_WEBTOON_PACKAGE_NAME + it.id,
                                it.url
                            )
                        }
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}