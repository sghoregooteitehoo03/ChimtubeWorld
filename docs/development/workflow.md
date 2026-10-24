# 여러 모듈 작업 Workflow

이 문서는 여러 계층이나 모듈에 걸친 기능 구현에서 연결 누락을 방지하기 위한 체크리스트다. 구현 순서를 강제하지 않으며, 단순 UI·문자열·resource·단일 파일 수정이나 범위가 명확한 bug fix에는 전체 항목을 적용하지 않는다.

## 작업 전

1. 요구사항과 완료 조건을 확인하고 변경 범위를 정한다.
2. [README.md](../README.md)의 읽기 순서에 따라 영향을 받는 영역 문서를 찾는다. 변경 대상 모듈과 연결 지점을 파악하고, 의존 모듈 문서는 [quality.md의 구현 전 기준](quality.md#구현-전)에 따라 확인한다.
3. Gradle 설정, Kotlin 타입 선언, 관련 reference implementation을 확인해 현재 상태와 기존 규칙을 구분한다.
4. 변경되는 public API와 contract를 식별한다. 근거가 부족한 새 abstraction이나 module 책임은 임의로 확정하지 않는다.

## 구현 확인

5. 필요한 model, domain contract·usecase, data repository·mapper, network·database, feature, app 변경을 식별한다. 필요하지 않은 계층을 기계적으로 추가하지 않는다.
6. 각 변경이 해당 모듈의 포함·제외 대상과 공개 경계를 지키고, Gradle dependency 방향을 역전시키지 않는지 확인한다.
7. Hilt provider·binding과 scope, datasource·DAO 연결, DTO·Entity·model 변환이 필요한 경우 누락 여부를 확인한다.
8. navigation contract, EntryProvider, app 조립, top-level chrome 변경이 필요한 경우 연결 지점을 함께 확인한다.

## 완료 전

9. [quality.md](quality.md)에 따라 변경 모듈의 가장 작은 compile·test부터 수행하고, app 조립이나 전역 구성에 영향이 있으면 검증 범위를 넓힌다.
10. module responsibility, dependency direction, public API, navigation·architecture·상태 관리·테스트 convention, convention plugin 또는 이 workflow가 바뀌었으면 관련 문서를 같은 작업에서 갱신한다.

판단 근거가 충돌하거나 부족하면 확실한 범위까지만 작업하고, 확정하지 않은 내용과 수행하지 못한 검증을 결과에 명시한다.
