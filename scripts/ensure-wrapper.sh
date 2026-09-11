#!/usr/bin/env bash
# Ensures gradle-wrapper.jar is present (official Gradle 8.9 jar).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JAR="$ROOT/gradle/wrapper/gradle-wrapper.jar"
if [[ -f "$JAR" && -s "$JAR" ]]; then
  echo "gradle-wrapper.jar already present ($(wc -c < "$JAR") bytes)"
  exit 0
fi
mkdir -p "$ROOT/gradle/wrapper"
echo "Downloading official gradle-wrapper.jar (Gradle 8.9)..."
curl -fsSL -o "$JAR" \
  "https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar"
echo "Downloaded gradle-wrapper.jar ($(wc -c < "$JAR") bytes)"
