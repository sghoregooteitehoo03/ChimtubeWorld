package com.sghore.chimtubeworld.core.navigation

/*
 * feature API가 화면 간 전달 결과의 타입과 범위를 선언할 때 사용하는 key다.
 * 각 feature는 object로 key를 선언해 문자열 충돌 없이 결과를 구분한다.
 */
abstract class NavigationResultKey<T : Any>
