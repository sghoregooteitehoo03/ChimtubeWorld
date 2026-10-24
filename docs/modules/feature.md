# Feature 작업 규칙

여섯 feature는 API·impl과 아래 MVI·문자열 리소스·Preview·테스트 규칙을 따른다. chzzk 화면은 Chzzk* 이름을 사용하며 domain의 TwitchRepository 계약은 유지한다. 데이터·식별자 보존과 릴리스 동작 검증은 [기준선](../migration/baseline.md)을 따른다.

feature API는 navigation 계약, impl은 EntryProvider와 assisted ViewModel 생성을 소유한다. app은 entry 등록과 TopLevelDestination을 소유한다.

## 모듈 경계

- 대상 feature는 `youtube`(playlist·videos 포함), `chzzk`, `webtoon`, `cafe`, `store`, `bookmark`다.
- feature는 `api`와 `impl`을 분리한다. 사용 모듈과 관계없이 모든 `@Serializable NavKey`·`Navigator.navigateTo…` 확장 함수·navigation result key는 API에 공개 선언한다. feature 내부에서만 사용하는 navigation 계약도 API에 두며 impl에 `internal`로 선언하지 않는다.
- youtube API는 YoutubeKey·PlaylistsKey·VideosKey와 navigateToYoutube·navigateToPlaylists·navigateToVideos를 공개한다. bookmark API는 AddBookmarkKey·EditBookmarkKey·navigateToAddBookmark·navigateToEditBookmark·BookmarkResultKey를 공개한다. app의 공유 인텐트 진입은 navigateToAddBookmark를, youtube impl의 편집 이동·결과 소비는 navigateToEditBookmark·BookmarkResultKey를 사용한다.
- 호출 모듈은 구체 NavKey를 직접 생성하지 않고 API의 `navigateTo...` 확장 함수를 사용한다.
- impl은 대응 API와 이동 대상 feature API에 직접 의존한다. API에는 Compose UI·ViewModel·data 구현을 두지 않는다.
- navigation parameter는 최소 식별자 또는 core model의 순수 공유 모델로 제한한다. 결과 key는 feature API가 `NavigationResultKey<T>` object로 선언한다.
- feature 간 연결 예: youtube impl은 bookmark api의 `navigateToEditBookmark`와 `BookmarkResultKey`에 의존한다. bookmark impl이 `setResult` 후 `goBack`하고, 현재 Videos entry가 `consumeResult`로 한 번 읽어 기존 ViewModel에 전달한다. bookmark impl → youtube 의존이나 impl → impl 의존은 두지 않는다. VideosKey·EditBookmarkKey의 공유 VideoSource는 core:model에 둔다.

## API·Impl 구조

```text
feature/<name>/api/
  navigation/<Name>Navigation.kt
feature/<name>/impl/
  <name>_screen/
    <Name>Contract.kt
    <Name>Route.kt
    <Name>Screen.kt
    <Name>ViewModel.kt
    component/<Name>Component.kt
  navigation/<Name>EntryProvider.kt
```

- 한 화면의 Contract·Route·Screen·ViewModel을 `<name>_screen`에 함께 둔다.
- impl은 화면을 소유하고, navigation에는 API의 모든 소유 key를 등록하는 단일 EntryProvider를 둔다.
- feature 하나는 `EntryProviderScope<NavKey>.<name>Entry(...)` 함수 하나만 공개하고 그 함수가 모든 소유 화면의 entry를 등록한다. 화면별 등록 helper가 필요하면 private으로 둔다. app은 feature별 함수만 호출해 화면을 등록한다. youtubeEntry(navigator)는 YoutubeKey·PlaylistsKey·VideosKey, bookmarkEntry(navigator)는 AddBookmarkKey·EditBookmarkKey를 함께 등록한다.
- component는 feature 내부 재사용에 한정한다. 여러 feature가 쓰는 component는 designsystem 또는 ui 승격을 검토한다.

## MVI와 Compose 화면

- Contract는 UiState·UiEvent·UiSideEffect를 구현한다. 지속 값은 state, 입력은 event, navigation·Snackbar·외부 앱 실행은 side effect에 둔다.
- ViewModel은 BaseViewModel을 상속하고 onEvent에서 처리한다. 비즈니스 규칙은 domain에 둔다.
- Route는 state 수집과 외부 콜백으로의 side effect 전달만 담당하며 Navigator를 받지 않는다. 화면 진입 인자를 event로 보내 최초 조회를 시작하지 않는다.
- EntryProvider가 side effect 콜백을 navigation·외부 실행·Toast·클립보드 동작으로 연결한다. 외부 앱·Custom Tabs 실행은 core:ui의 OpenOtherApp을 사용한다.
- Screen은 uiState·onEvent만 받아 레이아웃과 component를 조합한다. Screen·component는 ViewModel·Hilt·Navigator를 직접 알지 않는다.
- component는 표시와 입력 전달만 담당하며 화면 전용 상태 조합·navigation·ViewModel 생성을 넣지 않는다.
- 서로 다른 Kotlin 선언과 논리 블록 사이에는 공백 한 줄을 둔다.

## 화면 진입 인자와 ViewModel 생성

- 인자 없는 Route는 `hiltViewModel()` 기본 파라미터로 ViewModel을 얻는다.
- 인자 있는 화면은 직렬화 가능한 NavKey에 식별자를 둔다. feature 내부 사용 여부와 관계없이 모든 NavKey·Navigator 이동 확장 함수·result key는 API에 공개 선언한다. impl의 단일 `<name>Entry`가 모든 소유 key와 화면을 연결한다. ViewModel은 `@AssistedInject`, `@Assisted`, `@HiltViewModel(assistedFactory = …)`, `@AssistedFactory`로 생성한다.
- EntryProvider가 entry의 ViewModelStore를 사용하는 `hiltViewModel<VM, Factory>(creationCallback = …)`에서 factory.create를 호출하고 Route의 필수 인자로 전달한다. Factory를 별도 주입해 직접 ViewModel을 만들지 않는다.
- 최초 조회는 ViewModel 초기화에서 시작한다. 재시도는 생성자의 동일 식별자를 사용한다. 재구성 때 최초 조회 event를 다시 보내지 않는다.
- 같은 entry의 ViewModel이 유지되면 assisted 인자를 다시 주입하지 않는다. 다른 식별자는 새 NavKey로 진입한다.
- assisted 인자 자체는 저장되지 않는다. rememberNavBackStack에 직렬화 가능한 NavKey를 보관하고 복원된 entry에서 같은 인자로 ViewModel을 생성한다. stack 수명은 [navigation 규칙](core/navigation.md)을 따른다.
- 단위 테스트는 ViewModel을 직접 생성한다. Hilt 연결의 컴파일 확인과 실제 재생성·프로세스 복원 검증은 구분한다.

## 화면 문자열과 오류 상태

- 제목·버튼·입력 라벨·접근성 설명·오류 문구는 impl의 `src/main/res/values/strings.xml`이 소유한다. 이름에 feature 접두사를 사용하고 Screen에서 stringResource로 읽는다.
- 데이터 삽입은 리소스 형식 인자를 사용한다. 서버 데이터와 사용자 입력은 문자열 데이터로 유지한다.
- ViewModel은 표시 문구나 리소스 ID 대신 의미 있는 상태를 전달한다. 오류 종류에 맞는 표현을 선택하며 기존 실패·빈 응답 정책을 임의로 바꾸지 않는다.
- app 탭 이름은 app 리소스가 소유한다. 문구가 같다는 이유만으로 공통 UiText 추상화나 공통 문자열을 만들지 않는다.

## Preview

- 모든 Screen과 시각적 component에 Preview를 둔다. 샘플 UiState·빈 onEvent·샘플 콜백으로 그린다.
- Hilt 주입과 side effect 수집을 맡는 Route는 Preview 대상이 아니다.

## 새 feature 추가 절차

1. settings에 `:feature:<name>:api`와 `:feature:<name>:impl`을 등록한다. namespace는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.
2. api에는 `alias(libs.plugins.android.feature.api)`, impl에는 `alias(libs.plugins.android.feature.impl)`을 적용한다.
3. 각 모듈에 빈 `<manifest />`를 둔다. impl은 대응 API와 이동 대상 API를 의존하며 convention 의존성을 다시 선언하지 않는다.
4. API의 navigation contract, impl의 화면·문자열·Preview·EntryProvider를 연결한다.
5. app에 API·impl 의존성을 추가하고 root composable의 entryProvider에서 feature의 `<name>Entry(...)` 하나를 호출해 모든 화면을 등록한다. top-level 탭이면 app 탭 정의·문자열·아이콘도 연결한다.
6. 분기 있는 로직과 상태별 Screen 테스트는 [테스트 가이드](../development/testing.md)를 따른다.
7. 대상 모듈 compile·test 후 app assembleDebug를 확인한다. 명령과 완료 보고는 [품질 규칙](../development/quality.md)을 따른다.

## 참고 예제

- 인자 없는 top-level 화면의 Contract·Route·Screen·ViewModel: [webtoon_screen](../../feature/webtoon/impl/src/main/java/com/sghore/chimtubeworld/feature/webtoon/impl/webtoon_screen), [store_screen](../../feature/store/impl/src/main/java/com/sghore/chimtubeworld/feature/store/impl/store_screen). 고정 목록은 UiState의 데이터로 유지하며 표시하지 않는 로딩·오류 상태는 추가하지 않는다.
- repository 조회·로딩·오류 상태: [CafeViewModel](../../feature/cafe/impl/src/main/java/com/sghore/chimtubeworld/feature/cafe/impl/cafe_screen/CafeViewModel.kt), [ChzzkViewModel](../../feature/chzzk/impl/src/main/java/com/sghore/chimtubeworld/feature/chzzk/impl/chzzk_screen/ChzzkViewModel.kt)은 초기화 시 `suspendRunCatching`으로 조회하고 진행·실패를 표시 문구 대신 `isLoading`·`hasLoadError`로 표현한다. [CafeScreen](../../feature/cafe/impl/src/main/java/com/sghore/chimtubeworld/feature/cafe/impl/cafe_screen/CafeScreen.kt)·[ChzzkScreen](../../feature/chzzk/impl/src/main/java/com/sghore/chimtubeworld/feature/chzzk/impl/chzzk_screen/ChzzkScreen.kt)이 feature의 동일 오류 문구 리소스를 선택한다. 조회 실패는 본문에 오류 문구로 표시한다. 재시도 입력은 추가하지 않았다. Cafe의 카테고리별 Paging은 `cachedIn(viewModelScope)`를 유지하고 읽음 저장 실패는 삼킨다. Chzzk의 다이얼로그 열림은 UiState에 두며 첫 `type == 0` 채널이 없는 응답은 기존처럼 예외를 전파한다.
- cafe·chzzk 외부 실행: [CafeEntryProvider](../../feature/cafe/impl/src/main/java/com/sghore/chimtubeworld/feature/cafe/impl/navigation/CafeEntryProvider.kt)는 `OpenUrl`을 Custom Tabs로, [ChzzkEntryProvider](../../feature/chzzk/impl/src/main/java/com/sghore/chimtubeworld/feature/chzzk/impl/navigation/ChzzkEntryProvider.kt)는 `OpenYoutube`·`OpenUrl`을 기존 YouTube 패키지·웹 fallback 또는 Custom Tabs helper로 연결한다. Route는 lifecycle state 수집과 effect 전달만 담당하고 dialog는 Screen이 조합한다.
- 외부 실행 연결: [WebtoonEntryProvider](../../feature/webtoon/impl/src/main/java/com/sghore/chimtubeworld/feature/webtoon/impl/navigation/WebtoonEntryProvider.kt)는 `OpenWebtoon(id, url)`을 기존 패키지·스킴·웹 fallback helper로, [StoreEntryProvider](../../feature/store/impl/src/main/java/com/sghore/chimtubeworld/feature/store/impl/navigation/StoreEntryProvider.kt)는 `OpenUrl(url)`을 Custom Tabs helper로 연결한다. app의 기존 `webtoonEntry()`·`storeEntry()` 등록은 유지한다.
- 화면 소유 문자열·접근성 형식 인자: [webtoon strings.xml](../../feature/webtoon/impl/src/main/res/values/strings.xml), [store strings.xml](../../feature/store/impl/src/main/res/values/strings.xml). 작품명·연도·상점명·URL은 데이터로 유지한다. Screen과 `component/`의 항목은 샘플 UiState로 라이트·다크 Preview를 제공한다.
- AssistedInject·최초 조회 예제: [PlaylistViewModel](../../feature/youtube/impl/src/main/java/com/sghore/chimtubeworld/feature/youtube/impl/playlist_screen/PlaylistViewModel.kt)·[VideosViewModel](../../feature/youtube/impl/src/main/java/com/sghore/chimtubeworld/feature/youtube/impl/videos_screen/VideosViewModel.kt)은 생성자의 ID·타입으로 init에서 Paging을 요청한다. 제목과 영상 소스도 생성자 인자로 전달한다. [YoutubeEntryProvider](../../feature/youtube/impl/src/main/java/com/sghore/chimtubeworld/feature/youtube/impl/navigation/YoutubeEntryProvider.kt)의 단일 youtubeEntry가 세 key를 등록하고 entry ViewModelStore의 `hiltViewModel<VM, Factory>(creationCallback = …)`로 생성한 VM을 Route에 필수 인자로 전달한다.
- 결과 key·Context effect 예제: [BookmarkEntryProvider](../../feature/bookmark/impl/src/main/java/com/sghore/chimtubeworld/feature/bookmark/impl/navigation/BookmarkEntryProvider.kt)의 단일 bookmarkEntry가 Add/Edit key를 등록하고 공유 [BookmarkRoute](../../feature/bookmark/impl/src/main/java/com/sghore/chimtubeworld/feature/bookmark/impl/bookmark_screen/BookmarkRoute.kt)·Screen을 사용한다. `BookmarkChanged`를 기존 Toast → `setResult(BookmarkResultKey)` → `goBack`으로 연결하고 복사는 ClipboardManager·동일 Toast로 처리한다. Videos entry는 현재 보이는 entry에서 결과를 한 번 소비해 `BookmarkResultReceived` 이벤트로 전달한다. Videos의 `OpenVideo`는 OpenOtherApp의 기존 YouTube/Twitch·Custom Tabs fallback으로, `ShowHelp`는 기존 도움말 Toast로 연결한다. Route에는 Context 작업이 없다.
- 입력 검증·dialog 예제: [BookmarkContract](../../feature/bookmark/impl/src/main/java/com/sghore/chimtubeworld/feature/bookmark/impl/bookmark_screen/BookmarkContract.kt)·[BookmarkViewModel](../../feature/bookmark/impl/src/main/java/com/sghore/chimtubeworld/feature/bookmark/impl/bookmark_screen/BookmarkViewModel.kt)은 제목 10자·위치 8자 제한, 비어 있지 않을 때 저장 활성, 기존 SimpleDateFormat 파싱·영상 범위 검증과 InvalidPosition 효과를 사용한다. 오류 문구·저장 결과 문구는 impl 문자열 리소스가 소유한다. 기존 시간대 의존과 lenient 정규화는 유지한다. 삭제 확인은 isOpenDialog, 영상 시작 위치 선택은 VideosUiState.selectedVideo로 표현하며 Screen이 feature-local component를 조합한다.
- YouTube·Bookmark 조회는 `suspendRunCatching`·isLoading·hasLoadError를 사용하며 Screen이 오류 리소스를 선택한다. 조회 오류는 cafe/chzzk처럼 본문에 표시한다. Bookmark 조회 실패 Toast는 유지한다. 저장·삭제 예외 전파와 삭제 결과의 빈 제목·0L 표식을 유지한다. Twitch 북마크 복사 URL은 저장된 영상 URL에 `?t=<time>`을 붙인다.
- ViewModel·Screen 테스트는 [테스트 가이드의 실제 예제](../development/testing.md#실제-예제)를 참고한다.
