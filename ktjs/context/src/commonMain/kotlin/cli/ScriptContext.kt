package cli

import com.github.ajalt.clikt.core.CliktCommand

/**
 * Base class for CLI scripts transpiled to JS via Kotlin MPP.
 * Extends CliktCommand with defaults suited for single-file CLI tools.
 */
abstract class CliScript(
    name: String? = null,
    help: String = "",
) : CliktCommand(name = name, help = help)

/**
 * Retrieves command-line arguments from the platform runtime.
 * On JS/Node.js, reads process.argv (stripping the first two entries: node and script path).
 */
expect fun platformArgs(): Array<String>

/**
 * Reads the first line from stdin if it is a pipe (non-interactive).
 * Returns null when stdin is a TTY, on error, or when no data is available.
 */
expect fun readStdinLine(): String?

/**
 * Entry-point helper: runs a CliktCommand with platform-resolved args.
 * Use from `fun main() = runScript(MyCommand())` in .cli.kts files.
 */
fun runScript(command: CliktCommand, args: Array<String> = platformArgs()) {
    command.main(args)
}
