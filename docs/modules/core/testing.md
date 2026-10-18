# :core:testing

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 공통 테스트 도구는 아직 구현하지 않았다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

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
- `responseDelayMillis`는 음수가 아닌 지연이다. `runTest`의 가상 시간으로 제어하며 실제 시간을 기다리지 않는다.
- 응답과 지연은 조회 시작 시 고정된다. 대기 중 `setResult`나 `setError`로 설정을 바꿔도 진행 중인 응답은 바뀌지 않는다. `requestedIds`에는 실패·취소된 요청도 기록된다.
- ViewModel은 JUnit Rule이 Main을 교체한 뒤 테스트 본문에서 생성한다. `runTest(mainDispatcherRule.testDispatcher)`로 같은 scheduler를 사용한다.
- 추가 dispatcher가 필요하면 Rule의 `testDispatcher.scheduler`를 전달한다. 테스트 종료 시에도 남는 작업은 테스트에서 취소하고 정리한다.
- Rule은 테스트 성공·실패와 무관하게 종료 시 `Dispatchers.resetMain()`을 호출한다. instrumentation test의 실제 Main을 교체하는 용도로 사용하지 않는다.

작성 방법은 [테스트 가이드](../../development/testing.md)를 따른다. 공통 Fake·fixture·MainDispatcherRule은 후속 단계에서 도입하며 아직 구현이 없다.
