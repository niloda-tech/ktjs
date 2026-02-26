plugins {
    kotlin("multiplatform") version "2.1.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().apply {
        download = false
    }
}
