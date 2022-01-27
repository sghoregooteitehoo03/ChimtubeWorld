package com.sghore.chimtubeworld.presentation.cafeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.databinding.FragmentCafeBinding
import com.sghore.chimtubeworld.other.OpenOtherApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CafeFragment : Fragment() {
    private val mViewModel by viewModels<CafeViewModel>()
    private lateinit var binding: FragmentCafeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        binding = FragmentCafeBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    CafeScreen(
                        viewModel = mViewModel,
                        onCafeBannerClick = {
                            OpenOtherApp(requireContext()).openCustomTabs(it!!.url)
                        },
                        onCafeCategoryClick = {
                            mViewModel.changeCategory(it.categoryId)
                        },
                        onCafePostClick = {
                            mViewModel.readPost(it!!.id)
                            OpenOtherApp(requireContext()).openCustomTabs(it.url ?: "")
                        }
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}