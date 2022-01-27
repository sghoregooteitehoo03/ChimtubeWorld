package com.sghore.chimtubeworld.presentation.storeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.databinding.FragmentStoreBinding
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment() {
    private val mViewModel by viewModels<StoreViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()

    private lateinit var binding: FragmentStoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        binding = FragmentStoreBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    StoreScreen(
                        viewModel = mViewModel,
                        onCategoryClick = {
                            mViewModel.changeCategory(it)
                        },
                        onGoodsClick = { goodsList, goodsIndex ->
                            gViewModel.showGoodsList.value = goodsList
                            gViewModel.selectedGoodsPos.value = goodsIndex
                        }
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}