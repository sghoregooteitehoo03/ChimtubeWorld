# 코드 리뷰 규칙

이 문서는 PR·브랜치·작업 트리 또는 지정한 모듈의 코드를 검토하는 절차와 결과 보고 기준이다. 구현 규칙은 해당 모듈 문서, 검증 범위와 명령은 [quality.md](quality.md), 테스트 작성 방법은 [testing.md](testing.md)를 따른다.

## 검토 범위

- 시작할 때 현재 브랜치와 `git status --short`를 확인하고, 요청이 PR·브랜치 비교인지 작업 트리 또는 특정 모듈 검토인지 구분한다. 범위가 불명확해 검토 대상이 달라지면 사용자에게 확인한다.
- PR·브랜치 리뷰는 지정된 base를 우선한다. 지정이 없으면 PR 대상 브랜치와 저장소 정보를 확인해 base를 정하고 보고한다. `git merge-base <base> HEAD`를 기준으로 전체 변경을 확인하며 마지막 커밋만 검토하지 않는다.
- 작업 트리 리뷰는 요청된 staged·unstaged 변경과 관련 untracked 파일을 확인한다. PR·브랜치 리뷰에 로컬 미커밋 변경을 임의로 섞지 않는다.
- 모듈·전체 리뷰는 요청 범위의 진입점, 상태 소유자, 의존성, 테스트 위치를 파악하고 영향이 큰 흐름부터 확인한다. PR 리뷰에서는 변경으로 추가되거나 악화된 문제와 기존 문제를 구분한다.
- [README.md](../README.md)에서 변경 영역 문서를 선택한다. 구조·의존성 판단이 필요하면 [spec.md](../architecture/spec.md)를, 빌드 구성 변경이 있으면 [build-logic.md](build-logic.md)를 추가로 확인한다.

## 근거 확인 절차

1. 요구사항, diff, 관련 테스트로 변경 목적과 변경 전·후 동작을 파악한다. 확인되지 않은 요구사항을 가정하지 않는다.
2. 변경된 줄의 주변 구현을 읽고 관련 호출자·호출 대상, state 생산·소비 지점, 저장·네트워크 경계까지 추적한다. 공통 API나 component 변경은 영향을 받는 사용처도 확인한다.
3. 문제를 일으키는 입력·실행 순서·lifecycle 조건과 실제 영향을 확인한다. 관련 방어 로직, 동일 기능의 기존 구현, 문서에 명시된 예외도 확인한다.
4. 필요한 경우 [품질 규칙](quality.md)에 따라 가장 작은 관련 compile·test 또는 재현으로 판단을 보강한다. 실행하지 못한 검증과 환경 제약은 코드 결함과 구분한다.
5. 근거가 확인된 문제만 지적한다. 전제가 확인되지 않은 내용은 질문이나 선택적 제안으로 분리하고, 하나의 원인에서 발생하는 문제는 중복 보고하지 않는다.

## 변경 영역별 확인

변경 내용과 관련된 행만 확인한다. 세부 정책은 연결된 문서를 기준으로 판단한다.

| 변경 영역 | 확인할 내용과 기준 문서 |
| --- | --- |
| 모듈 경계·공개 API·DI | 의존성 방향, 접근 제한자, feature API의 구현 노출, DTO·Entity의 domain/feature 노출, Hilt binding 중복·누락과 scope를 확인한다. [spec](../architecture/spec.md), [feature](../modules/feature.md), [app](../modules/app.md), 변경한 core 문서를 따른다. |
| ViewModel·Coroutine·Flow | 로딩·성공·빈 값·실패·재시도 상태, 중복 요청과 응답 순서, 취소 전파, 작업·수집자의 수명, side effect 전달 조건을 추적한다. [feature](../modules/feature.md), [ui](../modules/core/ui.md), [common](../modules/core/common.md)을 따른다. |
| Compose 화면·공통 component | Route·Screen의 책임, state와 event 연결, effect 실행 조건, 화면 재진입·재구성 시 동작, 문자열·접근성, 변경된 공통 component의 사용처를 확인한다. [feature](../modules/feature.md), [ui](../modules/core/ui.md), [designsystem](../modules/core/designsystem.md)을 따른다. |
| Navigation | 이동 인자와 ViewModel 생성, top-level/sub-stack, 뒤로가기, result 소비, bottom bar 노출, 화면 재생성과 프로세스 복원의 영향을 확인한다. [navigation](../modules/core/navigation.md), [feature](../modules/feature.md), [app](../modules/app.md)을 따른다. |
| 데이터·저장소·외부 입력 | 요청 인자와 DTO·Entity 변환, 오류·취소 전파, 부분 저장 실패, schema·저장 key 호환성을 확인한다. 민감 정보·인증·logging·외부 입력을 다루면 노출과 입력 검증도 확인한다. [data](../modules/core/data.md), 변경한 [network](../modules/core/network.md)·[database](../modules/core/database.md) 문서를 따른다. |
| Gradle·convention·테스트 | 공개·구현·테스트 의존성의 범위와 중복, 공통 설정의 영향 모듈, 변경된 동작과 실패 경로를 검증하는 테스트를 확인한다. [build logic](build-logic.md), [quality](quality.md), [testing](testing.md)을 따른다. |

## 지적 기준과 심각도

- 실제 버그, 컴파일 오류, runtime crash, 보안 문제, 의존성 역전, 명확한 모듈 경계 위반, resource leak·lifecycle 문제를 우선한다.
- 문법이나 API 이름만 보고 결함으로 단정하지 않는다. 예를 들어 nullable 값, broad catch, 반복 호출은 도달 가능한 실패 조건과 결과를 확인한 뒤 판단한다.
- 중복·불필요한 추상화·가독성 문제는 구체적인 유지보수 영향과 동작을 보존하는 개선 방향이 있을 때 지적한다. [domain의 usecase 기준](../modules/core/domain.md#공개-경계와-의존성)처럼 명시된 정책을 먼저 확인한다.
- 단순 취향, formatter·linter가 처리할 형식, 근거 없는 성능 최적화나 무관한 리팩터링을 필수 수정으로 요구하지 않는다.

| 심각도 | 판단 기준 |
| --- | --- |
| Critical | 재현 조건과 영향이 확인된 crash, 데이터 유실·손상, 악용 가능한 보안 결함, 핵심 기능 실패 |
| Major | 잘못된 상태·결과, lifecycle·취소·경쟁 조건 오류, 의도하지 않은 반복 요청, 실제 영향이 있는 구조·계약 위반 |
| Minor | 영향이 제한된 중복·불필요한 구현·가독성 문제로, 구체적인 개선이 가능한 항목 |

발생 조건·가능성·영향 범위를 함께 설명한다. 선택적 개선은 심각도가 있는 결함과 구분해 `Suggestion`으로 표시하고, 장단점을 짧게 적는다.

## 결과 보고

- 지적 사항을 심각도 순으로 먼저 제시한다. 각 항목에 `심각도`, 정확한 `파일:줄`, 발생 조건, 문제와 실제 영향, 코드·테스트 근거, 수정 방향을 포함한다. 위치는 문제를 이해하는 데 필요한 최소 범위로 잡는다.
- 발견한 문제가 없으면 "검토 범위에서 확인된 문제 없음"이라고 명시한다. 테스트 통과나 지적 없음만으로 전체 동작을 보장하지 않는다.
- 검토한 브랜치·base·범위, 실제 수행한 검증과 결과, 미실행 사유, 남은 회귀 위험이나 확인이 필요한 질문을 기록한다. 수행하지 않은 검증을 통과로 보고하지 않는다.
- 마지막에 필수 수정과 선택적 개선을 요약한다. PR·브랜치 리뷰는 근거에 따라 `병합 가능`, `수정 후 재검토`, `추가 확인 필요`를 판단하고, 모듈·전체 리뷰는 수정 우선순위를 정리한다.
