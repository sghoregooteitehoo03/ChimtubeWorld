# :core:testing

namespace와 package는 `com.sghore.chimtubeworld.core.testing`이다. production 소비자는 없으며 data 로컬 테스트와 여섯 feature의 ViewModel 테스트에서 Fake·fixture·MainDispatcherRule을 필요한 만큼 사용한다.

## 역할

JUnit 4 로컬 unit test에서 재사용하는 domain 계약 기반 Fake, 샘플 모델, coroutine Main dispatcher 도구를 제공한다. Android library로 빌드하지만 실제 네트워크·저장소·Hilt 그래프 없이 사용할 수 있다.

## 포함 대상

- 결과·예외·응답 지연을 제어하고 요청 ID를 기록하는 domain 계약 기반 Fake repository.
- 호출마다 새 공유 순수 모델을 만드는 모델 fixture.
- `StandardTestDispatcher`를 기본으로 Main을 교체·복원하는 `MainDispatcherRule`.

## 제외 대상

- production 코드, 실제 repository·network·Room 구현과 Hilt binding.
- feature 전용 UI state, Screen, ViewModel과 해당 화면의 테스트.
- datasource·DAO Fake/Spy. 해당 대역은 소비하는 data 모듈의 `src/test`에 둔다.

## 공개 경계와 의존성

- 테스트 도구는 다른 모듈의 테스트가 사용하므로 공개한다. 내부 결과와 mutable 요청 기록은 외부에 노출하지 않는다.
- 소비 모듈은 `testImplementation(project(":core:testing"))`으로만 사용한다. feature impl convention이 이 의존성을 제공하며 각 feature에서 중복 선언하지 않는다. 그 외 모듈은 필요한 경우 직접 추가한다. production의 `api`·`implementation`에는 추가하지 않는다.
- testing은 공개 도구의 타입에 필요한 `:core:domain`, JUnit 4, coroutine test를 `api`로 제공한다. `:core:model`은 domain의 `api`를 통해 제공한다. feature·data·network·database·ui에는 의존하지 않는다.
- coroutine test 버전은 기존 coroutine 버전과 같은 version catalog 항목을 사용한다.

## 사용 기준

- 각 테스트는 새 Fake 인스턴스를 사용한다. 성공·데이터 없음·실패·재시도 응답을 명시적으로 설정한다.
- 메서드별 `<operation>ResponseDelayMillis`는 음수가 아닌 지연이다. `runTest`의 가상 시간으로 제어하며 실제 시간을 기다리지 않는다.
- 응답과 지연은 메서드 호출 시작 시 고정된다. 대기 중 결과·오류·지연 설정을 바꿔도 진행 중인 응답은 바뀌지 않는다. Paging은 Flow를 반환할 때 고정하며 수집 시 지연 후 방출한다. 호출 이후 수집 전에 설정을 바꿔도 기존 Flow에는 적용하지 않는다. 요청 기록에는 실패·취소된 호출도 포함한다.
- ViewModel은 JUnit Rule이 Main을 교체한 뒤 테스트 본문에서 생성한다. `runTest(mainDispatcherRule.testDispatcher)`로 같은 scheduler를 사용한다.
- 추가 dispatcher가 필요하면 Rule의 `testDispatcher.scheduler`를 전달한다. 테스트 종료 시에도 남는 작업은 테스트에서 취소하고 정리한다.
- Rule은 테스트 성공·실패와 무관하게 종료 시 `Dispatchers.resetMain()`을 호출한다. instrumentation test의 실제 Main을 교체하는 용도로 사용하지 않는다.

## 실제 구성

| 파일 | 제공하는 계약과 설정 |
| --- | --- |
| `repository/FakeBookmarkRepository.kt` | `add`·`delete`·`itemId`별 결과·오류·지연, `addRequests`·`deleteRequests`·`itemIdRequests` |
| `repository/FakeCafeRepository.kt` | `posts`·`info`·`read`별 결과·오류·지연, 카테고리·읽은 게시글 ID 기록, `infoCallCount` |
| `repository/FakeTwitchRepository.kt` | `channels` 결과·오류·지연, `channelsCallCount` |
| `repository/FakeYoutubeRepository.kt` | `videos`·`mainPlaylist`·`subPlaylist`·`channels`·`video`별 결과·오류·지연, 채널·목록 ID·URL 기록과 `channelsCallCount` |
| `model/ChimtubeFixtures.kt` | `channel`, `video`, `playlist`, `post`, `bookmark`, `linkInfo`, `cafeCategory`, `goodsChannelInfo` |
| `rule/MainDispatcherRule.kt` | 템플릿과 같은 JUnit 4 Rule. 기본 `StandardTestDispatcher`, 시작 시 Main 교체·종료 시 복원 |

- 각 operation은 `set<Operation>Result(...)`, `set<Operation>Error(Throwable)`로 설정한다. Unit 메서드의 성공 설정은 인자 없이 호출한다. 각 메서드의 설정은 다른 메서드에 영향을 주지 않는다.
- Paging setter는 `List<Model>` 또는 `Flow<PagingData<Model>>`을 받는다. 목록은 복사 후 `PagingData.from`으로 제공하며 기본 응답은 빈 목록이다. 사용자 지정 Flow는 해당 Flow의 방출을 따른다. Flow 참조 선택은 고정하지만 그 Flow 자체의 이후 방출까지 복제·동결하지는 않는다.
- ID가 없는 채널·카페 정보 조회는 호출 횟수를 기록한다. 재생목록 ID 입력과 요청 기록의 바깥 목록은 복사해 외부 목록 변경에서 격리한다.
- fixture는 호출마다 새 모델을 생성한다. `Channel.explains` 배열과 `Video.bookmarks` 입력 목록도 복사한다. 모델 내부의 기존 가변성은 유지한다.
- 자체 테스트는 네 Fake의 실패·복구·호출 시 설정 고정·가상 지연·인자 기록, 대표 suspend/Paging 취소·격리, fixture 인스턴스 격리, Rule의 scheduler 공유를 확인한다. Paging snapshot 검증용 `paging-testing`은 이 모듈의 `testImplementation`에만 둔다.

[현재 구현](../../../core/testing/src/main/java/com/sghore/chimtubeworld/core/testing)과 [자체 테스트](../../../core/testing/src/test/java/com/sghore/chimtubeworld/core/testing)를 참고한다. 작성 방법은 [테스트 가이드](../../development/testing.md)를 따른다.
