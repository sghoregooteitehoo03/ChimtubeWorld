package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.sghore.chimtubeworld.adapter.GoodsAdapter
import com.sghore.chimtubeworld.adapter.GoodsCategoryAdapter
import com.sghore.chimtubeworld.databinding.FragmentStoreBinding
import com.sghore.chimtubeworld.ui.custom.GridItemDecoration
import com.sghore.chimtubeworld.viewmodel.GlobalViewModel
import com.sghore.chimtubeworld.viewmodel.storeFrag.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment(),
    GoodsCategoryAdapter.GoodsCategoryItemListener,
    GoodsAdapter.GoodsItemListener {
    private val mViewModel by viewModels<StoreViewModel>()
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val spanCount = 3

    private lateinit var binding: FragmentStoreBinding
    private lateinit var categoryAdapter: GoodsCategoryAdapter
    private lateinit var goodsAdapter: GoodsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        binding = FragmentStoreBinding.inflate(inflater)
        categoryAdapter = GoodsCategoryAdapter().apply {
            setOnItemListener(this@StoreFragment)
        }
        goodsAdapter = GoodsAdapter().apply {
            setOnItemListener(this@StoreFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            with(this.goodsCategoryList) {
                adapter = categoryAdapter
                itemAnimator = null
            }
            with(this.goodsList) {
                adapter = goodsAdapter
                layoutManager = GridLayoutManager(requireContext(), spanCount)
                addItemDecoration(GridItemDecoration(context, spanCount, 4))
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 카테고리 클릭이벤트
    override fun onCategoryClickListener(pos: Int) {
        if (categoryAdapter.previousSelectedPos != pos) {
            // 선택된 아이템이 이전에 선택한 아이템이랑 다를때에만 동작
            mViewModel.selectedPos.value = pos
        }
    }

    // 굿즈 상품 클릭 이벤트
    override fun onGoodsClickListener(pos: Int) {
        gViewModel.showGoodsList.value = mViewModel.goodsList.value
        gViewModel.selectedGoodsPos.value = pos
    }

    // 옵저버 설정
    private fun setObserver() {
        // 스토어 카테고리
        mViewModel.storeImages.observe(viewLifecycleOwner) { images ->
            if (images.isEmpty()) { // 스토어 정보가 없을 때
                mViewModel.getStoreImages()
            } else {
                categoryAdapter.syncData(images)
                mViewModel.selectedPos.value = mViewModel.selectedPos.value //  옵저버 트리거
            }
        }
        // 카테고리 선택된 위치
        mViewModel.selectedPos.observe(viewLifecycleOwner) { pos ->
            if (!categoryAdapter.isEmptyList()) {
                val channelData = categoryAdapter.getItem(pos)

                mViewModel.currentUrl.value = channelData.url
                categoryAdapter.selectCategory(pos)
            }
        }
        // 읽어올 사이트의 url
        mViewModel.currentUrl.observe(viewLifecycleOwner) { url ->
            if (url.isNotEmpty()) {
                mViewModel.getGoodsList(url)
            }
        }
        // 굿즈 리스트
        mViewModel.goodsList.observe(viewLifecycleOwner) { goodsList ->
            if (goodsList != null) {
                goodsAdapter.syncData(goodsList)
            }
        }
    }
}