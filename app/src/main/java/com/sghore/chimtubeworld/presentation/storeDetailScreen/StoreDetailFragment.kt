package com.sghore.chimtubeworld.presentation.storeDetailScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.databinding.FragmentStoreDetailBinding
import com.sghore.chimtubeworld.other.OpenOtherApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoreDetailFragment(private val goods: Goods? = null) : Fragment() {

    @Inject
    lateinit var assistedFactory: StoreDetailViewModel.AssistedFactory
    private val mViewModel by viewModels<StoreDetailViewModel> {
        StoreDetailViewModel.provideFactory(
            assistedFactory,
            goods
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (goods == null) {
            return null
        }

        // 인스턴스 설정
        val binding = FragmentStoreDetailBinding.inflate(inflater)

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    StoreDetailScreen(
                        viewModel = mViewModel,
                        goods = goods,
                        onActionClick = {
                            OpenOtherApp(requireContext())
                                .openCustomTabs(it)
                        }
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}