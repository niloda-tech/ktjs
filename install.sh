#!/usr/bin/env bash
# Install ktjs: copies run-cli.sh to an executable script named ktjs and
# symlinks it to ~/.local/bin/ktjs (or DEST if given).
#
# Usage:
#   ./install.sh              # create ktjs and symlink to ~/.local/bin/ktjs
#   ./install.sh [DEST]       # symlink to DEST/ktjs instead (e.g. /usr/local/bin)
#
# With a symlink, the script runs from this workspace; keep this directory in place.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR" && pwd)"
KTJS_RUNNER="$REPO_ROOT/run-cli.sh"
KTJS_BIN="$REPO_ROOT/ktjs"

if [[ ! -f "$KTJS_RUNNER" ]]; then
    echo "Error: run-cli.sh not found at $KTJS_RUNNER" >&2
    exit 1
fi

cp "$KTJS_RUNNER" "$KTJS_BIN"
chmod +x "$KTJS_BIN"
echo "Created executable: $KTJS_BIN"

DEST="${1:-$HOME/.local/bin}"
DEST="${DEST/#\~/$HOME}"
mkdir -p "$DEST"
DEST_DIR="$(cd "$DEST" && pwd)"
LINK="$DEST_DIR/ktjs"
if [[ -L "$LINK" ]] && [[ "$(readlink "$LINK")" == "$KTJS_BIN" ]]; then
    echo "Already linked: $LINK -> $KTJS_BIN"
else
    ln -sf "$KTJS_BIN" "$LINK"
    echo "Linked: $LINK -> $KTJS_BIN"
fi
echo "Run 'ktjs' from anywhere if $DEST_DIR is on your PATH."
