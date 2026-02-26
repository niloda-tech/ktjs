# ktjs-cli-workspace

Portable copy of **ktjs** (Kotlin/JS CLI runner) and **JVM script runner** for `*.cli.kts` and `*.kts` scripts.

## Layout

- **`install.sh`** – Installs ktjs: creates `ktjs/ktjs` and symlinks it to `~/.local/bin/ktjs` (or a custom path). After install, run `ktjs` from anywhere; the script resolves the workspace via the symlink.
- **`ktjs/`** – Kotlin Multiplatform project that transpiles `*.cli.kts` to JavaScript (Node/Bun).
- **`run-cli.sh`** – Runs a `.cli.kts` file: scaffolds a subproject, compiles to JS, runs with bun (or node).
- **`ktjs/ktjs`** – Executable copy of `run-cli.sh` created by `install.sh`; used as the `ktjs` command when installed.
- **`scripts/run-kotlin.sh`** – Runs any `.kts` file on the JVM with a Clikt classpath.
- **`scripts/hello.cli.kts`** – Example for **ktjs** (uses `CliScript` / `runScript`; transpiled to JS).
- **`scripts/example.cli.kts`** – Example for **JVM** (plain Clikt; run with `run-kotlin.sh`).
- **`scripts/prompt.cli.kts`** – Example **ktjs** script (prompt/CLI utilities).

## Requirements

- **JDK 21+** (for Gradle and, if using JVM runner, kotlinc)
- **Kotlin** on PATH for JVM scripts: `sdk install kotlin` or `brew install kotlin`
- **bun** or **node** for ktjs (running transpiled `.cli.kts`)

## Installation (optional)

To use the `ktjs` command from anywhere:

```bash
./install.sh                    # creates ktjs/ktjs, symlinks to ~/.local/bin/ktjs
./install.sh /usr/local/bin     # symlink to a custom directory
```

Ensure the install directory (e.g. `~/.local/bin`) is on your PATH. The symlink points at this workspace; keep the directory in place so `ktjs` can find the Gradle project and run scripts.

## Run *.cli.kts via ktjs (Kotlin → JS, bun/node)

From workspace root:

```bash
./run-cli.sh scripts/hello.cli.kts -- --name World --repeat 2
```

If you ran `install.sh`, you can use `ktjs` from anywhere (run from this workspace or with an absolute script path):

```bash
ktjs scripts/hello.cli.kts -- --name World --repeat 2
```

Or make the script executable and run it (shebang calls `run-cli.sh`):

```bash
chmod +x scripts/hello.cli.kts
./scripts/hello.cli.kts -- --name World
```

First run per script scaffolds `ktjs/scripts/<name>/` and compiles to JS; later runs are incremental.

## Run *.kts via JVM (run-kotlin.sh)

One-time: build the script classpath:

```bash
./gradlew writeScriptClasspath
```

Then run any `.kts` (including `example.cli.kts`):

```bash
./scripts/run-kotlin.sh scripts/example.cli.kts -- --help
./scripts/run-kotlin.sh scripts/example.cli.kts -- --name Kotlin -r 2 -o out.txt
```

## Using this as a new workspace

1. Copy the whole `ktjs-cli-workspace` folder to where you want (or open it in Cursor as a folder).
2. From its root, run `./gradlew writeScriptClasspath` if you use the JVM runner.
3. Run `./run-cli.sh scripts/hello.cli.kts -- --help` to confirm ktjs.
4. (Optional) Run `./install.sh` to symlink `ktjs` into `~/.local/bin` and use the `ktjs` command from anywhere.
5. Add your own `*.cli.kts` under `scripts/` and run them with either runner as above.
