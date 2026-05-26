# studio-api

studio 포트폴리오 백엔드 API. **Spring Boot 3.5 + Java 17 + Gradle + JPA + PostgreSQL** 스택으로 구성되어 있다.

## 요구 사항

- Java 17 (Gradle 툴체인이 강제)
- `DB_URL` 로 접근 가능한 PostgreSQL 14 이상 (`bootRun` 시에만 필요. 테스트는 H2 사용)

## 환경 설정

런타임 설정은 환경 변수로 주입된다. 기본값은 `src/main/resources/application.yml`
에 정의되어 있다.

| 변수            | 기본값                                         | 비고                                          |
|-----------------|------------------------------------------------|-----------------------------------------------|
| `DB_URL`        | `jdbc:postgresql://localhost:5432/studio`      | JDBC URL                                      |
| `DB_USERNAME`   | `studio`                                       |                                               |
| `DB_PASSWORD`   | _(빈 값)_                                      | 환경 변수로 주입. 시크릿은 절대 커밋 금지     |
| `SERVER_PORT`   | `8080`                                         |                                               |

로컬 오버라이드가 필요하면 `application.yml` 옆에 **커밋하지 않는**
`application-local.yml` 을 만들고 `--spring.profiles.active=local` 로 활성화한다.
`.gitignore` 가 `.env*` 와 `application-local.*` 의 커밋을 차단한다.

## 실행

```bash
./gradlew bootRun                    # :8080 포트에서 설정된 DB 로 기동
./gradlew test                       # 테스트 프로파일(H2) 로 유닛 + 슬라이스 테스트 실행
./gradlew build                      # 테스트 포함 전체 빌드
```

## 엔드포인트

| 메서드 | 경로          | 설명                       |
|--------|---------------|----------------------------|
| GET    | `/api/health` | 라이브니스 응답, 인증 불필요 |

응답 예시:

```json
{
  "status": "success",
  "data": {
    "status": "UP",
    "service": "studio-api",
    "time": "2026-05-26T11:50:00Z"
  }
}
```

## 패키지 구조

```
com.studio.api
├── StudioApiApplication.java   Spring Boot 진입점
├── common/                     크로스컷팅 타입
│   ├── ApiResponse.java        제네릭 {status, data, error} 응답 봉투
│   └── ErrorPayload.java
├── config/                     크로스컷팅 설정을 둘 자리
└── health/
    └── HealthController.java   GET /api/health
```

## 테스트 전략

- `StudioApiApplicationTests` 는 `test` 프로파일로 전체 컨텍스트를 부팅한다.
  이 프로파일은 H2 + `ddl-auto=create-drop` 로 스왑되므로 PostgreSQL 없이도
  실행된다.
- `HealthControllerTest` 는 `@WebMvcTest` 슬라이스로 `/api/health` 응답 봉투의
  형태를 검증한다.

## 컨벤션

- 브랜치: `feature/{TICKET_ID}-{short-description}` 형식으로 `dev` 에서 분기
- 커밋: Conventional Commits (`chore:`, `feat:`, `fix:`, `docs:`, `test:`)
- 협업 워크플로는 [studio-docs](https://github.com/youngsoosoo/studio-docs)
  의 문서를 새 티켓 시작 전에 먼저 확인할 것
