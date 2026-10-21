# :core:designsystem

Step 9에서 추출했다. namespace/package는 `com.sghore.chimtubeworld.core.designsystem`이다. library·library.compose convention을 적용한다. 공개 Shapes·Typography 타입을 위해 기존 Material 2 의존성을 `api`로 제공하며, convention의 Material 3 의존성은 실제 UI 전환을 뜻하지 않는다.

## 역할

앱 전체에서 공유하는 Material theme, 색상·타이포그래피, 재사용 가능한 Compose component와 drawable 리소스를 제공한다.

## 포함 대상

- 앱 공통 theme, color scheme, typography.
- 여러 화면에서 동일한 규칙으로 사용할 button, text, loading component.
- 앱 공통 아이콘 drawable resource.

## 제외 대상

- feature 전용 문구, 화면 조합, 화면 상태 판단, ViewModel, navigation.
- model을 받아 화면별 정책으로 가공하는 UI와 data/domain 로직.

## 공개 경계와 의존성

- 공통 Compose API는 기본 공개 범위를 사용한다.
- component는 공통 Material 표현으로 시작하고, feature 정책을 담지 않는다.
- 모든 디자인 시스템 component에는 theme 적용을 확인하는 Preview를 둔다.
- 아이콘은 별도 Kotlin 단일 진입점 없이 designsystem의 drawable을 `R.drawable`로 직접 참조한다.
- designsystem은 Compose와 Material에만 의존하며 다른 core 모듈에 의존하지 않는다.

## 전환 순서

먼저 기존 Material 2 theme·component를 그대로 옮긴다. Material 3 전환은 별도 단계이며 Compose convention의 Material 3 의존성 추가만으로 UI를 바꾸지 않는다.

## 현재 포함된 구현과 리소스

- `theme/Color.kt`, `Shape.kt`, `Theme.kt`, `Type.kt`: Material 2 색상·Shapes·Typography와 `ChimtubeWorldTheme(darkTheme: Boolean = false, content)`를 그대로 유지한다. Material 3 전환은 Step 10에서 수행한다.
- `component/ReUseableView.kt`: 모델·Paging·navigation에 의존하지 않는 `TitleTextWithExplain`, `RowList`, `RowItemCollocate`와 theme 적용 Preview.
- `res/values/colors.xml`, `values-night/colors.xml`: 기존 파일 전체를 값·이름·qualifier 변경 없이 이동했다. app의 XML theme도 리소스 병합으로 이 색상을 사용한다. 런처 전용 `ic_launcher_background`는 app에 유지한다.
- `drawable-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}`: `youtube`, `chzzk`, `twitch`, `webtoon`, `cafe`, `store`와 각각의 `_darker` 변형(12종, 60파일). 현재 탭 chrome과 영상 종류 표시에 쓰이며 미사용 twitch 탭 변형도 같은 아이콘 묶음으로 보존한다.
- `drawable/ic_close.xml`, `ic_twitch_bedge.xml`: 현재 참조는 없지만 범용 닫기·플랫폼 배지 아이콘으로 함께 이동한다.
- app에 남긴 리소스: `webtoon_cover_1..5`(웹툰 화면), `marple`, `naver`(스토어 화면), `ic_logo`(현재 미사용 로고), `splash`(XML splash), `ic_launcher_foreground`와 mipmap·`ic_launcher_background`(런처), `themes.xml`·`values-night/themes.xml`(앱 XML theme), `attrs.xml`과 `dimens.xml`의 `pageMargin`·`pagerWidth`(공유 사용처 없음). 단일 화면 자산은 feature 분리 시 이동을 검토한다.

## 현재 구현 참고

- [현재 소스](../../../core/designsystem/src/main/java/com/sghore/chimtubeworld/core/designsystem)
- [현재 리소스](../../../core/designsystem/src/main/res)
- 참고 템플릿: `core/designsystem/build.gradle.kts`, `core/designsystem/src/main/AndroidManifest.xml`, `core/designsystem/src/main/java/com/sg/androidcomposetemplate/core/designsystem`.
