package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.adapter.WebToonAdapter
import com.sghore.chimtubeworld.databinding.FragmentWebtoonBinding
import com.sghore.chimtubeworld.viewmodel.webToonFrag.WebToonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebToonFragment : Fragment() {
    private val mViewModel by viewModels<WebToonViewModel>()

    private lateinit var webToonAdapter: WebToonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentWebtoonBinding.inflate(inflater)
        webToonAdapter = WebToonAdapter()

        // 바인딩 설정
        with(binding) {
            this.webtoonList.adapter = webToonAdapter

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
        mViewModel.webToonList.observe(viewLifecycleOwner) { toonList ->
            if (toonList != null) {
                webToonAdapter.syncData(toonList)
            } else {
                mViewModel.getWebToonList()
            }
        }
    }
}