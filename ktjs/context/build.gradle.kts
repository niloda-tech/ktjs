plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            api("com.github.ajalt.clikt:clikt:4.4.0")
        }
    }
}
