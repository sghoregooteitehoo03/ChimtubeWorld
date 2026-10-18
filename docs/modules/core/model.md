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
- `Bookmark.kt`: Room annotation을 제거한 순수 북마크 모델. 기존 필드·기본값과 id 기반 `equals` 본문을 그대로 유지한다.
- `Video.kt`: 영상 정보와 `bookmarks: List<Bookmark>`. 기존 필드명·타입·기본값과 `bookmarks`의 가변성을 유지한다.

Step 4에서 옮긴 모델의 필드명·타입·기본값·생성자를 유지한다. 특히 Firestore `toObjects`에서 사용하는 `LinkInfo`의 모든 기본값과 no-arg 생성 동작을 보존한다. Step 5에서 `Bookmark`를 Entity와 분리하고 `Video`도 이동했다. 두 모델의 필드 기반 Gson 직렬화 구조는 유지한다.

`Bookmark.equals`는 기존 `other as Bookmark?`와 `other?.id == id`를 그대로 둔다. nullable id·null 비교·다른 타입 비교의 기존 동작도 이번 이동에서 수정하지 않는다. 현재 `VideosViewModel`의 `List.indexOf`가 이 id 비교를 사용한다. `hashCode`는 별도로 추가하지 않고 기존 data class의 생성 동작을 유지한다. 기존 equals/hashCode 계약의 불일치를 이번 이동에서 수정하지 않는다.

## 포함·제외 기준

- feature API가 navigation contract로 노출하는 모델, domain repository·usecase가 반환하는 순수 모델, 여러 feature에서 공유하는 Android 비의존 모델을 포함한다.
- Room Entity, Retrofit DTO, Android와 Compose 의존 타입은 제외한다.
- 특정 feature 내부에서만 쓰는 `UiState`, 화면 표시 전용 가공 모델, data 구현 세부 타입은 제외한다.
- `BookmarkEntity`와 `ReadHistoryEntity`는 `:core:database`에 둔다. 순수 `ReadHistory` 모델은 생성하지 않는다. 읽음 기록의 행 자체를 domain·UI에 전달하는 사용처가 없고, 기존 `Post.isRead`로 읽음 여부를 표현하기 때문이다.
- `Resource`는 계층 공통 결과 래퍼로 `:core:common`에 둔다. 사용되지 않는 `Goods`는 삭제했다.

## 공개 경계와 의존성

- model은 feature API와 domain contract에 노출될 수 있으므로 기본 공개 범위를 사용한다.
- model은 다른 core, feature, app 모듈에 의존하지 않는다.
- `sg.android.library` convention만 적용하며 production 외부 라이브러리 의존성은 없다.
- DTO와 Entity 변환은 data 계층 mapper의 책임이다. 현재 `BookmarkEntity` ↔ `Bookmark` 변환은 app의 `data/mapper/BookmarkMapper.kt`에 `internal`로 두며 repository·PagingSource에서 사용한다. Step 7에서 repository와 함께 `:core:data`로 이동한다. model은 database에 의존하지 않는다.

## 구현과 템플릿 참고

- [현재 소스](../../../core/model/src/main/java/com/sghore/chimtubeworld/core/model)
- [북마크 mapper](../../../app/src/main/java/com/sghore/chimtubeworld/data/mapper/BookmarkMapper.kt)
- [Room Entity](../../../core/database/src/main/java/com/sghore/chimtubeworld/core/database/entity)
- 참고 템플릿: `core/model/build.gradle.kts`, `core/model/src/main/AndroidManifest.xml`, `core/model/src/main/java/com/sg/androidcomposetemplate/core/model/TemplateModel.kt`.
