#!/usr/bin/env bash
# Run a single-file Kotlin script (.kts) like a bash script.
# Scripts get a default classpath with Clikt (argument parsing) and stdlib (file I/O).
# Usage:
#   ./scripts/run-kotlin.sh scripts/myscript.kts
#   ./scripts/run-kotlin.sh scripts/myscript.kts -- arg1 arg2
#
# Requires: Kotlin on PATH (e.g. sdk install kotlin, or brew install kotlin).
# First run: Gradle (./gradlew or gradle) to build scripts/.kotlin-classpath.

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CLASSPATH_FILE="$SCRIPT_DIR/.kotlin-classpath"

if [[ $# -eq 0 ]]; then
  echo "Usage: $0 <file.kts> [-- script-args...]" >&2
  echo "Example: $0 scripts/hello.kts" >&2
  exit 1
fi

KTS_FILE="$1"
shift

if [[ "$1" == "--" ]]; then
  shift
fi

if [[ ! -f "$KTS_FILE" ]]; then
  if [[ -f "$REPO_ROOT/$KTS_FILE" ]]; then
    KTS_FILE="$REPO_ROOT/$KTS_FILE"
  else
    echo "Error: script not found: $KTS_FILE" >&2
    exit 1
  fi
fi

# Resolve to absolute path so kotlinc runs with a stable path
KTS_ABS="$(cd "$(dirname "$KTS_FILE")" && pwd)/$(basename "$KTS_FILE")"
cd "$REPO_ROOT"

# Ensure default classpath exists (Clikt, etc.)
if [[ ! -f "$CLASSPATH_FILE" ]]; then
  if [[ -x "$REPO_ROOT/gradlew" ]]; then
    "$REPO_ROOT/gradlew" -q writeScriptClasspath
  elif command -v gradle >/dev/null 2>&1; then
    gradle -q writeScriptClasspath
  else
    echo "Error: No Gradle (run ./gradlew or install Gradle). Need to build script classpath once." >&2
    exit 1
  fi
fi

SCRIPT_CP=""
[[ -f "$CLASSPATH_FILE" ]] && SCRIPT_CP="$(cat "$CLASSPATH_FILE")"

if command -v kotlinc >/dev/null 2>&1; then
  # Pass script args after -- so kotlinc doesn't consume options like --help
  if [[ -n "$SCRIPT_CP" ]]; then
    exec kotlinc -cp "$SCRIPT_CP" -script "$KTS_ABS" -- "$@"
  else
    exec kotlinc -script "$KTS_ABS" -- "$@"
  fi
else
  echo "Error: kotlinc not found. Install Kotlin (e.g. sdk install kotlin or brew install kotlin)." >&2
  exit 1
fi
