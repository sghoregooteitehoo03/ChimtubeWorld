//package com.sghore.chimtubeworld.presentation.bookmarkScreen
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.sghore.chimtubeworld.databinding.FragmentAddBookmarkBinding
//import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class AddBookmarkFragment : Fragment() {
//    @Inject
//    lateinit var assistedFactory: BookmarkViewModel.AssistedFactory
//
//    private val gViewModel by activityViewModels<GlobalViewModel>()
//    private val mViewModel by viewModels<BookmarkViewModel> {
//        BookmarkViewModel.provideFactory(
//            assistedFactory = assistedFactory,
//            url = args.url
//        )
//    }
//    private val args by navArgs<AddBookmarkFragmentArgs>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // 인스턴스 설정
//        val binding = FragmentAddBookmarkBinding.inflate(inflater)
//
//        // 바인딩 설정
//        with(binding) {
//            this.composeView.apply {
//                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//                setContent {
//                    AddBookmarkRoute(
//                        viewModel = mViewModel,
//                        gViewModel = gViewModel,
//                        navController = findNavController()
//                    )
//                }
//            }
//
//            lifecycleOwner = viewLifecycleOwner
//        }
//
//        return binding.root
//    }
//}