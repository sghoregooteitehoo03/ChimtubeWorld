# :core:model

## 역할

navigation parameter, feature public contract, 여러 모듈에서 공유하는 순수 모델을 제공한다. namespace와 package는 `com.sghore.chimtubeworld.core.model`이다.

## 현재 포함된 구현

- `Channel.kt`: 채널 정보.
- `Playlist.kt`: 재생목록 정보.
- `Post.kt`: 카페 게시글 정보와 기존 읽음 상태.
- `CafeCategory.kt`: 카페 카테고리 정보.
- `LinkInfo.kt`: Firestore에서 읽는 공유 링크 정보.
- `GoodsChannelInfo.kt`: 스토어 채널 정보. 이미지 식별자는 기존 `Int` 그대로이며 Android 타입에 의존하지 않는다.

Step 4에서는 package만 바꾸고 필드명·타입·기본값·생성자를 유지한다. 특히 Firestore `toObjects`에서 사용하는 `LinkInfo`의 모든 기본값과 no-arg 생성 동작을 보존한다.

## 포함·제외 기준

- feature API가 navigation contract로 노출하는 모델, domain repository·usecase가 반환하는 순수 모델, 여러 feature에서 공유하는 Android 비의존 모델을 포함한다.
- Room Entity, Retrofit DTO, Android와 Compose 의존 타입은 제외한다.
- 특정 feature 내부에서만 쓰는 `UiState`, 화면 표시 전용 가공 모델, data 구현 세부 타입은 제외한다.
- `Bookmark`와 `ReadHistory`는 Room Entity이므로 현재 `:app`에 유지한다.
- `Video`도 `bookmarks: List<Bookmark>`를 직접 참조하므로 현재 `:app`에 유지한다. Step 5의 entity/model 분리까지 기존 타입·필드·Gson 직렬화 동작을 보존하며 model → app 의존성을 만들지 않는다.
- `Resource`는 계층 공통 결과 래퍼로 `:core:common`에 둔다. 사용되지 않는 `Goods`는 삭제했다.

## 공개 경계와 의존성

- model은 feature API와 domain contract에 노출될 수 있으므로 기본 공개 범위를 사용한다.
- model은 다른 core, feature, app 모듈에 의존하지 않는다.
- `sg.android.library` convention만 적용하며 production 외부 라이브러리 의존성은 없다.
- DTO와 Entity 변환은 후속 `:core:data` mapper에서 담당한다.

## 구현과 템플릿 참고

- [현재 소스](../../../core/model/src/main/java/com/sghore/chimtubeworld/core/model)
- [app에 남은 모델·Entity](../../../app/src/main/java/com/sghore/chimtubeworld/data/model)
- 참고 템플릿: `core/model/build.gradle.kts`, `core/model/src/main/AndroidManifest.xml`, `core/model/src/main/java/com/sg/androidcomposetemplate/core/model/TemplateModel.kt`.
