#!/usr/bin/env bash
set -Eeuo pipefail

repo_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_dir"

exec 9>"/tmp/studio-api-cd.lock"
if ! flock -n 9; then
  echo "이미 배포 확인이 실행 중입니다."
  exit 0
fi

if [[ "$(git branch --show-current)" != "main" ]]; then
  echo "오류: EC2 저장소는 main 브랜치여야 합니다." >&2
  exit 1
fi

if [[ ! -f .env ]]; then
  echo "오류: 저장소 루트에 런타임 설정 .env가 필요합니다." >&2
  exit 1
fi

compose=(docker compose --env-file .env)
"${compose[@]}" config --quiet
"${compose[@]}" pull api
"${compose[@]}" up -d --no-build api
"${compose[@]}" ps api
