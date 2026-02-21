#!/usr/bin/env bash
set -euo pipefail

: "${APP_DIR:?Set APP_DIR to the repo location on the EC2 host}"

cd "$APP_DIR"

if [[ -f .env ]]; then
  echo "Loading .env"
  set -o allexport
  # shellcheck disable=SC1091
  source .env
  set +o allexport
fi

docker compose pull || true

docker compose up --build -d

docker compose ps
