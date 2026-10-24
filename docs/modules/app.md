# App 작업 규칙

## 현재 상태

`:app`은 앱 조립·navigation chrome·공유 인텐트 처리를 담당한다. 여섯 feature는 API·impl로 분리하고 모두 MVI를 사용한다. app에는 feature 화면·상태·ViewModel·NavKey·EntryProvider 구현을 두지 않는다.

[MainActivity](../../app/src/main/java/com/sghore/chimtubeworld/presentation/ui/MainActivity.kt)는 theme·시스템 바·공유 인텐트를, [ChimtubeWorldApp](../../app/src/main/java/com/sghore/chimtubeworld/presentation/ui/ChimtubeWorldApp.kt)은 feature entry 등록·NavDisplay·하단바를 조립한다. [AppState](../../app/src/main/java/com/sghore/chimtubeworld/presentation/ui/AppState.kt)는 NavigationState와 Navigator를 보유한다. feature 내부 사용 여부와 관계없이 모든 feature NavKey·Navigator 이동 확장 함수·result key는 각 API에 공개 선언하고, 화면과 모든 소유 key를 등록하는 단일 EntryProvider는 각 impl에 둔다. [app/navigation](../../app/src/main/java/com/sghore/chimtubeworld/navigation)에는 TopLevelDestination만 남는다.

app의 production Kotlin 파일은 `presentation/ui/{MainActivity,MainApplication,SplashActivity,ChimtubeWorldApp,AppState}.kt`, 하단바의 `presentation/ReUseableView.kt`, `navigation/TopLevelDestination.kt`다. Manifest의 Activity·intent filter와 app 식별자·런처·테마 리소스는 유지한다.

## Navigation과 화면 책임

- 시작 탭은 YouTube이고, 하단 탭 순서는 YouTube·Chzzk·Webtoon·Cafe·Store다. 선택 아이콘은 현재 sub-stack의 top-level key로 결정한다.
- ChimtubeWorldApp의 entryProvider는 youtubeEntry(navigator)·bookmarkEntry(navigator)·webtoonEntry()·storeEntry()·cafeEntry()·chzzkEntry()를 각각 한 번 호출한다. 각 feature 함수가 소유한 모든 화면의 entry를 등록하므로 app은 내부 화면 등록 함수를 알지 않는다. TopLevelDestination은 feature API의 YoutubeKey·WebtoonKey·StoreKey·CafeKey·ChzzkKey를 사용하며 app은 여섯 feature의 API·impl 모듈 12개에 직접 의존한다.
- app은 entryProvider를 등록하고 `NavigationState.toEntries`를 NavDisplay에 전달한다. entry별 saveable state·ViewModelStore는 core:navigation이 관리한다. [탭·뒤로가기 정책](core/navigation.md)을 적용한다.
- youtube API의 PlaylistsKey는 채널명·선택적 channelId·playlistId·type, VideosKey는 playlistId·플레이리스트명·VideoSource를 보관한다. bookmark API의 AddBookmarkKey는 URL·요청 ID, EditBookmarkKey는 Video·북마크 위치·core:model의 순수 enum VideoSource를 보관한다. Gson JSON이나 문자열 route는 사용하지 않는다.
- feature impl의 PlaylistViewModel·VideosViewModel·BookmarkViewModel은 AssistedInject를 사용한다. entry의 `hiltViewModel(creationCallback)`이 key의 인자를 factory.create에 전달한다. Route는 ViewModel을 필수 인자로 받고 Navigator 대신 side effect 콜백을 받는다.
- 북마크 저장·수정·삭제 완료는 BookmarkUiSideEffect.BookmarkChanged → Route의 onSideEffect → EntryProvider의 Toast·`setResult(BookmarkResultKey)`·`goBack`으로 연결한다. Videos entry는 복귀 시 `consumeResult`로 한 번 읽고 `onEvent(BookmarkResultReceived)`로 ViewModel에 전달한다.

## 화면별 상단바와 하단바

- 각 Screen이 designsystem의 `ScreenScaffold`를 호출해 자신의 상단바를 그린다. 최상위 5개 화면은 중앙 `CHIMHA` 제목, Playlists·Videos는 key의 채널명·플레이리스트명, Add/EditBookmark는 `북마크` 제목을 사용한다. 하위 4개 화면은 뒤로가기 화살표를 제공한다.
- Videos 도움말·Bookmark URL 복사는 Screen의 event → ViewModel의 side effect → Route의 전달 → EntryProvider의 Toast·클립보드 처리로 연결한다. 삭제 확인 다이얼로그는 UiState로 관리하고 Screen이 표시한다.
- 상단바의 기존 색상·56dp 높이·제목 스타일·뒤로가기 위치·아이콘·하단 구분선을 유지한다. 하단바는 AddBookmark/EditBookmark에서만 숨기며, 나머지 화면에서는 표시한다. 하단바는 56dp 높이, 그림자 8dp·tonalElevation 0dp·투명 선택 indicator를 유지한다.
- `enableEdgeToEdge`·`setSystemBarsColor`와 decor의 시스템 바 처리는 유지한다. root·화면 Scaffold와 상/하단바의 자동 window inset은 0이다. 색상·타이포그래피 기준은 [designsystem 문서](core/designsystem.md)를 따른다.
- 공통 아이콘은 designsystem drawable을 직접 참조한다. source enum은 북마크 정보 표시 지점에서 YouTube/Twitch drawable로 변환한다. Context 기반 외부 실행 helper `OpenOtherApp`은 core:ui에서 feature들이 공유한다.

## 공유 인텐트 진입

- Manifest의 ACTION_SEND `text/*`·singleTask·portrait와 Activity 선언을 유지한다.
- MainActivity는 최초 인텐트와 모든 지원 API에서 호출되는 `onNewIntent(Intent)`의 EXTRA_TEXT를 대기 목록에 넣고 인텐트를 비운다. 구성 변경 시 대기 목록은 Bundle에 보존하고 이미 처리한 최초 인텐트를 다시 소비하지 않는다.
- root의 LaunchedEffect가 준비된 Navigator로 bookmark api의 navigateToAddBookmark를 호출해 현재 탭 stack에 AddBookmarkKey를 추가하고 요청을 제거한다. 화면 복귀는 직전 화면으로 이루어진다. 이미 처리한 화면은 직렬화된 back stack으로 복원되며, 같은 URL의 연속 공유는 별도 요청 ID로 구분한다.

## 앱 조립의 경계

- app은 composition root로서 feature API·impl 및 Hilt 그래프를 조립한다. 화면과 상태는 각 feature impl이 소유한다.
- feature 사이의 이동은 대상 API의 확장 함수로 연결한다. app은 top-level 탭 조립에 한해 API의 NavKey를 직접 참조한다.
- data 구현과 저장소·네트워크 세부사항을 app 화면에서 직접 사용하지 않는다.
- convention·app 전용 설정은 [build-logic 규칙](../development/build-logic.md), 로컬 준비는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.
