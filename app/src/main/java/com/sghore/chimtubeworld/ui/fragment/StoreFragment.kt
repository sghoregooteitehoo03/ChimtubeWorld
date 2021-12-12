package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.adapter.GoodsAdapter
import com.sghore.chimtubeworld.adapter.GoodsCategoryAdapter
import com.sghore.chimtubeworld.databinding.FragmentStoreBinding
import com.sghore.chimtubeworld.ui.custom.SpannableGridLayoutManager
import com.sghore.chimtubeworld.ui.custom.SpannableGridLayoutManager.SpanInfo
import com.sghore.chimtubeworld.viewmodel.storeFrag.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoreFragment : Fragment() {
    private val mViewModel by viewModels<StoreViewModel>()
    private lateinit var categoryAdapter: GoodsCategoryAdapter
    private lateinit var goodsAdapter: GoodsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentStoreBinding.inflate(inflater)
        val gridLayoutManager =
            SpannableGridLayoutManager(object : SpannableGridLayoutManager.GridSpanLookup {

                override fun getSpanInfo(position: Int): SpannableGridLayoutManager.SpanInfo {
                    return if (position == 0) {
                        SpanInfo(2, 2)
                        //this will count of row and column you want to replace
                    } else {
                        SpanInfo(1, 1)
                    }
                }
            }, 3, 1f)
        categoryAdapter = GoodsCategoryAdapter()
        goodsAdapter = GoodsAdapter()

        // 바인딩 설정
        with(binding) {
            this.goodsCategoryList.adapter = categoryAdapter
            this.goodsList.layoutManager = gridLayoutManager
            this.goodsList.adapter = goodsAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 옵저버 설정
    private fun setObserver() {
        mViewModel.storeImages.observe(viewLifecycleOwner) { images ->
            if (images.isEmpty()) {
                mViewModel.getStoreImages()
            } else {
                categoryAdapter.syncData(images)
            }
        }
        mViewModel.goodsList.observe(viewLifecycleOwner) { goodsList ->
            if (goodsList == null) {
                mViewModel.getGoodsList()
            } else {
                goodsAdapter.syncData(goodsList)
            }
        }
    }
}