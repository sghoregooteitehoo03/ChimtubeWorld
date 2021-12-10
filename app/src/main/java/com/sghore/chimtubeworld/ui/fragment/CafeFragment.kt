package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sghore.chimtubeworld.adapter.CafeCategoryAdapter
import com.sghore.chimtubeworld.adapter.CafePostPagingAdapter
import com.sghore.chimtubeworld.databinding.FragmentCafeBinding
import com.sghore.chimtubeworld.viewmodel.cafeFrag.CafeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CafeFragment : Fragment() {
    private val mViewModel by viewModels<CafeViewModel>()

    private lateinit var categoryAdapter: CafeCategoryAdapter
    private lateinit var postAdapter: CafePostPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentCafeBinding.inflate(inflater)
        categoryAdapter = CafeCategoryAdapter()
        postAdapter = CafePostPagingAdapter()

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.cafeCategoryList.adapter = categoryAdapter
            this.cafePostList.adapter = postAdapter

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
        // 카페 정보
        mViewModel.cafeInfoData.observe(viewLifecycleOwner) { cafeInfo ->
            if (cafeInfo == null) { // 정보가 없을 때
                mViewModel.getCafeInfo()
            }
        }
        // 카페 게시글
        lifecycleScope.launch {
            mViewModel.cafePosts.collect { pagingData ->
                postAdapter.submitData(pagingData)
            }
        }
//        mViewModel.cafePosts.observe(viewLifecycleOwner) { pagingData ->
//            lifecycleScope.launch {
//                postAdapter.submitData(pagingData)
//            }
//        }
    }
}