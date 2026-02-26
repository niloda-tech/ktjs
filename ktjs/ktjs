#!/usr/bin/env bash
# Runner for *.cli.kts scripts — scaffolds a Kotlin/JS subproject, builds, and runs with bun.
#
# Usage (from repo root):
#   ktjs/run-cli.sh scripts/hello.cli.kts -- --name World
#
# Or via shebang in .cli.kts files (when run from repo root):
#   #!/usr/bin/env -S bash -c '"$(git rev-parse --show-toplevel)/ktjs/run-cli.sh" "$0" "$@"'
#
# Requires: bun (or node), JDK 21+, Gradle wrapper at repo root.
# REPO_ROOT: from git if available, else from this script's directory (..).

set -euo pipefail

# Resolve script path (follow symlinks) so REPO_ROOT is correct when ktjs is installed in ~/.local/bin
SOURCE="${BASH_SOURCE[0]:-$0}"
while [[ -L "$SOURCE" ]]; do
    SCRIPT_DIR="$(cd "$(dirname "$SOURCE")" && pwd)"
    SOURCE="$(readlink "$SOURCE")"
    [[ "$SOURCE" != /* ]] && SOURCE="$SCRIPT_DIR/$SOURCE"
done
SCRIPT_DIR="$(cd "$(dirname "$SOURCE")" && pwd)"
REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

KTJS_DIR="$REPO_ROOT/ktjs"
GRADLEW="$REPO_ROOT/gradlew"

if [[ $# -eq 0 ]]; then
    echo "Usage: $(basename "$0") <script.cli.kts> [-- args...]" >&2
    exit 1
fi

SCRIPT_FILE="$1"
shift

if [[ "${1-}" == "--" ]]; then
    shift
fi

# Resolve script to absolute path
if [[ ! "$SCRIPT_FILE" = /* ]]; then
    SCRIPT_FILE="$(cd "$REPO_ROOT" && cd "$(dirname "$SCRIPT_FILE")" && pwd)/$(basename "$SCRIPT_FILE")"
fi

if [[ ! -f "$SCRIPT_FILE" ]]; then
    echo "Error: script not found: $SCRIPT_FILE" >&2
    exit 1
fi

NAME="$(basename "$SCRIPT_FILE" .cli.kts)"

# Temp dir for script wrappers (persisted for Gradle cache); keyed by repo so multiple repos don't clash
WRAPPER_ROOT="${TMPDIR:-/tmp}/ktjs-scripts/$(echo -n "$REPO_ROOT" | cksum 2>/dev/null | awk '{print $1}' || echo default)"
mkdir -p "$WRAPPER_ROOT"
PROJ_DIR="$WRAPPER_ROOT/$NAME"
SRC_DIR="$PROJ_DIR/src/jsMain/kotlin"

# Symlink so Gradle's settings.gradle.kts can include :scripts:$NAME
mkdir -p "$KTJS_DIR/scripts"
ln -sfn "$PROJ_DIR" "$KTJS_DIR/scripts/$NAME"

# --- Scaffold subproject if it doesn't exist ---
if [[ ! -f "$PROJ_DIR/build.gradle.kts" ]]; then
    mkdir -p "$SRC_DIR"
    cat > "$PROJ_DIR/build.gradle.kts" << 'GRADLE'
plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":context"))
        }
    }
}
GRADLE
fi

# --- Transform .cli.kts → Main.kt (strip shebang) ---
mkdir -p "$SRC_DIR"
sed '1{/^#!/d;}' "$SCRIPT_FILE" > "$SRC_DIR/Main.kt"

# Preserve stdin (e.g. piped input) across Gradle build; restore before running the script.
exec 3<&0

# --- Build Kotlin → JS (stdin from /dev/null so piped input is not consumed by Gradle) ---
BUILD_TASK=":scripts:$NAME:compileProductionExecutableKotlinJs"
if ! "$GRADLEW" --project-dir "$KTJS_DIR" "$BUILD_TASK" -q --console=plain 2>&1 < /dev/null; then
    echo "Production build failed, trying development…" >&2
    BUILD_TASK=":scripts:$NAME:compileDevelopmentExecutableKotlinJs"
    "$GRADLEW" --project-dir "$KTJS_DIR" "$BUILD_TASK" -q --console=plain 2>&1 < /dev/null
fi

# Restore stdin for the script (bun/node will inherit it)
exec 0<&3 3<&-

# --- Locate compiled JS entry point ---
JS_OUT=""
for variant in productionExecutable developmentExecutable; do
    JS_OUT=$(find "$PROJ_DIR/build" -name "ktjs-scripts-${NAME}.js" -path "*$variant*" 2>/dev/null | head -1)
    [[ -n "$JS_OUT" ]] && break
done

if [[ -z "$JS_OUT" ]]; then
    for variant in productionExecutable developmentExecutable; do
        JS_OUT=$(find "$PROJ_DIR/build" -name "*.js" -path "*$variant*" ! -path "*node_modules*" 2>/dev/null | grep -v 'kotlin-kotlin-stdlib' | grep -v 'ktjs-context' | head -1)
        [[ -n "$JS_OUT" ]] && break
    done
fi

if [[ -z "$JS_OUT" ]]; then
    echo "Error: compiled JS not found in $PROJ_DIR/build/" >&2
    find "$PROJ_DIR/build" \( -name "*.js" -o -name "*.mjs" \) 2>/dev/null >&2 || true
    exit 1
fi

# --- Run with bun (fallback to node) ---
if command -v bun >/dev/null 2>&1; then
    exec bun "$JS_OUT" "$@"
elif command -v node >/dev/null 2>&1; then
    exec node "$JS_OUT" "$@"
else
    echo "Error: neither bun nor node found on PATH" >&2
    exit 1
fi
