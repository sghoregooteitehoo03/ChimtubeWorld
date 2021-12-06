package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sghore.chimtubeworld.adapter.TwitchUserAdapter
import com.sghore.chimtubeworld.databinding.FragmentTwitchBinding
import com.sghore.chimtubeworld.viewmodel.twitchFrag.TwitchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TwitchFragment : Fragment() {
    private val mViewModel by viewModels<TwitchViewModel>()

    private lateinit var twitchUserAdapter: TwitchUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentTwitchBinding.inflate(inflater)
        twitchUserAdapter = TwitchUserAdapter()

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.twitchUserList.adapter = twitchUserAdapter

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
        // 유저 리스트
        mViewModel.twitchUserList.observe(viewLifecycleOwner) { userList ->
            if(userList != null) {
                twitchUserAdapter.syncData(userList)
            } else { // 리스트가 없으면 로드함
                mViewModel.getTwitchUserInfo()
            }
        }
    }
}