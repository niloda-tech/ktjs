#!/bin/bash
//usr/bin/true && exec "ktjs" "$0" "$@"
package prompt

import cli.CliScript
import cli.runScript
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

class Prompt : CliScript(name = "prompt", help = "Print a prompt message (for use in shell scripts or pipelines)") {
    private val message by option("--message", "-m", help = "Prompt text to display").default("")
    private val default by option("--default", "-d", help = "Default value to show in brackets").default("")

    override fun run() {
        val text = if (default.isBlank()) message else "$message [$default]"
        if (text.isNotBlank()) echo(text)
    }
}

fun main() = runScript(Prompt())
