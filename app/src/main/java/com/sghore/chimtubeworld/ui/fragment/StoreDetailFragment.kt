package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.PreviewImageAdapter
import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.databinding.FragmentStoreDetailBinding
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.viewmodel.storeDetailFrag.StoreDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreDetailFragment(private val goods: Goods? = null) : Fragment(),
    View.OnClickListener,
    PreviewImageAdapter.PreviewImageItemListener {
    private val mViewModel by viewModels<StoreDetailViewModel>()
    private lateinit var previewImageAdapter: PreviewImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(goods == null) {
            return null
        }

        // 인스턴스 설정
        val binding = FragmentStoreDetailBinding.inflate(inflater)
        previewImageAdapter = PreviewImageAdapter().apply {
            setOnItemListener(this@StoreDetailFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.goodsData = goods
            this.clickListener = this@StoreDetailFragment
            with(this.previewImageList) {
                adapter = previewImageAdapter
                itemAnimator = null
            }

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
            R.id.open_text -> { // 보러 가기 텍스트
                OpenOtherApp(requireContext())
                    .openCustomTabs(
                        goods!!.url
                    )
            }
        }
    }

    // 프리뷰 이미지 아이템 클릭이벤트
    override fun onImageClickListener(pos: Int) {
        if (previewImageAdapter.previousSelectedPos != pos) {
            // 이전에 선택한것과 다를때에만 반응
            mViewModel.selectedPos.value = pos
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 프리뷰 이미지
        mViewModel.previewImages.observe(viewLifecycleOwner) { images ->
            if (images != null) {
                previewImageAdapter.syncData(images)
                mViewModel.selectedPos.value = 0
            } else {
                mViewModel.getStoreDetail(goods!!)
            }
        }
        // 선택된 위치
        mViewModel.selectedPos.observe(viewLifecycleOwner) { pos ->
            if (pos != -1) {
                val image = previewImageAdapter.getItem(pos)

                mViewModel.selectedImage.value = image
                previewImageAdapter.selectedImage(pos)
            }
        }
    }
}