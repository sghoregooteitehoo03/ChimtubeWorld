package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.adapter.MainContentAdapter
import com.sghore.chimtubeworld.adapter.SubContentAdapter
import com.sghore.chimtubeworld.databinding.FragmentYoutubeBinding
import com.sghore.chimtubeworld.viewmodel.youtubeFrag.YoutubeViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . 리스트 스크롤 안되게
//  . 리스트 클릭 시 영상 보여주는 화면으로 넘어가기
//  . SubContent margin값 주기

@AndroidEntryPoint
class YoutubeFragment : Fragment() {
    private val mViewModel by viewModels<YoutubeViewModel>()

    private lateinit var mainContentAdapter: MainContentAdapter
    private lateinit var subContentAdapter: SubContentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentYoutubeBinding.inflate(inflater)
        mainContentAdapter = MainContentAdapter()
        subContentAdapter = SubContentAdapter()

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.mainList.adapter = mainContentAdapter
            this.subList.adapter = subContentAdapter

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver() // 옵저버 설정

        mViewModel.getChannelInfo()
    }

    // 옵저버 설정
    private fun setObserver() {
        mViewModel.allChannelList.observe(viewLifecycleOwner) { channelList ->
            if (channelList != null) {
                mainContentAdapter.syncData(channelList.filter { channel ->
                    channel.type == 0
                })
                subContentAdapter.syncData(channelList.filter { channel ->
                    channel.type == 1
                })
            }
        }
    }
}