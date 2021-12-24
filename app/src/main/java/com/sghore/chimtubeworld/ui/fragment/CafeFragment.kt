package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.CafeCategoryAdapter
import com.sghore.chimtubeworld.adapter.CafePostPagingAdapter
import com.sghore.chimtubeworld.databinding.FragmentCafeBinding
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.viewmodel.cafeFrag.CafeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CafeFragment : Fragment(), View.OnClickListener,
    CafeCategoryAdapter.CafeCategoryItemListener,
    CafePostPagingAdapter.CafePostItemListener {

    private val mViewModel by viewModels<CafeViewModel>()

    private lateinit var binding: FragmentCafeBinding
    private lateinit var categoryAdapter: CafeCategoryAdapter
    private lateinit var postAdapter: CafePostPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        binding = FragmentCafeBinding.inflate(inflater)
        categoryAdapter = CafeCategoryAdapter().apply {
            setOnItemListener(this@CafeFragment)
        }
        postAdapter = CafePostPagingAdapter().apply {
            addLoadStateListener { loadState ->
                binding.cafePostList.isVisible = loadState.source.refresh is LoadState.NotLoading
            }
            setOnItemListener(this@CafeFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@CafeFragment
            with(this.cafeCategoryList) {
                adapter = categoryAdapter
                itemAnimator = null
            }
            this.cafePostList.adapter = postAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 뷰 클릭 이벤트
    override fun onClick(view: View) {
        when (view.id) {
            R.id.cafe_banner_layout -> {
                val url = mViewModel.cafeInfoData.value?.url ?: ""
                OpenOtherApp(requireContext()).openCustomTabs(url)
            }
            else -> {}
        }
    }

    // 카테고리 클릭 이벤트
    override fun onCategoryClickListener(pos: Int) {
        mViewModel.selectedPos.value = pos
    }

    // 게시글 클릭 이벤트
    override fun onPostClickListener(pos: Int) {
        val postData = postAdapter.getPostData(pos)
        OpenOtherApp(requireContext())
            .openCustomTabs(postData?.url ?: "")
    }

    // 옵저버 설정
    private fun setObserver() {
        // 카테고리 선택한 위치
        mViewModel.selectedPos.observe(viewLifecycleOwner) { pos ->
            val categoryId = categoryAdapter.getCategoryId(pos)

            mViewModel.categoryId.value = categoryId
            categoryAdapter.selectCategory(pos)
        }
        // 카테고리 아이디
        mViewModel.categoryId.observe(viewLifecycleOwner) { categoryId ->
            postAdapter.refresh() // 카테고리 아이디에 따라 게시글을 새로 불러옴
            binding.cafePostList.scrollToPosition(0)
        }
        // 카페 정보
        mViewModel.cafeInfoData.observe(viewLifecycleOwner) { cafeInfo ->
            if (cafeInfo == null) { // 정보가 없을 때
                mViewModel.getCafeInfo()
            }
        }
        // 카페 게시글
        mViewModel.cafePosts.observe(viewLifecycleOwner) { pagingData ->
            postAdapter.submitData(lifecycle, pagingData)
        }
    }
}