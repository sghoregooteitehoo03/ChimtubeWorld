package com.sghore.chimtubeworld.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.BookmarkColorAdapter
import com.sghore.chimtubeworld.databinding.FragmentAddBookmarkBinding
import com.sghore.chimtubeworld.ui.custom.LinearItemDecoration
import com.sghore.chimtubeworld.viewmodel.addBookemarkFrag.AddBookmarkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBookmarkFragment : Fragment(), BookmarkColorAdapter.BookmarkColorItemListener,
    View.OnClickListener {
    private val mViewModel by viewModels<AddBookmarkViewModel>()
    private val args by navArgs<AddBookmarkFragmentArgs>()

    private lateinit var bookmarkColorAdapter: BookmarkColorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentAddBookmarkBinding.inflate(inflater)
        bookmarkColorAdapter = BookmarkColorAdapter().apply {
            setOnItemListener(this@AddBookmarkFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@AddBookmarkFragment
            with(this.bookmarkColorList) {
                adapter = bookmarkColorAdapter
                itemAnimator = null
                addItemDecoration(
                    LinearItemDecoration(
                        requireContext(),
                        12
                    )
                )
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    // 북마크 아이템 클릭
    override fun onItemClickListener(pos: Int) {
        if (pos != bookmarkColorAdapter.previousSelectedPos) {
            mViewModel.selectedPos.value = pos
        }
    }

    // 뷰 클릭 이벤트
    override fun onClick(view: View) {
        when (view.id) {
            R.id.add_bookmark_btn -> {
                mViewModel.addBookmark()
            }
            else -> {}
        }
    }

    // 옵저버 설정
    private fun setObserver() {
        // 로딩 여부
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                mViewModel.getVideoData(args.url) // 동영상 정보를 가져옴
            }
        }
        // 에러 메세지
        mViewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.errorMsg.value = ""
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if(isError) {
                findNavController().navigateUp()
            }
        }
        // 완료 여부
        mViewModel.isComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                findNavController().navigateUp()
                Toast.makeText(requireContext(), "북마크가 추가되었습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // 북마크 색깔 선택 위치
        mViewModel.selectedPos.observe(viewLifecycleOwner) { pos ->
            if (pos != -1) {
                val color = bookmarkColorAdapter.getItem(pos)
                mViewModel.bookmarkColor.value = color

                bookmarkColorAdapter.selectColor(pos)
            }
        }
        mViewModel.bookmarkTitle.observe(viewLifecycleOwner) { title ->
            mViewModel.checkData()
        }
        mViewModel.videoPosition.observe(viewLifecycleOwner) { position ->
            mViewModel.checkData()
        }
        mViewModel.bookmarkColor.observe(viewLifecycleOwner) { color ->
            mViewModel.checkData()
        }
    }
}