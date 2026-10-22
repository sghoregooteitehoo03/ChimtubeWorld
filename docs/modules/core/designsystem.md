# :core:designsystem

Step 9에서 추출했다. namespace/package는 `com.sghore.chimtubeworld.core.designsystem`이다. library·library.compose convention을 적용한다. Step 10에서 Material 3로 전환했다. 공개 Shapes·Typography 타입을 위해 `androidx.compose.material3:material3`를 `api`로 제공한다.

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

## Material 3 테마와 색상 보존

`ChimtubeWorldTheme(darkTheme: Boolean = false, content)`의 이름과 시그니처를 유지한다. `lightColorScheme`·`darkColorScheme`을 사용하며 dynamic color는 사용하지 않는다. 기존 Color와 XML 리소스 값은 그대로다. M2 기본값으로 암묵적으로 사용하던 surface·error도 명시한다.

| M2 역할 | M3 역할 | 라이트 | 다크 |
| --- | --- | --- | --- |
| primary | primary | `#FFFFFF` | `#000000` |
| primaryVariant | primaryContainer | `#FFFFFF` | `#000000` |
| onPrimary | onPrimary, onPrimaryContainer | `#000000` | `#FFFFFF` |
| secondary | secondary | `#000000` | `#FFFFFF` |
| secondaryVariant | secondaryContainer | `#000000` | `#FFFFFF` |
| onSecondary | onSecondary, onSecondaryContainer | `#FFFFFF` | `#000000` |
| background | background | `#FFFFFF` | `#111111` |
| onBackground | onBackground | `#000000` | `#FFFFFF` |
| surface (M2 기본값) | surface, surfaceVariant, surfaceDim/Bright, surfaceContainer 계열 | `#FFFFFF` | `#121212` |
| onSurface | onSurface | `#000000` | `#FFFFFF` |
| error (M2 기본값) | error, errorContainer | `#B00020` | `#CF6679` |
| onError | onError, onErrorContainer | `#FFFFFF` | `#000000` |
| isLight | darkTheme 인자로 scheme 선택 | false → light | true → dark |

M3에만 있는 tertiary/container와 onTertiary/container는 secondary 계열을 따른다. 각 fixed/fixedDim·onFixed/Variant도 해당 primary·secondary·tertiary 계열의 같은 색으로 채운다. `onSurfaceVariant`는 onSurface의 0.6 alpha, `outline`·`outlineVariant`는 각각 0.38·0.12 alpha다. inverseSurface·inverseOnSurface는 반대 명도의 흑백, inversePrimary는 secondary, scrim은 Black이다. 기본 M3 보라색 팔레트가 화면에 유입되지 않도록 한다.

- `surfaceTint = Color.Transparent`로 색상 tint를 막는다. Surface·AlertDialog·NavigationBar는 `tonalElevation = 0.dp`를 명시한다. 그림자가 필요하면 shadow/button elevation을 별도로 지정한다.
- 화면에서 기존 `colorResource`로 지정한 background/text/icon 색상은 계속 사용한다. dark background `#111111`와 M2 surface `#121212`를 합치지 않는다.
- `MaterialTheme.colors`는 `colorScheme`으로 바꾼다. M3에는 `ContentAlpha`·`LocalContentAlpha`가 없으므로 기본 body content는 `LocalContentColor`에 onBackground의 0.87 alpha를 제공한다. Scaffold의 기본 content와 TopAppBar의 뒤로가기·일반 제목도 0.87, AlertDialog 본문은 onSurface의 0.6 alpha, 확인·취소는 0.87로 명시한다. 기존에 직접 지정한 Text color·Icon tint와 읽은 카페 글의 0.4 alpha는 보존한다.

## 타이포그래피와 Shapes 매핑

| M2 | M3 | 크기 / weight | lineHeight / letterSpacing (sp) |
| --- | --- | --- | --- |
| h1 | displayLarge | 30sp / Bold | 기존처럼 미지정 |
| h2 | displayMedium | 24sp / SemiBold | 기존처럼 미지정 |
| h3 | displaySmall | 20sp / Medium | 기존처럼 미지정 |
| h4 | headlineLarge | 16sp / Medium | 기존처럼 미지정 |
| h5 | headlineMedium | 24sp / Normal | 24 / 0 |
| h6 | headlineSmall | 20sp / Medium | 24 / 0.15 |
| subtitle1 | titleLarge | 16sp / Normal | 24 / 0.15 |
| subtitle2 | titleMedium | 14sp / Medium | 24 / 0.1 |
| body1 | bodyLarge | 14sp / Normal | 기존처럼 미지정 |
| body2 | bodyMedium | 14sp / Normal | 기존처럼 미지정 |
| button | labelLarge | 14sp / Medium | 16 / 1.25 |
| caption | bodySmall | 12sp / Normal | 16 / 0.4 |
| overline | labelSmall | 10sp / Normal | 16 / 1.5 |

M3의 추가 슬롯 titleSmall은 subtitle2, labelMedium은 caption과 같은 값으로 채운다. 모든 fontFamily는 기존 FontFamily.Default다. M2 기본 스타일의 lineHeightStyle(Center/None)·font padding 비활성화를 유지하고, 기존 사용자 정의 스타일에는 새 lineHeight·letterSpacing을 추가하지 않는다. M2 타입을 직접 참조한 기존 화면은 없으며, M3 TopAppBar의 기본 titleLarge 대신 headlineSmall(h6)을 일반 제목에, AlertDialog 본문에는 bodyMedium(body2), 확인·취소에는 bodyLarge를 명시한다. TextField는 bodyLarge, Button 내부 Text는 M3의 labelLarge로 기존 body1·button 스타일을 유지한다.

Shapes의 small 4dp·medium 12dp·large 22dp는 그대로다. M3 추가 슬롯 extraSmall은 4dp, extraLarge는 22dp다. AlertDialog에는 medium(12dp), 북마크 버튼에는 기존 8dp를 명시한다.

## 컴포넌트와 의존성

- Scaffold, Text, Icon, IconButton, Surface, Button, TextField, AlertDialog, CircularProgressIndicator를 M3로 전환했다. Divider는 HorizontalDivider, BottomNavigation/Item은 NavigationBar/Item으로 바꿨다. 현재 Card·OutlinedTextField 사용처는 없다.
- accompanist TopAppBar를 M3 TopAppBar로 교체했다. 상/하단바 높이는 56dp이고 상단바는 스크롤 동작 없이 elevation 0이다. 뒤로가기 아이콘 슬롯 너비를 지정해 제목 시작 위치 72dp를 유지한다.
- NavigationBar는 기존 XML background를 명시하고 tint 없이 8dp 그림자를 유지한다. indicator는 Transparent이며 내부 Row로 M3 기본 8dp 항목 간격을 제거해 기존 5개 탭의 균등 폭을 유지한다. 아이콘 drawable·선택 판단·navigation 콜백은 그대로다.
- MainActivity의 enableEdgeToEdge·setSystemBarsColor는 유지한다. M2의 기존 무인셋 overload와 같은 동작을 위해 Scaffold.contentWindowInsets·TopAppBar.windowInsets·NavigationBar.windowInsets는 모두 0이다. 시스템 바를 중복해서 패딩하지 않는다.
- TextField는 기존 text/cursor/indicator 색상과 투명 container, 14sp 스타일을 명시한다. Button은 48dp 높이·8dp 모서리·16/8dp content padding과 M2 elevation(default 2, pressed 8, focused/hovered 4, disabled 0dp)을 유지한다.
- 로딩 원은 기존 색상에 투명 track·Square stroke cap·gap 0dp를 명시한다. M3 자체의 회전 애니메이션 타이밍은 M2와 다를 수 있다.
- app·designsystem의 직접 `androidx.compose.material:material`, app의 accompanist-insets-ui 및 미사용 catalog alias/version을 제거했다. material-icons-extended와 XML theme용 com.google.android.material은 유지한다. Compose convention은 이미 M3를 제공하므로 변경하지 않는다.
- toolbar-compose 2.3.5는 캐시의 Gradle module/POM에서 런타임 Material 2 의존성을 선언한다. 전이 의존성은 제외하지 않으며 CollapsingToolbarScaffold 호출·스크롤 동작을 유지한다. 따라서 앱 소스에 M2 component import가 없어도 runtime classpath에 M2가 남을 수 있다. coil·compose-shimmer는 유지한다.

라이트/다크 Preview를 기존 component에 적용하고 북마크 입력 Preview를 추가했다. M3 AlertDialog의 기본 본문 baseline·버튼 영역 여백, ripple·로딩 애니메이션·텍스트 렌더링에는 시각 차이가 남을 수 있다. 다이얼로그 버튼은 M3의 외곽 24dp padding에 기존 padding을 중복하지 않는다. 빌드 및 실기기 라이트/다크 비교는 [스모크 체크리스트](../../migration/smoke-checklist.md)의 기존 항목으로 별도 확인한다.

## 현재 포함된 구현과 리소스

- `theme/Color.kt`, `Shape.kt`, `Theme.kt`, `Type.kt`: Material 3 ColorScheme·Shapes·Typography와 `ChimtubeWorldTheme(darkTheme: Boolean = false, content)`를 제공한다.
- `component/ReUseableView.kt`: 모델·Paging·navigation에 의존하지 않는 `TitleTextWithExplain`, `RowList`, `RowItemCollocate`와 theme 적용 Preview.
- `res/values/colors.xml`, `values-night/colors.xml`: 기존 파일 전체를 값·이름·qualifier 변경 없이 이동했다. app의 XML theme도 리소스 병합으로 이 색상을 사용한다. 런처 전용 `ic_launcher_background`는 app에 유지한다.
- `drawable-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}`: `youtube`, `chzzk`, `twitch`, `webtoon`, `cafe`, `store`와 각각의 `_darker` 변형(12종, 60파일). 현재 탭 chrome과 영상 종류 표시에 쓰이며 미사용 twitch 탭 변형도 같은 아이콘 묶음으로 보존한다.
- `drawable/ic_close.xml`, `ic_twitch_bedge.xml`: 현재 참조는 없지만 범용 닫기·플랫폼 배지 아이콘으로 함께 이동한다.
- app에 남긴 리소스: `webtoon_cover_1..5`(웹툰 화면), `marple`, `naver`(스토어 화면), `ic_logo`(현재 미사용 로고), `splash`(XML splash), `ic_launcher_foreground`와 mipmap·`ic_launcher_background`(런처), `themes.xml`·`values-night/themes.xml`(앱 XML theme), `attrs.xml`과 `dimens.xml`의 `pageMargin`·`pagerWidth`(공유 사용처 없음). 단일 화면 자산은 feature 분리 시 이동을 검토한다.

## 현재 구현 참고

- [현재 소스](../../../core/designsystem/src/main/java/com/sghore/chimtubeworld/core/designsystem)
- [현재 리소스](../../../core/designsystem/src/main/res)
- 참고 템플릿: `core/designsystem/build.gradle.kts`, `core/designsystem/src/main/AndroidManifest.xml`, `core/designsystem/src/main/java/com/sg/androidcomposetemplate/core/designsystem`.
