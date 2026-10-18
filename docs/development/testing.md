# 테스트 작성 가이드

검증 범위와 완료 보고는 [quality.md](quality.md), 공통 도구의 책임은 [core/testing.md](../modules/core/testing.md)를 따른다.

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 공통 Fake·fixture·MainDispatcherRule과 feature 테스트는 아직 도입하지 않았다. 아래는 향후 테스트 작성 규칙이다. 현재 동작 확인은 [마이그레이션 기준선](../migration/baseline.md)과 [스모크 체크리스트](../migration/smoke-checklist.md)를 따른다.

## 테스트 위치와 대상

| 대상 | 목표 위치 | 확인할 계약 |
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

## 실행과 결과

대상 모듈 생성 후 실제 경로로 바꿔 실행한다.

```shell
./gradlew :feature:<name>:impl:testDebugUnitTest :core:data:testDebugUnitTest
./gradlew :feature:<name>:impl:assembleDebugAndroidTest
# 실행 가능한 기기 또는 에뮬레이터가 있을 때
./gradlew :feature:<name>:impl:connectedDebugAndroidTest
```

각 모듈의 build/test-results/testDebugUnitTest/TEST-*.xml에서 실행 수·실패·오류·건너뜀을 확인한다. APK 빌드와 기기 테스트 결과는 구분하고, 미실행 사유와 남은 범위는 [PR 검증 항목](pull-request.md)에 기록한다.
