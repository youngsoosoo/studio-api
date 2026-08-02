# studio-api

studio 포트폴리오 백엔드 API. **Spring Boot 3.5 + Java 17 + Gradle + JPA + PostgreSQL** 스택으로 구성되어 있다.
포트폴리오 콘텐츠는 PostgreSQL에 저장된다. 애플리케이션 시작 시에는 기존 스키마를 검증할 뿐
초기 데이터나 스키마를 자동 생성·변경하지 않는다. 포트폴리오 조회 API는 읽기 전용이지만,
관리자 이미지 업로드 API를 호출하면 이미지 메타데이터와 대상 연결 정보가 DB에 저장된다.

## 요구 사항

- Java 17 (Gradle 툴체인이 강제)
- 접근 가능한 기존 PostgreSQL 14 이상 (`bootRun`과 API 컨테이너 실행 시 필요. 테스트는 H2 사용)
- EC2 API 배포 시 Docker Engine과 Docker Compose 플러그인

## 기존 PostgreSQL 연결

이 저장소는 PostgreSQL을 생성하거나 관리하지 않는다. `docker-compose.yml`도 API 서비스만 정의하며
이미 EC2에서 실행 중인 PostgreSQL 컨테이너와 그 볼륨을 선언하거나 변경하지 않는다.

애플리케이션 시작 시 Hibernate는 `ddl-auto: validate`로 스키마를 검증할 뿐 생성하거나 수정하지 않는다.
포트폴리오 조회 API는 읽기 전용이며, 관리자 이미지 업로드 API를 실제 호출할 때만 이미지 메타데이터와
대상 연결 정보가 DB에 저장된다.

- `./gradlew test`와 `./gradlew build`의 테스트는 H2를 사용하므로 EC2 PostgreSQL에 연결하지 않는다.
- EC2 보안 그룹에서 PostgreSQL 포트 `5432`를 외부에 공개하지 않는다.
- 운영 DB 스키마와 데이터 변경은 애플리케이션 배포와 분리된 승인 절차에서만 수행한다.
- 이 저장소에는 운영 데이터, 실행용 SQL, DB dump와 비밀번호를 보관하지 않는다.

### 시크릿 관리

- `.env`와 `.env.production` 등 모든 환경 파일은 저장소에 커밋하지 않는다.
- EC2의 프로젝트 디렉터리에 `.env`를 직접 만들고 `chmod 600 .env`로 권한을 제한한다.
- GitHub Actions에서는 저장소 또는 배포 Environment의 Secrets에 값을 등록한다.
- 시크릿이 Git 이력이나 공개 화면에 노출됐다면 파일 삭제로 끝내지 말고 즉시 폐기·재발급한다.

## EC2 API 서버 배포

`Dockerfile`은 Spring Boot 실행 이미지를 만들고, `docker-compose.yml`은 `api` 컨테이너와 업로드 전용
볼륨만 관리한다. Compose 파일에는 PostgreSQL 서비스나 PostgreSQL 볼륨이 없다.

### 연결 전제

Compose는 EC2 Linux의 `network_mode: host`를 사용한다. 따라서 API 컨테이너의 `localhost`는
EC2 호스트와 동일하다. 기존 PostgreSQL 컨테이너가 `127.0.0.1:5432`처럼 호스트 포트를 게시하고 있다면
다음 JDBC URL로 연결할 수 있다.

```text
jdbc:postgresql://localhost:5432/<database>
```

PostgreSQL이 다른 호스트 포트를 사용한다면 `DB_URL`의 포트만 맞춘다. 기존 PostgreSQL 컨테이너의
네트워크, 환경 변수 또는 볼륨은 변경하지 않는다.

### 1. EC2 환경 변수 준비

저장소 루트에 `.env`를 직접 만든다. 실제 값은 저장소에 커밋하지 않는다.

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/<database>
DB_USER=<existing-user>
DB_PASSWORD=<existing-password>
SERVER_PORT=8080
APP_PUBLIC_BASE_URL=https://<api-domain>
ADMIN_KEY=<strong-random-key>
```

`UPLOAD_DIR`은 Compose가 `/app/uploads`로 고정하며 `studio-api-uploads` named volume에 보존한다.

### 2. 빌드 및 실행

```bash
chmod 600 .env
docker compose build api
docker compose up -d api
docker compose ps
docker compose logs --tail=100 api
```

위 명령은 `api` 서비스만 대상으로 한다. 기존 PostgreSQL 컨테이너에는 Compose 명령을 실행하지 않는다.

### 3. 새 버전 배포

```bash
git pull
docker compose build --pull api
docker compose up -d api
docker compose logs --tail=100 api
```

### 4. 확인 및 운영 주의

```bash
curl http://localhost:8080/api/portfolio
```

- `docker compose stop api`는 API 컨테이너만 중단한다.
- `docker compose down`도 이 파일에 정의된 API 리소스만 대상으로 하지만, 업로드 볼륨 보호를 위해
  `docker compose down -v`는 실행하지 않는다.
- host network에서는 API 포트가 EC2 호스트에 직접 열린다. 운영 환경에서는 Nginx 등으로 80/443만
  공개하고 `SERVER_PORT`는 보안 그룹에서 외부 접근을 제한하는 구성을 권장한다.

## 환경 설정

런타임 설정은 환경 변수로 주입한다. 로컬 기본값은 `src/main/resources/application.yml`에 정의되어
있으며, EC2에서는 기존 PostgreSQL 접속 정보로 반드시 오버라이드한다.

| 변수                  | 기본값                                            | 비고                                          |
|-----------------------|---------------------------------------------------|-----------------------------------------------|
| `DB_URL`              | `jdbc:postgresql://localhost:5432/portfolio`      | JDBC URL                                      |
| `DB_USER`             | `portfolio_user`                                  |                                               |
| `DB_PASSWORD`         | **_(필수 · 기본값 없음)_**                        | 미설정 시 **기동 실패**. 설정 파일에 비밀번호를 두지 않는다 |
| `SERVER_PORT`         | `8080`                                            |                                               |
| `APP_PUBLIC_BASE_URL` | `http://localhost:8080`                           | 업로드 이미지 URL 프리픽스                    |
| `ADMIN_KEY`           | _(빈 값)_                                         | 비어 있으면 이미지 업로드 API 비활성(fail closed) |
| `UPLOAD_DIR`          | `./uploads`                                       | 업로드 파일 저장 경로. Compose에서는 `/app/uploads`로 고정 |

> 🔐 `DB_PASSWORD` 는 **의도적으로 기본값이 없다.** 비어 있으면
> `DataSourceSecretCheck`가 명확한 오류와 함께 기동을 중단한다.
> 커밋된 설정의 비밀번호로 애플리케이션이 조용히 동작하는 상황을 막기 위함이다.

로컬 설정은 프로젝트 루트의 `.env`에서 읽으며 운영체제 환경 변수가 `.env`보다 우선한다.
추가 오버라이드가 필요하면 `application.yml` 옆에 **커밋하지 않는**
`application-local.yml`을 만들고 `--spring.profiles.active=local`로 활성화한다.

## 실행

```powershell
.\gradlew.bat test                   # 테스트 프로파일(H2). 외부 DB에 연결하지 않음
.\gradlew.bat build                  # 테스트 포함 전체 빌드
```

로컬에서 기존 PostgreSQL로 연결할 때는 프로젝트 루트의 `.env`에 실제 접속 정보를 넣는다.
EC2 DB가 `127.0.0.1`에만 열려 있다면 먼저 SSH 터널을 사용한다.

```bash
ssh -i key.pem -N -L 5432:localhost:5432 ec2-user@<EIP>
```

`.env`가 준비되어 있다면 별도의 PowerShell 환경 변수 설정 없이 기동할 수 있다:

```powershell
.\gradlew.bat bootRun                # 준비된 DB 스키마를 검증한 뒤 :8080 기동
```

셸 환경 변수로 직접 주입할 수도 있다:

```powershell
$env:DB_PASSWORD = "<비밀번호>"
.\gradlew.bat bootRun
```

> 완전히 새 DB에는 먼저 검토·승인된 SQL로 스키마를 준비해야 한다. 애플리케이션은 스키마나
> 초기 콘텐츠를 자동으로 생성하지 않는다.

## 엔드포인트

읽기 API는 모두 `{status, data, error}` 봉투로 감싼다.

| 메서드 | 경로                                | 설명                                        |
|--------|-------------------------------------|---------------------------------------------|
| GET    | `/api/portfolio`                    | 전체 포트폴리오 집계 (프론트가 사용)        |
| GET    | `/api/portfolio/{section}`          | 섹션별 조회 (profile, about, projects 등)   |
| GET    | `/api/portfolio/projects/{slug}`    | 프로젝트 상세(케이스 스터디). 없으면 404    |
| POST   | `/api/admin/images`                 | 이미지 업로드 (multipart, `X-Admin-Key` 필요) |

정적 파일: 업로드된 이미지는 `GET /files/{stored_name}` 로 서빙된다.

### 이미지 업로드

`X-Admin-Key` 헤더(= `ADMIN_KEY`)로 보호된다. `target` 으로 업로드와 동시에 연결한다.

```bash
# 프로필 사진
curl -X POST -H "X-Admin-Key: $ADMIN_KEY" \
  -F "file=@me.jpg" "http://localhost:8080/api/admin/images?target=avatar"

# 프로젝트 카드 썸네일
curl -X POST -H "X-Admin-Key: $ADMIN_KEY" \
  -F "file=@thumb.png" "http://localhost:8080/api/admin/images?target=thumbnail&project=sample-project"

# 프로젝트 상세 이미지(아키텍처/스크린샷)
curl -X POST -H "X-Admin-Key: $ADMIN_KEY" \
  -F "file=@arch.png" "http://localhost:8080/api/admin/images?target=project-image&project=sample-project&alt=구조도"
```

허용 형식: `jpg`, `jpeg`, `png`, `webp` (확장자 + Content-Type + 파일 시그니처 검사), 최대 5MB.
실행 가능한 콘텐츠를 포함할 수 있는 SVG 업로드는 허용하지 않는다.
업로드 응답의 `data.url` 은 절대 URL(`{APP_PUBLIC_BASE_URL}/files/...`)이다.

## 패키지 구조

```
com.studio.api
├── StudioApiApplication.java   Spring Boot 진입점
├── common/                     크로스컷팅 타입
│   ├── ApiResponseDto.java        제네릭 {status, data, error} 응답 봉투
│   ├── ErrorResponseDto.java
│   ├── GlobalExceptionHandler.java  예외 → 에러 봉투 (404/401/400/413/500)
│   ├── NotFoundException.java  UnauthorizedException.java
├── config/
│   ├── WebCorsConfig.java      localhost:5173 CORS
│   ├── WebConfig.java          /files/** 정적 서빙 + admin 인터셉터 등록
│   └── AdminKeyInterceptor.java  /api/admin/** X-Admin-Key 검사
├── image/                      이미지 업로드 모듈
│   ├── controller/             관리자 이미지 업로드 API
│   ├── dto/                    업로드 응답 모델
│   ├── entity/                 이미지 메타데이터 JPA 엔티티
│   ├── repository/             이미지 메타데이터 리포지토리
│   └── service/                파일 저장·연결·URL 변환
└── portfolio/
    ├── controller/PortfolioController.java
    ├── dto/                    응답 record (ProfileDto, ProjectDetailResponseDto 등)
    ├── entity/                 JPA 엔티티 (*Entity)
    ├── repository/             Spring Data 리포지토리
    └── service/
        ├── PortfolioReader.java         읽기 인터페이스
        ├── DatabasePortfolioService.java  DB 조회
        └── PortfolioMapper.java         엔티티 → DTO
```

## 데이터베이스 스키마

- 슬러그(`focus`, `t-2024-join` 등)를 공개 `id` 로 유지하되 내부 PK는 대리키(BIGINT IDENTITY).
- `List<String>` 필드는 자식 테이블 + `sort_order`(0부터 연속, `@OrderColumn`)로 정규화.
- 이미지는 소유측 FK(`profile.avatar_image_id`, `project.thumbnail_image_id`,
  `project_image.image_id`)로 `image` 테이블을 참조.
- JPA 엔티티(`portfolio/entity/`)는 기존 DB 스키마와의 매핑을 정의하며 기동 시 매핑을 검증한다.

## 테스트 전략

- `test` 프로파일은 외부 DB와 연결하지 않고 H2(`MODE=PostgreSQL`)에 격리된 테스트 스키마를 만든다.
- `PortfolioControllerTest`는 개인 정보가 없는 mock 응답으로 집계·프로젝트 상세(200/404)를 검증한다.
- `AdminImageControllerTest`는 합성 테스트 데이터로 인증(401)·업로드(201)·타입 검증(400)·연결을 검증한다.

## 컨벤션

- 브랜치: `feature/{TICKET_ID}-{short-description}` 형식으로 `dev` 에서 분기
- 커밋: Conventional Commits (`chore:`, `feat:`, `fix:`, `docs:`, `test:`)
- 협업 워크플로는 [studio-docs](https://github.com/youngsoosoo/studio-docs)
  의 문서를 새 티켓 시작 전에 먼저 확인할 것
