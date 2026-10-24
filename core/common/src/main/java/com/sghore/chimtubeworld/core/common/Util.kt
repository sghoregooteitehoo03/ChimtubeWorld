package com.sghore.chimtubeworld.core.common

/*
 * 여러 모듈에서 공통으로 사용할 수 있고, 의존성이 가벼운 유틸만 추가합니다.
 *
 * 추가하기 좋은 항목:
 * - 순수 Kotlin 기반 문자열, 숫자, 날짜 포맷 헬퍼.
 * - 여러 모듈에서 사용하는 안전한 변환 헬퍼.
 * - 특정 기능 의미를 담지 않는 컬렉션 확장 함수.
 *
 * 제외해야 하는 항목:
 * - Android Context에 의존하는 헬퍼.
 * - Compose, UI, 디자인 시스템 관련 유틸.
 * - 네트워크, 데이터베이스, 영속성 전용 헬퍼.
 * - 특정 feature에만 속하는 로직.
 */

fun getVideoId(url: String): String? {
    val regex = Regex(
        "(?:https?://)?(?:www\\.)?(?:youtube\\.com|youtu\\.be)/(?:watch\\?v=|embed/|v/|shorts/|.+\\?v=)?([\\w-]{11})"
    )
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value
}