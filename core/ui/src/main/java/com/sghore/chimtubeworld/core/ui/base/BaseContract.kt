package com.sghore.chimtubeworld.core.ui.base

// 화면에 지속적으로 표시되는 상태를 정의하는 공통 contract다. 각 feature의 상태 class가 구현한다.
interface UiState

// 사용자 입력이나 화면에서 발생한 요청을 정의하는 공통 contract다. 각 feature의 event class가 구현한다.
interface UiEvent

// 화면 이동, Snackbar처럼 상태로 보관하지 않는 단발성 UI 효과를 정의하는 공통 contract다.
interface UiSideEffect
