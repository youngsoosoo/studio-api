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
- Actions가 EC2에 전송하는 `.env`는 `chmod 600`으로 권한을 제한한다.
- GitHub Actions에서는 저장소 또는 배포 Environment의 Secrets에 값을 등록한다.
- 시크릿이 Git 이력이나 공개 화면에 노출됐다면 파일 삭제로 끝내지 말고 즉시 폐기·재발급한다.

## main CI/CD와 EC2 배포

운영 이미지와 EC2 배포 기준은 `main` 브랜치뿐이다. `.github/workflows/main-ci-cd.yml`은 다음과 같이 동작한다.

1. CI는 `main` 대상 Pull Request에서 Java 17 Gradle 빌드와 H2 기반 테스트만 수행한다.
2. `main` push에서는 CI가 `main-<실행번호>-<짧은 SHA>` 버전을 생성하고 Docker Hub 게시까지 수행한다.
3. CI가 성공한 경우에만 CD가 `production` Environment를 열고 SSH로 EC2에 배포 파일을 전송한다.
4. CD는 EC2에서 지정된 버전의 이미지를 pull하고 `api`와 Nginx `proxy` 컨테이너를 재배포한다.

`latest` 태그는 만들거나 사용하지 않는다. 예를 들어 Actions 실행번호가 27이고 커밋 SHA가 `a1b2c3d...`이면
배포 버전은 `main-27-a1b2c3d`다. 전체 Git SHA 태그도 롤백 추적용으로 함께 게시한다.

GitHub Actions 권한은 `contents: read`뿐이며 Git 브랜치에 커밋하거나 push하지 않는다. EC2에는 저장소 clone,
systemd 타이머 또는 별도 배포 스크립트가 필요 없다. 워크플로가 `~/studio-api`를 만들고 필요한 파일을
전송한다.

`Dockerfile`은 CI에서 Spring Boot 실행 이미지를 만들고, `docker-compose.yml`은 EC2에서 `api`, Nginx
`proxy` 컨테이너와 업로드 전용 볼륨만 관리한다. Compose 파일에는 PostgreSQL 서비스나 PostgreSQL
볼륨이 없다.

### 연결 전제

Compose는 EC2 Linux의 `network_mode: host`를 사용한다. Nginx는 호스트의 80번 포트에서 요청을 받고
`127.0.0.1:8080`의 API로 전달한다. API 컨테이너의 `localhost`도 EC2 호스트와 동일하므로 기존
PostgreSQL 컨테이너가 `127.0.0.1:5432`처럼 호스트 포트를 게시하고 있다면 다음 JDBC URL로 연결할 수 있다.

```text
jdbc:postgresql://localhost:5432/<database>
```

PostgreSQL이 다른 호스트 포트를 사용한다면 `DB_URL`의 포트만 맞춘다. 기존 PostgreSQL 컨테이너의
네트워크, 환경 변수 또는 볼륨은 변경하지 않는다.

### 1. EC2 사전 조건

EC2에는 다음 항목만 준비한다. 기존 PostgreSQL 컨테이너와 데이터는 변경하지 않는다.

- Docker Engine과 Docker Compose 플러그인
- 배포 사용자가 비밀번호 없이 `docker` 명령을 실행할 수 있는 권한
- GitHub Actions에서 사용할 SSH 키의 공개 키가 `~/.ssh/authorized_keys`에 등록된 상태
- EC2 보안 그룹의 SSH 포트가 GitHub-hosted runner에서 접근 가능한 상태
- EC2 보안 그룹의 TCP 80 포트가 API 사용자에게 공개되고 8080과 5432는 외부에 공개되지 않은 상태
- 기존 PostgreSQL이 EC2 호스트의 `127.0.0.1:<port>`로 게시된 상태

### 2. Docker Hub와 GitHub Actions 설정

Docker Hub에 `studio-api` 저장소를 만든 뒤 GitHub 저장소의 **Settings → Secrets and variables → Actions**에
다음 Repository Variable과 CI용 Secret을 등록한다.

| 종류 | 이름 | 예시/설명 |
|------|------|-----------|
| Variable | `DOCKERHUB_IMAGE` | `youngsoosoo/studio-api` 형식의 전체 이미지 저장소 경로 |
| Variable | `DOCKERHUB_USERNAME` | Docker Hub 로그인 사용자명 |
| Variable | `EC2_HOST` | EC2의 고정 Public IP 또는 도메인 |
| Variable | `EC2_USER` | Amazon Linux는 보통 `ec2-user`, Ubuntu는 `ubuntu` |
| Repository Secret | `DOCKERHUB_TOKEN` | CI가 사용할 Docker Hub push access token |

`production` Environment에는 CD에서만 사용하는 다음 Environment Secret을 등록한다.

| 종류 | 이름 | 설명 |
|------|------|------|
| Environment Secret | `DOCKERHUB_PULL_TOKEN` | EC2가 이미지를 받을 때 사용할 pull 전용 access token |
| Environment Secret | `EC2_SSH_PRIVATE_KEY` | EC2 접속용 SSH 개인 키 전문 |
| Environment Secret | `EC2_KNOWN_HOSTS` | 검증한 EC2 SSH host key 한 줄 |
| Environment Secret | `EC2_APP_ENV` | 아래 형식의 운영 애플리케이션 환경 변수 전문 |

`EC2_APP_ENV` Secret 값은 다음과 같이 등록한다. 이미지 이름과 버전은 워크플로가 별도로 관리하므로
이 Secret에 넣지 않는다.

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/<database>
DB_USER=<existing-user>
DB_PASSWORD=<existing-password>
SERVER_PORT=8080
APP_PUBLIC_BASE_URL=http://<EC2-Elastic-IP-or-domain>
ADMIN_KEY=<strong-random-key>
```

Nginx가 외부 80번을 내부 8080번으로 전달하므로 `SERVER_PORT`는 `8080`을 유지한다. HTTPS를 적용하면
`APP_PUBLIC_BASE_URL`을 `https://<api-domain>`으로 변경한다.

SSH host key는 신뢰할 수 있는 경로로 EC2 fingerprint를 먼저 확인한 뒤 다음과 같이 얻어
`EC2_KNOWN_HOSTS`에 등록한다.

```bash
ssh-keyscan -H <ec2-host>
```

GitHub의 `production` Environment를 만들고 배포 브랜치를 `main`으로 제한한다. CI의 Docker Hub push에는
EC2 Secret이 노출되지 않으며, CD 작업이 시작될 때만 Environment 승인 규칙과 Secret이 적용된다.

### 3. 배포와 상태 확인

새 버전은 `main`에 merge 또는 push한다. 빌드와 테스트가 성공하면 버전 이미지 게시와 EC2 SSH 배포가
같은 워크플로에서 이어진다. EC2에는 다음 파일이 생성된다.

- `~/studio-api/docker-compose.yml`
- `~/studio-api/nginx/default.conf` — 80번에서 8080번으로 전달하는 Nginx 설정
- `~/studio-api/.env` — `EC2_APP_ENV`에서 생성, 권한 `600`
- `~/studio-api/image.env` — `DOCKERHUB_IMAGE`와 현재 `IMAGE_VERSION`

```bash
cd ~/studio-api
cat image.env
docker compose --env-file image.env ps api proxy
docker compose --env-file image.env logs --tail=100 api proxy
curl http://localhost/api/portfolio
```

### 4. 롤백과 운영 주의

GitHub Actions에서 **Run workflow**를 선택하고 `main` 브랜치와 Docker Hub에 존재하는 이전
`image_version`을 입력하면 CI는 건너뛰고 CD만 실행해 해당 버전으로 EC2를 재배포한다. `latest`로 태그를
옮기는 작업은 하지 않는다.

- `docker compose stop api proxy`는 API와 Nginx 프록시만 중단한다.
- `docker compose down`도 이 파일에 정의된 API 리소스만 대상으로 하지만, 업로드 볼륨 보호를 위해
  `docker compose down -v`는 실행하지 않는다.
- host network에서는 API의 8080번도 EC2 호스트에 바인딩되므로 보안 그룹에서는 80번만 공개하고
  8080번과 5432번은 외부 접근을 허용하지 않는다.
- 이 Nginx 설정은 HTTP 80번만 제공한다. HTTPS인 Vercel 프런트와 연결하려면 이후 도메인과 TLS 인증서를
  적용해 443번을 제공해야 한다.

## 환경 설정

런타임 설정은 환경 변수로 주입한다. 로컬 기본값은 `src/main/resources/application.yml`에 정의되어
있으며, EC2에서는 기존 PostgreSQL 접속 정보로 반드시 오버라이드한다.

| 변수                  | 기본값                                            | 비고                                          |
|-----------------------|---------------------------------------------------|-----------------------------------------------|
| `DB_URL`              | `jdbc:postgresql://localhost:5432/portfolio`      | JDBC URL                                      |
| `DB_USER`             | `portfolio_user`                                  |                                               |
| `DB_PASSWORD`         | **_(필수 · 기본값 없음)_**                        | 미설정 시 **기동 실패**. 설정 파일에 비밀번호를 두지 않는다 |
| `DOCKERHUB_IMAGE`     | **_(Compose 배포 시 필수)_**                      | Actions가 `image.env`에 기록                  |
| `IMAGE_VERSION`       | **_(Compose 배포 시 필수)_**                      | `main-<실행번호>-<짧은 SHA>` 형식             |
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

# 개별 문제 사례의 통합 Visual 이미지
# problemOrder와 visualOrder는 0부터 시작한다. 동일 visualOrder는 교체된다.
curl -X POST -H "X-Admin-Key: $ADMIN_KEY" \
  -F "target=problem-visual-image" \
  -F "project=sample-project" \
  -F "problemKind=problem" \
  -F "problemOrder=0" \
  -F "visualOrder=1" \
  -F "title=패킷 검증" \
  -F "alt=DNS 패킷 검증 화면" \
  -F "file=@packet.png" \
  "http://localhost:8080/api/admin/images"
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
│   ├── WebCorsConfig.java      로컬 개발 + Vercel 프런트 CORS
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
