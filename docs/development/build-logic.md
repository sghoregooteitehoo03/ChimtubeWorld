# Build Logic 작업 규칙

## 단일 기준

- 라이브러리와 Android SDK 버전, version catalog로 적용하는 plugin 버전은 `gradle/libs.versions.toml`에서 관리한다.
- 공통 Android 설정과 의존성 묶음은 `build-logic/convention`의 convention plugin과 extension에서 관리한다.
- 여러 모듈에 반복되는 의존성을 각 모듈 Gradle 파일에 중복 추가하지 않는다.

## 현재 적용 범위

- plugin id는 참조 구현과 같은 `sg.*`를 유지한다. 등록·내부 적용·catalog alias·문서의 이름을 일치시켜 불필요한 이름 변경을 줄인다. build-logic group은 `com.sghore.chimtubeworld.buildlogic`이다.
- app에는 application, application.compose, hilt만 적용한다. 기존 SDK 설정은 catalog에서, Java 17 호환성은 Android extension에서 제공한다. 앱 식별자·버전·BuildConfig·릴리스 설정·google-services는 app에 남긴다.
- Compose convention은 Material 3와 공통 UI·Lifecycle·테스트 의존성을 추가한다. 기존 Material 2는 app과 designsystem이 선언하며 icons-extended는 app이 계속 선언한다.
- Room convention은 `room.incremental=true`, `room.schemaLocation=$projectDir/schemas`를 추가하므로 현재 app에는 적용하지 않는다. 기존 Room 의존성과 KSP 설정을 유지한다.
- library는 추출된 core에, library.compose는 designsystem·ui에 적용한다. feature.api·feature.impl·room·serialization은 아직 적용하지 않았다. **마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. [기준선](../migration/baseline.md)을 확인한다. feature의 `:core:*` 참조는 plugin을 적용할 때만 평가한다.

## 공통 테스트 구성

- Android application·library convention은 `configureAndroidTestDependencies`에서 로컬 JUnit 4와 coroutine test, instrumentation의 AndroidX JUnit·Espresso를 적용한다.
- coroutine test는 production coroutine과 같은 version catalog 버전을 사용한다. Jupiter나 별도의 테스트 framework plugin은 적용하지 않는다.
- Compose convention은 기존 Compose UI test와 manifest 의존성을 유지한다.
- 공통 Fake·fixture·Main dispatcher 도구는 `:core:testing`에서 제공한다. feature impl convention이 `testImplementation`으로 연결하므로 각 feature에서 중복 선언하지 않는다. 다른 소비 모듈은 필요한 경우 직접 테스트 의존성을 추가한다. 공통 Android library convention에는 넣지 않는다.
- testing 모듈 자체가 공개하는 domain·JUnit·coroutine test 타입은 해당 모듈의 `api`로 제공한다. model은 domain의 `api`를 통해 제공한다. 이것을 production 모듈의 테스트 라이브러리 공개 근거로 일반화하지 않는다.

## Convention plugin

- `sg.android.application`: Android application 기본 설정과 공통 테스트 의존성을 적용한다.
- `sg.android.application.compose`: application에 Compose compiler와 공통 Compose 의존성을 적용한다.
- `sg.android.library`: Android library 기본 설정과 공통 테스트 의존성을 적용한다.
- `sg.android.library.compose`: Compose compiler와 공통 Compose 의존성을 적용한다.
- `sg.android.hilt`: Hilt plugin과 compiler를 적용한다.
- `sg.android.room`: Room runtime, KTX, KSP 설정을 적용한다.
- `sg.kotlin.serialization`: Kotlin serialization plugin과 JSON 의존성을 적용한다.
- `sg.android.feature.api`: Android library, serialization, `:core:model`, `:core:navigation`, serialization core를 공개 의존성으로 적용한다.
- `sg.android.feature.impl`: Android library, Compose, Hilt와 `:core:common`, `:core:domain`, `:core:designsystem`, `:core:ui`, Hilt Navigation Compose를 구현 의존성으로 적용하고 `:core:testing`을 테스트 의존성으로 적용한다.

## 새 모듈 추가

1. `settings.gradle.kts`에 모듈을 등록한다.
2. 모듈 역할에 맞는 convention plugin을 적용한다.
3. 필요한 프로젝트 의존성을 가장 좁은 범위로 추가한다.
4. 새 core 모듈이면 `docs/modules/core/<module>.md`를 함께 추가한다.
5. feature convention 또는 공통 plugin을 변경하면 영향을 받는 모든 모듈의 의존성 방향을 다시 확인한다.

새 feature 모듈의 전체 연결 순서는 [feature.md의 새 feature 추가 절차](../modules/feature.md#새-feature-추가-절차)를 따른다.

## 의존성 선택

- 모듈의 public type에 노출되는 의존성은 `api`를 사용한다.
- 구현 내부에서만 사용하는 의존성은 `implementation`을 사용한다.
- feature API는 navigation contract를 외부에 노출하므로 model과 navigation을 `api`로 제공한다.
- feature impl은 UI와 domain 사용을 내부로 유지한다.
- core는 app이나 feature에 의존하지 않는다. data 구현은 domain contract에 의존하고, domain은 model에만 의존한다.

참고 템플릿: `build-logic/convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt`, `AndroidFeatureApiConventionPlugin.kt`, `AndroidFeatureImplConventionPlugin.kt`, `extensions/AndroidExtensions.kt`, `gradle/libs.versions.toml`.
