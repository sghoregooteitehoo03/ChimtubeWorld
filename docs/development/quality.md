# 품질과 검증 규칙

## 구현 전

- 변경할 모듈의 문서를 확인한다. 직접 의존 모듈의 문서는 변경하는 계약·동작과 관련된 경우에 확인한다. 단순 문자열·레이아웃 수정처럼 의존 모듈의 계약·동작에 영향이 없으면 의존 관계만으로 읽기 범위를 넓히지 않는다.
- 기존 코드의 주석, 접근 제한자, package 구조를 먼저 확인한다.
- 공개 API, Hilt binding, navigation 정책, Gradle convention에 영향을 주는지 판단한다.

## 테스트와 컴파일

- 새로 추가하거나 변경하는 상태 계산, event 처리, usecase, repository 정책처럼 분기 있는 로직은 unit test를 추가한다. 기존 코드에 테스트를 소급해 추가할 필요는 없으며, 해당 로직을 변경할 때 적용한다.
- NavigationState 또는 Navigator 정책을 바꾸면 탭 전환, 재선택, sub-stack 보존, 뒤로가기 동작을 unit test로 검증한다.
- Compose 화면 변경 시 Screen과 feature-local component Preview를 추가하거나 갱신한다.
- 가능한 가장 작은 변경 모듈의 compile·test부터 수행한다. app 조립, navigation, DI 연결에 영향이 있으면 app까지 검증한다.
- 코드 작성을 마친 것과 작업을 완료한 것을 동일하게 취급하지 않는다. 가능한 검증을 수행하고, 수행하지 못한 검증은 이유와 함께 보고한다.

변경한 core·feature와 app에 해당하는 명령을 실행한다. `<module>`, `<name>`, `<module-path>`는 실제 Gradle module path로 바꾼다.

```shell
./gradlew :core:<module>:compileDebugKotlin
./gradlew :core:<module>:testDebugUnitTest
./gradlew :feature:<name>:api:compileDebugKotlin :feature:<name>:impl:compileDebugKotlin
./gradlew :feature:<name>:impl:testDebugUnitTest
./gradlew :app:compileDebugKotlin
./gradlew :app:assembleDebug
./gradlew :<module-path>:lintDebug
./gradlew test
./gradlew :app:bundleRelease
```

- 테스트가 없는 모듈에는 `testDebugUnitTest`를 기계적으로 실행하지 않고, 변경한 로직의 테스트가 있는 모듈을 대상으로 한다.
- build-logic이나 공통 dependency 구성을 변경하면 영향을 받는 모듈 compile 후 `:app:assembleDebug` 또는 전체 `test`로 범위를 넓힌다.
- device가 필요한 `connectedDebugAndroidTest`는 관련 instrumentation test와 실행 가능한 device가 있을 때 수행한다.

## 코드 리뷰

검토 범위, 근거 확인 절차, 변경 영역별 확인 항목, 심각도와 결과 보고는 [코드 리뷰 규칙](code-review.md)을 따른다. 리뷰 중 필요한 검증 범위와 명령은 이 문서의 테스트와 컴파일 기준을 적용한다.

## 문서 동기화

- [AGENTS.md](../../AGENTS.md)의 문서 갱신 기준에 해당하면 관련 문서를 같은 변경에 포함한다.
- 문서의 경로와 구현 참조가 실제 파일과 맞는지 확인한다.
