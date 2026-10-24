# 테스트 작성 가이드

검증 범위와 완료 보고는 [quality.md](quality.md), 공통 도구의 책임은 [core/testing.md](../modules/core/testing.md)를 따른다.

Step 8에서 `:core:testing`의 Fake·fixture·MainDispatcherRule과 `:core:data`의 repository·mapper·PagingSource 로컬 테스트를 추가했다. Step 13에서 webtoon·store, Step 15에서 cafe·chzzk의 ViewModel·Compose Screen 테스트를 작성했다. 나머지 feature는 후속 MVI 단계에서 적용한다. 테스트 작성과 실행 통과는 구분하며, 수동 동작 확인은 [마이그레이션 기준선](../migration/baseline.md)과 [스모크 체크리스트](../migration/smoke-checklist.md)를 따른다.

Step 11의 [NavigatorTest](../../core/navigation/src/test/java/com/sghore/chimtubeworld/core/navigation/NavigatorTest.kt)는 템플릿 테스트를 5개 탭에 맞추어 확장했다. 탭 재선택·sub-stack 유지·탭 이력·root 뒤로가기·popUpTo·결과 1회 소비를 검증한다. [NavigationKeysTest](../../app/src/test/java/com/sghore/chimtubeworld/navigation/NavigationKeysTest.kt)는 각 feature API의 모든 key 9개(YoutubeKey·PlaylistsKey·VideosKey·ChzzkKey·WebtoonKey·CafeKey·StoreKey·AddBookmarkKey·EditBookmarkKey)와 특수문자 인자·Video/Bookmark 직렬화를 확인한다. 각각 `:core:navigation:testDebugUnitTest`, `:app:testDebugUnitTest`로 실행하며 기기 스모크를 대체하지 않는다.

## 테스트 위치와 대상

| 대상 | 위치 | 확인할 계약 |
| --- | --- | --- |
| ViewModel | feature impl의 src/test | onEvent·uiState·sideEffect·조회 인자 |
| Repository | core data의 src/test | 요청 인자·DTO 변환·Entity 저장·실패 전파 |
| Compose Screen | feature impl의 src/androidTest | 상태별 표시·입력 가능 여부·event 전달 |
| 공통 도구 | core testing의 src/test | 결과·지연·취소·인스턴스 격리 |

- 로컬 unit test와 Compose 테스트는 JUnit 4, suspend 작업은 runTest를 사용한다.
- ViewModel·repository는 테스트에서 직접 생성한다. Hilt·서버·실제 Room 없이 계약을 확인한다. 테스트를 위해 production 접근 제한자나 private 함수를 공개하지 않는다.
- 단순 위임의 구현을 복제하는 테스트는 만들지 않는다.

## Fake·Spy와 fixture

- domain 계약 기반 Fake는 성공·빈 결과·실패·응답 지연을 제어하고 의미 있는 요청 인자를 기록한다. 테스트마다 새 Fake와 모델 fixture를 만든다.
- datasource Fake와 DAO Spy는 DTO·Entity 경계에 묶이므로 data의 src/test에 둔다. 공통 testing으로 옮기지 않는다.
- DAO Spy는 저장 시도와 성공한 저장을 구분한다. 이는 호출 계약 검증이며 SQL·Room 동작 검증은 아니다.
- 요청 ID와 응답 ID를 다르게 설정해 mapper가 요청 ID로 응답을 덮어쓰는지 확인한다.
- Firestore·Paging은 테스트 가능한 경계를 선택하고 직접 대역을 만들기 어려운 범위와 이유를 기록한다. 새 fallback 정책을 테스트 목적으로 추가하지 않는다.

## Main dispatcher와 가상 시간

- MainDispatcherRule이 Main을 교체한 뒤 ViewModel을 생성한다. 테스트 필드 초기화에서 생성하지 않는다.
- runTest에 Rule의 testDispatcher를 전달한다. 추가 dispatcher도 같은 testScheduler를 사용한다.
- `@IoDispatcher` 등을 주입받는 repository를 직접 생성할 때는 생성자의 dispatcher 인자에 같은 testScheduler의 test dispatcher를 전달한다. 현재 `CafeRepositoryImpl`의 `ioDispatcher`가 이에 해당하며 Hilt graph 없이 검증할 수 있다. ViewModel은 dispatcher를 주입받지 않으므로 MainDispatcherRule만 적용한다.
- StandardTestDispatcher에서 runCurrent로 현재 작업을 실행하고 로딩 상태를 확인한 후 advanceUntilIdle로 완료한다. 정확한 지연 경계는 advanceTimeBy 뒤 runCurrent로 실행한다.
- 실제 시간 대기나 Thread.sleep을 사용하지 않는다.
- ViewModelStore에 ViewModel을 등록하고 After에서 clear한다. 별도 수집은 backgroundScope 또는 명시적 취소로 정리한다. Rule은 종료 시 Main을 복원한다.

## 현재 테스트 예제와 경계

공통 Fake는 domain 계약을 구현하며 메서드별 설정을 사용한다. 예를 들어 `FakeYoutubeRepository`의 단일 영상 조회는 다음처럼 실제 시간 대기 없이 확인한다.

```kotlin
@Test
fun `요청 URL을 기록하고 설정한 영상을 반환한다`() = runTest {
    val expected = video(id = "response-video")
    val repository = FakeYoutubeRepository().apply {
        setVideoResult(expected)
        videoResponseDelayMillis = 1_000L
    }
    val response = async { repository.getVideo("requested-url") }
    runCurrent()
    assertFalse(response.isCompleted)
    assertEquals(listOf("requested-url"), repository.videoRequests)
    advanceUntilIdle()
    assertSame(expected, response.await())
}
```

위 코드는 `core.testing.model.video`, `FakeYoutubeRepository`, JUnit 4·coroutines-test의 API를 사용하며 시간 제어 함수에는 `@OptIn(ExperimentalCoroutinesApi::class)`을 적용한다. Main을 사용하는 테스트는 `@get:Rule val mainDispatcherRule = MainDispatcherRule()`을 선언하고 `runTest(mainDispatcherRule.testDispatcher)`로 실행한다. 실제 [Fake 테스트](../../core/testing/src/test/java/com/sghore/chimtubeworld/core/testing/repository)와 [Rule 테스트](../../core/testing/src/test/java/com/sghore/chimtubeworld/core/testing/rule/MainDispatcherRuleTest.kt)를 참고한다.

- [BookmarkRepositoryImplTest](../../core/data/src/test/java/com/sghore/chimtubeworld/core/data/repository/BookmarkRepositoryImplTest.kt)는 Entity 저장·삭제 인자, 내용 기반 ID 조회와 DAO 실패 전파를 확인한다. `Bookmark.equals`는 ID만 비교하므로 [BookmarkMapperTest](../../core/data/src/test/java/com/sghore/chimtubeworld/core/data/mapper/BookmarkMapperTest.kt)와 영상 북마크 변환 테스트는 모든 모델 필드를 별도로 비교한다.
- [CafeRepositoryImplTest](../../core/data/src/test/java/com/sghore/chimtubeworld/core/data/repository/CafeRepositoryImplTest.kt)는 `StandardTestDispatcher(testScheduler)`를 생성자에 전달하고 읽음 기록이 없을 때만 저장하는 정책과 조회·저장 실패를 확인한다.
- data의 `datasource/FakeCafeRemoteDataSource`, `FakeYoutubeRemoteDataSource`, `SpyDao`는 data의 `src/test`에만 둔다. Spy의 시도 목록과 성공 목록을 구분하며 Room SQL·충돌 정책·실제 DB 영속성은 검증하지 않는다.
- [PagingSource 테스트](../../core/data/src/test/java/com/sghore/chimtubeworld/core/data/repository/dataSource)는 네 source를 직접 생성하고 `paging-common`의 `LoadParams.Refresh`·`Append`로 `load()` 결과를 확인한다. Main/Sub playlist는 Fake의 공개 suspend 메서드 참조를 기존 callback 생성자에 전달한다. private helper나 production 가시성 변경이 필요하지 않다. 요청 ID와 응답 ID를 다르게 설정하고 DTO·북마크 매핑, 기존 key·빈 응답·Error 처리를 확인한다. Pager 전체 수명·UI 스크롤·refresh anchor는 이번 범위에서 검증하지 않는다.
- 공통 Fake의 Paging 응답은 `paging-testing`의 `asSnapshot()`으로 목록을 확인한다. 이 라이브러리는 `:core:testing` 자체 테스트에만 추가하며 production 의존성은 늘리지 않는다.
- `CafeRepositoryImpl.getCafeInfo()`는 Jsoup 정적 연결로 실제 웹을 읽고 주입 가능한 HTML 경계가 없어 제외한다. `CafePostPagingSource`의 사용되지 않는 private Jsoup helper도 제외한다. 이 source의 `load()`는 RemoteDataSource와 DAO만 사용하므로 테스트한다.
- Twitch·YouTube repository의 `getChannels()`는 Firestore 쿼리·Task·문서 변환을 직접 수행한다. 현재 경계로 서버 없이 Fake를 주입할 수 없어 제외한다. `YoutubeRepositoryImpl.getVideo()` 직접 테스트도 Firestore 생성자 의존성 때문에 이번 구성에서는 제외하며 영상 DTO 변환은 `YoutubePagingSource.load()`에서 확인한다. Firestore mock framework나 새 production 추상화는 도입하지 않는다.

## State와 side effect

- uiState.value에서 로딩·성공·빈 응답·예외·재시도를 서로 다른 조건으로 확인한다. 문구보다 의미를 검증한다.
- 진입 인자는 ViewModel 생성자에 직접 전달해 최초 조회·재시도의 ID 유지를 검증한다. Hilt Factory 연결은 컴파일·app 빌드로 별도 확인한다.
- side effect는 replay 없는 stream이므로 event 전에 수집한다. backgroundScope와 UnconfinedTestDispatcher(testScheduler)로 수집을 시작하고 생산 작업은 runCurrent로 실행한다. 수명과 지속 상태 구분은 [ui 규칙](../modules/core/ui.md)을 따른다.
- 입력 분기는 JUnit 4 Parameterized를 사용할 수 있다. 북마크 입력 검증, 생성·수정·삭제·결과 전달과 videos 선택 다이얼로그, 카페 카테고리·읽음·페이징, 치지직 채널 선택을 실제 계약에 맞춰 검증한다.
- repository는 성공뿐 아니라 원격 실패 시 저장하지 않음·저장 실패 전파를 기존 정책에 맞춰 확인한다.

## Compose Screen

- createComposeRule의 setContent에 theme와 Screen만 구성한다. 실제 ViewModel·Route·Hilt·Navigator를 사용하지 않는다.
- UiState를 직접 주입하고 onEvent 기록을 runOnIdle에서 확인한다. callback만으로 상태가 자동 갱신된다고 가정하지 않는다.
- 리소스 문자열은 targetContext.getString으로 읽고 데이터 문자열은 fixture를 사용한다.
- 보이는 텍스트와 semantics를 우선 사용한다. testTag는 텍스트로 선택하기 어려울 때만 사용한다.
- 상태별 표시·버튼 활성·입력·클릭 event를 나눠 확인한다. 실제 Main을 사용하므로 MainDispatcherRule은 적용하지 않는다. Android test 메서드 이름에는 공백 대신 밑줄을 쓴다.

## 실제 예제

| 테스트 | 검증 항목 |
| --- | --- |
| [WebtoonViewModelTest](../../feature/webtoon/impl/src/test/java/com/sghore/chimtubeworld/feature/webtoon/impl/webtoon_screen/WebtoonViewModelTest.kt) | 다섯 작품의 순서·이름·연도·이미지·ID·URL, 초기 effect 없음, 각 클릭의 정확한 ID·URL effect만 1회 전달·상태 유지 |
| [StoreViewModelTest](../../feature/store/impl/src/test/java/com/sghore/chimtubeworld/feature/store/impl/store_screen/StoreViewModelTest.kt) | 두 상점의 순서·이름·이미지·URL, 초기 effect 없음, 각 클릭의 정확한 URL effect만 1회 전달·상태 유지 |
| [WebtoonScreenTest](../../feature/webtoon/impl/src/androidTest/java/com/sghore/chimtubeworld/feature/webtoon/impl/webtoon_screen/WebtoonScreenTest.kt) | 주입한 다섯 작품의 스크롤 표시·연도·접근성 설명, 각 클릭의 event 값, 빈 목록의 제목·설명 |
| [StoreScreenTest](../../feature/store/impl/src/androidTest/java/com/sghore/chimtubeworld/feature/store/impl/store_screen/StoreScreenTest.kt) | 주입한 두 상점·제목·접근성 설명, 각 클릭의 event 값 |
| [CafeViewModelTest](../../feature/cafe/impl/src/test/java/com/sghore/chimtubeworld/feature/cafe/impl/cafe_screen/CafeViewModelTest.kt) | FakeCafeRepository로 초기 로딩·성공·실패, 카테고리 요청 ID·Paging Flow 교체·중복 수집 캐시, 읽음 상태·저장 ID·저장 실패 무시, 이미 읽은 글·null URL, 배너·침하하의 정확한 URL effect |
| [ChzzkViewModelTest](../../feature/chzzk/impl/src/test/java/com/sghore/chimtubeworld/feature/chzzk/impl/chzzk_screen/ChzzkViewModelTest.kt) | FakeTwitchRepository로 초기 로딩·성공·실패, 첫 type 0 선택·목록 순서·type 0 누락 예외 유지, 다이얼로그 열기·닫기, YouTube·메인·크루 채널의 정확한 URL effect |
| [CafeScreenTest](../../feature/cafe/impl/src/androidTest/java/com/sghore/chimtubeworld/feature/cafe/impl/cafe_screen/CafeScreenTest.kt) | 헤더·카페 정보·10개 카테고리 표시·선택된 카테고리 중복 입력 방지, 배너·침하하·Paging 게시글의 event, 오류 문자열 표시 |
| [ChzzkScreenTest](../../feature/chzzk/impl/src/androidTest/java/com/sghore/chimtubeworld/feature/chzzk/impl/chzzk_screen/ChzzkScreenTest.kt) | 로딩·오류 표시, 채널 목록·헤더·생방송 표시와 클릭, 다이얼로그 상태별 표시·두 선택지·뒤로가기 event |

webtoon·store는 고정 목록만 사용하므로 repository Fake나 로딩·실패·재시도 테스트가 필요하지 않다. cafe·chzzk는 core:testing의 Fake·fixture로 로딩·실패와 사용자 입력을 검증한다. 재시도 입력은 추가하지 않았으며 관련 테스트도 없다. Cafe의 Paging 단위 테스트는 Flow 교체와 캐시 수집 계약을 확인하고, 게시글 표시·클릭은 `flowOf(PagingData.from(...))`을 주입하는 Screen 테스트에서 확인한다. 실제 Pager 스크롤·원격 페이지 로딩은 기기 스모크 범위다.

ViewModel 테스트는 MainDispatcherRule·runTest·선행 effect 수집·ViewModelStore 정리를 사용하고, Screen 테스트는 `createComposeRule` v2에서 theme·Screen만 구성한다. 외부 앱 설치 여부·실제 Intent·Custom Tabs fallback과 라이트/다크 외형은 기기 스모크로 확인한다. APK 조립은 기기 테스트 실행을 대신하지 않는다.

## 실행과 결과

현재 core 테스트와 app 조립은 아래 첫 명령으로 검증한다. feature 명령은 대상 모듈 생성 후 실제 경로로 바꿔 사용한다.

```shell
./gradlew :core:testing:testDebugUnitTest :core:data:testDebugUnitTest :app:assembleDebug
./gradlew :feature:<name>:impl:testDebugUnitTest :core:data:testDebugUnitTest
./gradlew :feature:<name>:impl:assembleDebugAndroidTest
# 실행 가능한 기기 또는 에뮬레이터가 있을 때
./gradlew :feature:<name>:impl:connectedDebugAndroidTest
```

각 모듈의 build/test-results/testDebugUnitTest/TEST-*.xml에서 실행 수·실패·오류·건너뜀을 확인한다. APK 빌드와 기기 테스트 결과는 구분하고, 미실행 사유와 남은 범위는 [PR 검증 항목](pull-request.md)에 기록한다.
