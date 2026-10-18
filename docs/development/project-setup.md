# 프로젝트 시작 가이드

ChimtubeWorld의 로컬 빌드 준비를 설명한다. 구조는 [spec.md](../architecture/spec.md), 검증 기준은 [quality.md](quality.md)를 따른다.

## 빌드 환경

- AGP·라이브러리·SDK 버전은 [버전 카탈로그](../../gradle/libs.versions.toml)를 기준으로 한다. 해당 AGP를 지원하는 Android Studio와 SDK를 설치한다.
- Gradle wrapper는 저장소 설정을 사용한다. daemon JDK는 [JVM 설정](../../gradle/gradle-daemon-jvm.properties)의 toolchain을 사용하며, 로컬에 없으면 settings의 foojay resolver가 내려받는다.
- Android Java 소스·타깃 호환성은 기존과 같은 Java 17이다. build-logic 자체의 toolchain도 17을 사용한다.

## 로컬 설정

- 루트 `local.properties`에 `sdk.dir`과 `youtube_api_key`를 준비한다. 키는 `app/build.gradle.kts`에서 그대로 Java 문자열 표현식으로 전달하므로 값에 큰따옴표를 포함한다.

```properties
youtube_api_key="YOUR_YOUTUBE_API_KEY"
```

- 기존 `app/google-services.json`을 사용한다. google-services 플러그인을 유지하며 Firebase 프로젝트나 파일 내용을 임의로 바꾸지 않는다.
- `local.properties`의 실제 키를 문서·로그·커밋에 복사하지 않는다.
- 배포 중인 `applicationId`와 app namespace는 `com.sghore.chimtubeworld`로 유지한다. 새 모듈 namespace는 `com.sghore.chimtubeworld.<core|feature>.<name>`을 기준으로 한다.
- Room·Firestore·Manifest·버전·릴리스 설정의 보존 항목은 [마이그레이션 기준선](../migration/baseline.md)을 따른다. 새 템플릿 앱으로 식별자를 교체하는 작업이 아니다.

## 첫 빌드

```shell
./gradlew :app:assembleDebug
```

현재는 단일 app이다. core·feature 명령은 모듈 생성 후에만 실행한다. convention 구성과 Room 미적용 이유는 [build-logic.md](build-logic.md)를 따른다.

Codex 샌드박스에서는 네트워크·Java 실행 환경·Gradle lock 접근 제약으로 Gradle 실행이 불가능할 수 있다. 이 경우 정적 검증과 미실행 사유를 보고하고 Claude가 로컬에서 위 명령을 실행한다. 빌드·기기 테스트의 성공을 추정해서 보고하지 않는다.
