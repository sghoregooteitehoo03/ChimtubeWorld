# :core:designsystem

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

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

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/presentation/ui/theme). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.
