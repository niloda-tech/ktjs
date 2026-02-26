#!/bin/bash
//usr/bin/true && exec "ktjs" "$0" "$@"
package hello

import cli.CliScript
import cli.readStdinLine
import cli.runScript
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int

class Hello : CliScript(name = "hello", help = "Greeting CLI transpiled to JS via Kotlin MPP") {
    private val nameOpt by option("--name", "-n", help = "Name to greet (overrides piped input)")
    private val repeat by option("--repeat", "-r", help = "Repeat the greeting").int().default(1)

    override fun run() {
        val name = nameOpt ?: readStdinLine() ?: "World"
        repeat(repeat) {
            echo("Hello, $name!")
            
        }
    }
}

fun main() = runScript(Hello())
