package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.adapter.WebToonAdapter
import com.sghore.chimtubeworld.databinding.FragmentWebtoonBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.ui.custom.GridItemDecoration
import com.sghore.chimtubeworld.viewmodel.webToonFrag.WebToonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebToonFragment : Fragment(), WebToonAdapter.WebToonItemListener {
    private val mViewModel by viewModels<WebToonViewModel>()

    private lateinit var webToonAdapter: WebToonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentWebtoonBinding.inflate(inflater)
        webToonAdapter = WebToonAdapter().apply {
            setOnItemListener(this@WebToonFragment)
        }

        // 바인딩 설정
        with(binding) {
            with(this.webtoonList) {
                adapter = webToonAdapter
                addItemDecoration(GridItemDecoration(context))
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 웹툰 클릭 시
    override fun onItemClickListener(pos: Int) {
        val webToonData = webToonAdapter.getItem(pos)
        OpenOtherApp(requireContext()).openNaverWebToon(
            Contents.NAVER_WEBTOON_PACKAGE_NAME + webToonData.id,
            webToonData.url
        )
    }

    // 옵저버 설정
    private fun setObserver() {
        mViewModel.webToonList.observe(viewLifecycleOwner) { toonList ->
            if (toonList != null) {
                webToonAdapter.syncData(toonList)
            } else {
                mViewModel.getWebToonList()
            }
        }
    }
}