pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "ktjs"

include(":context")

file("scripts").listFiles()
    ?.filter { it.isDirectory && file("${it.path}/build.gradle.kts").exists() }
    ?.forEach { include(":scripts:${it.name}") }
