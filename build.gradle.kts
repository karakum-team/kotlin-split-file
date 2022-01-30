plugins {
    kotlin("js") version "1.6.10"
}

group = "karakum"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    js(IR) {
        useCommonJs()
        binaries.executable()
        nodejs {

        }
    }
}
