package com.sghore.chimtubeworld.presentation.bookmarkScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.databinding.FragmentAddBookmarkBinding
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditBookmarkFragment : Fragment() {
    @Inject
    lateinit var assistedFactory: BookmarkViewModel.AssistedFactory

    private val args by navArgs<EditBookmarkFragmentArgs>()
    private val gViewModel by activityViewModels<GlobalViewModel>()
    private val mViewModel by viewModels<BookmarkViewModel> {
        BookmarkViewModel.provideFactory(
            assistedFactory = assistedFactory,
            videoData = gViewModel.videoData.value,
            bookmark = gViewModel.videoData.value?.bookmarks?.get(args.bookmarkPos),
            typeImageRes = args.typeImageRes
        )
    }

    private lateinit var bookmark: Bookmark

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 인스턴스 설정
        val binding = FragmentAddBookmarkBinding.inflate(inflater)
        val videoData = gViewModel.videoData.value!!
        bookmark = videoData.bookmarks[args.bookmarkPos]

        // 바인딩 설정
        with(binding) {
            this.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    EditBookmarkScreen(
                        viewModel = mViewModel,
                        gViewModel = gViewModel,
                        navController = findNavController()
                    )
                }
            }

            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
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

    // 북마크 삭제 다이얼로그
    private fun deleteDialog() {
        with(MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)) {
            setMessage("북마크를 삭제하시겠습니까?")
            setPositiveButton("확인") { dialog, which ->
                dialog.dismiss()
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