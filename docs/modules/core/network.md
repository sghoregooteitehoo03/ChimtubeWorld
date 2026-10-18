# :core:network

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

## 역할

Retrofit 기반 API, DTO, remote datasource, 네트워크 객체 Hilt 제공을 담당한다.

## 포함 대상

- Retrofit endpoint를 정의하는 API interface.
- 네트워크 응답 전용 DTO. 기존 Gson converter와 응답 필드 매핑을 유지한다.
- API 호출을 감싸는 remote datasource contract와 구현체.
- OkHttpClient, Retrofit, API, datasource를 제공하는 Hilt module.

## 제외 대상

- domain model, mapper, repository 구현, Room 접근, feature UI state.
- remote/local 선택, caching, fallback 같은 repository 정책.

## 공개 경계와 의존성

- datasource contract와 DTO는 data가 remote source를 사용하기 위해 공개할 수 있지만, domain과 feature에 노출하지 않는다.
- Retrofit API와 datasource 구현, NetworkModule은 network 내부 구현으로 `internal`을 유지한다.
- DTO에서 공유 순수 모델로의 변환은 `:core:data` mapper가 담당한다.
- Gson을 유지하며 Moshi codegen과 새 JSON 변환 정책을 도입하지 않는다.
- 기존 YouTube·네이버 API의 base URL·endpoint·오류 정책을 이동 중 바꾸지 않는다. Firestore와 Jsoup 접근은 `:core:data`가 담당한다.
- network는 domain, model, data, feature, app에 의존하지 않는다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/data/retrofit). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.
