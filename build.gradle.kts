// Minimal build to provide a classpath for Kotlin scripts (.kts) run via scripts/run-kotlin.sh.
// Run: ./gradlew writeScriptClasspath
// Then: ./scripts/run-kotlin.sh scripts/<script>.kts [-- args...]

plugins {
    id("base")
}

repositories {
    mavenCentral()
}

val scriptClasspath by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    scriptClasspath("com.github.ajalt.clikt:clikt:4.4.0")
}

tasks.register("writeScriptClasspath") {
    description = "Resolve script dependencies and write scripts/.kotlin-classpath for run-kotlin.sh"
    doLast {
        val files = scriptClasspath.resolve()
        val path = files.joinToString(File.pathSeparator) { it.absolutePath }
        val outFile = file("scripts/.kotlin-classpath")
        outFile.parentFile.mkdirs()
        outFile.writeText(path)
        logger.lifecycle("Wrote ${outFile.absolutePath} (${files.size} jars)")
    }
}
