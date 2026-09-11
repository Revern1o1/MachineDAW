#!/usr/bin/env bash
# Ensures gradle-wrapper.jar is present (Gradle 8.9 official jar).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JAR="$ROOT/gradle/wrapper/gradle-wrapper.jar"
B64="$ROOT/gradle/wrapper/gradle-wrapper.jar.b64"
if [[ -f "$JAR" && -s "$JAR" ]]; then
  echo "gradle-wrapper.jar already present ($(wc -c < "$JAR") bytes)"
  exit 0
fi
mkdir -p "$ROOT/gradle/wrapper"
if [[ -f "$B64" ]]; then
  base64 -d "$B64" > "$JAR" 2>/dev/null || base64 --decode "$B64" > "$JAR"
  echo "Restored gradle-wrapper.jar from .b64 ($(wc -c < "$JAR") bytes)"
  exit 0
fi
curl -fsSL -o "$JAR" \
  "https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar"
echo "Downloaded gradle-wrapper.jar ($(wc -c < "$JAR") bytes)"
