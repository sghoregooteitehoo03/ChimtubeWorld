package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.MainContentAdapter
import com.sghore.chimtubeworld.adapter.SubContentAdapter
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.FragmentYoutubeBinding
import com.sghore.chimtubeworld.ui.custom.GridItemDecoration
import com.sghore.chimtubeworld.viewmodel.youtubeFrag.YoutubeViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . 채널 가져오는 기능 수정하기 O

@AndroidEntryPoint
class YoutubeFragment : Fragment(), MainContentAdapter.MainContentItemListener,
    SubContentAdapter.SubContentItemListener {

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
        mainContentAdapter = MainContentAdapter().apply {
            setOnItemListener(this@YoutubeFragment)
        }
        subContentAdapter = SubContentAdapter().apply {
            setOnItemListener(this@YoutubeFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.mainList.adapter = mainContentAdapter
            with(this.subList) {
                adapter = subContentAdapter
                addItemDecoration(GridItemDecoration(context))
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver() // 옵저버 설정
    }

    // 침투부 채널 리스트 클릭
    override fun onMainItemClickListener(pos: Int) {
        val channelData = mainContentAdapter.getItem(pos)!!
        moveFragmentToVideos(channelData)
    }

    // 침착맨 외부 방송 리스트 클릭
    override fun onSubItemClickListener(pos: Int) {
        val channelData = subContentAdapter.getItem(pos)!!
        moveFragmentToVideos(channelData)
    }

    // 옵저버 설정
    private fun setObserver() {
        mViewModel.allChannelList.observe(viewLifecycleOwner) { channelList ->
            if (channelList != null) {
                mainContentAdapter.syncData(channelList.filter { channel ->
                    channel?.type == 0
                })
                subContentAdapter.syncData(channelList.filter { channel ->
                    channel?.type == 1
                })
            } else { // 리스트가 없으면 로드함
                mViewModel.getChannelInfo()
            }
        }
    }

    private fun moveFragmentToVideos(channelData: Channel) {
        val directions = YoutubeFragmentDirections
            .actionYoutubeFragmentToVideosFragment(
                channelName = channelData.name,
                channelId = channelData.id.split("|")[1],
                typeImageRes = R.drawable.ic_youtube
            )
        findNavController().navigate(directions)
    }
}