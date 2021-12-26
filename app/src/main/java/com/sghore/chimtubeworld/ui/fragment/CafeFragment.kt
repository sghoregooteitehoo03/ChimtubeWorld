package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.CafeCategoryAdapter
import com.sghore.chimtubeworld.adapter.CafePostPagingAdapter
import com.sghore.chimtubeworld.databinding.FragmentCafeBinding
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.ui.custom.LinearItemDecoration
import com.sghore.chimtubeworld.viewmodel.cafeFrag.CafeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CafeFragment : Fragment(), View.OnClickListener,
    CafeCategoryAdapter.CafeCategoryItemListener,
    CafePostPagingAdapter.CafePostItemListener {

    private val mViewModel by viewModels<CafeViewModel>()
    private var isExpanded = true

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
                addItemDecoration(
                    LinearItemDecoration(requireContext(), 12)
                )
            }
            with(this.cafePostList) {
                adapter = postAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        // 프래그먼트를 구성할 때 리스트의 위치가 0이 아니면 카페 배너 레이아웃을 접음
                        if (computeVerticalScrollOffset() != 0 && isExpanded) {
                            isExpanded = false
                            binding.topBannerLayout.setExpanded(isExpanded, false)
                        }
                    }
                })
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    override fun onDestroyView() {
        // 프래그먼트가 삭제되기 전에 카테고리 리스트 상태를 저장시킴
        mViewModel.categoryListState.value =
            binding.cafeCategoryList.layoutManager?.onSaveInstanceState()
        super.onDestroyView()
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
        // 선택한 위치가 이전에 선택한 위치랑 다를 때
        if (pos != categoryAdapter.previousSelectedPos) {
            mViewModel.selectedPos.value = pos
        }
    }

    // 게시글 클릭 이벤트
    override fun onPostClickListener(pos: Int) {
        val postData = postAdapter.getPostData(pos)
        OpenOtherApp(requireContext())
            .openCustomTabs(postData?.url ?: "")
    }

    // 옵저버 설정
    private fun setObserver() {
        // 카테고리 리스트 복원 데이터
        mViewModel.categoryListState.observe(viewLifecycleOwner) { saveState ->
            if (saveState != null) {
                mViewModel.categoryListState.value = null
            }
        }
        // 카테고리 선택한 위치
        mViewModel.selectedPos.observe(viewLifecycleOwner) { pos ->
            val categoryId = categoryAdapter.getCategoryId(pos)

            mViewModel.categoryId.value = categoryId
            categoryAdapter.selectCategory(pos)
        }
        // 카테고리 아이디
        mViewModel.categoryId.observe(viewLifecycleOwner) { categoryId ->
            postAdapter.refresh() // 카테고리 아이디에 따라 게시글을 새로 불러옴
            if (postAdapter.itemCount != 0) {
                // 카테고리 정보가 바뀌었을 때 리스트에 위치를 맨 위로 만듬
                postAdapter.notifyDataSetChanged()
                binding.cafePostList.scrollToPosition(0)
            }
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