package com.sghore.chimtubeworld.presentation.twitchScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.TwitchUserAdapter
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.databinding.FragmentTwitchBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.presentation.ui.custom.GridItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TwitchFragment : Fragment(), View.OnClickListener, TwitchUserAdapter.TwitchUserItemListener {
    private val mViewModel by viewModels<TwitchViewModel>()
    private val spanCount = 4

    private lateinit var twitchUserAdapter: TwitchUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentTwitchBinding.inflate(inflater)
        twitchUserAdapter = TwitchUserAdapter().apply {
            setOnItemListener(this@TwitchFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@TwitchFragment
            with(this.twitchUserList) {
                adapter = twitchUserAdapter
                layoutManager = GridLayoutManager(requireContext(), spanCount)
                addItemDecoration(GridItemDecoration(context, spanCount, 12))
                setHasFixedSize(true)
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
            R.id.twitch_channel_layout -> {
                moveTwitchChannel()
            }
            else -> {}
        }
    }

    // 유저 리스트 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        val channelData = twitchUserAdapter.getItem(pos)!!
        moveFragmentToVideos(channelData)
    }

    // 옵저버 설정
    private fun setObserver() {
        // 유저 리스트
        mViewModel.twitchUserList.observe(viewLifecycleOwner) { userList ->
            if (userList != null) {
                twitchUserAdapter.syncData(userList)
            } else { // 리스트가 없으면 로드함
                mViewModel.getTwitchUserInfo()
            }
        }
    }

    // 트위치 채널로 이동
    private fun moveTwitchChannel() {
        val channelData = mViewModel.mainChannelData.value

        OpenOtherApp(requireActivity()).openTwitch(
            packageName = Contents.TWITCH_CHANNEL_PACKAGE_NAME + channelData?.name,
            url = channelData?.url ?: ""
        )
    }

    // 동영상 리스트 화면으로 이동
    private fun moveFragmentToVideos(channelData: Channel) {
        val directions = TwitchFragmentDirections
            .actionTwitchFragmentToVideosFragment(
                channelName = channelData.name,
                channelId = channelData.id,
                typeImageRes = R.drawable.ic_twitch
            )
        findNavController().navigate(directions)
    }
}