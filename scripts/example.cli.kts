#!/bin/bash
//usr/bin/true && exec "ktjs" "$0" "$@"

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.int
import java.io.File

class Example : CliktCommand(
    name = "example.cli.kts",
    help = "Example script with argument parsing and file output",
) {
    private val name by option("--name", "-n", help = "Name to greet").default("World")
    private val repeat by option("--repeat", "-r", help = "Number of greetings").int().default(1).validate { require(it > 0) { "repeat must be positive" } }
    private val output by option("--output", "-o", help = "Write output to file instead of stdout")

    override fun run() {
        val message = List(repeat) { "Hello, $name!" }.joinToString("\n")
        val out = output
        if (out != null) {
            File(out).writeText(message)
            println("Wrote to $out")
        } else {
            println(message)
        }
    }
}

Example().main(args)
