package com.sghore.chimtubeworld.presentation.editBookmarkScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.BookmarkColorAdapter
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.databinding.FragmentAddBookmarkBinding
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import com.sghore.chimtubeworld.presentation.addBookmarkScreen.AddBookmarkViewModel
import com.sghore.chimtubeworld.presentation.ui.custom.LinearItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditBookmarkFragment : Fragment(), BookmarkColorAdapter.BookmarkColorItemListener,
    View.OnClickListener {
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<AddBookmarkViewModel>()
    private val args by navArgs<EditBookmarkFragmentArgs>()

    private lateinit var bookmarkColorAdapter: BookmarkColorAdapter
    private lateinit var bookmark: Bookmark

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentAddBookmarkBinding.inflate(inflater)
        bookmarkColorAdapter = BookmarkColorAdapter().apply {
            setOnItemListener(this@EditBookmarkFragment)
        }

        // 바인딩 설정
        with(binding) {
            this.viewmodel = mViewModel
            this.clickListener = this@EditBookmarkFragment

            this.addBookmarkBtn.text = "수정하기"
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
        setHasOptionsMenu(true)

        initViewModelValue()
        setObserver()
    }

    // 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_bookmark_menu, menu)
    }

    // 메뉴 아이템 클릭
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_bookmark -> { // 삭제
                deleteDialog()
                true
            }
            R.id.copy_bookmark -> { // 공유
                clipData()
                true
            }
            else -> false
        }
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
                mViewModel.addOrEditBookmark(bookmark.id!!)
            }
            else -> {}
        }
    }

    private fun initViewModelValue() {
        val videoData = gViewModel.videoData.value!!
        val typeImageRes = args.typeImageRes
        bookmark = videoData.bookmarks[args.bookmarkPos]
        val colorIndex = bookmarkColorAdapter.getSelectedPos(bookmark.color)

        mViewModel.initValue(
            videoData = videoData,
            bookmark = bookmark,
            typeImageRes = typeImageRes,
            colorIndex = colorIndex
        )
    }

    // 옵저버 설정
    private fun setObserver() {
        // 토스트 메세지
        mViewModel.toastMsg.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                    .show()
                mViewModel.toastMsg.value = ""
            }
        }
        // 오류 여부
        mViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                findNavController().navigateUp()
            }
        }
        // 완료 여부
        mViewModel.isComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                gViewModel.refreshList.value = true // 영상 화면일 경우 refresh
                findNavController().navigateUp()
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

    // 북마크 삭제 다이얼로그
    private fun deleteDialog() {
        with(MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)) {
            setMessage("북마크를 삭제하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                mViewModel.deleteBookmark(bookmark)
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }

            show()
        }
    }

    // url 클립보드에 저장
    private fun clipData() {
        val url = mViewModel.getVideoUrl(bookmark.videoPosition)
        val clipBoard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("url", url)
        clipBoard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "URL이 복사되었습니다.", Toast.LENGTH_SHORT)
            .show()
    }
}