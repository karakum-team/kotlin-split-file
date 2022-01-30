import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec

plugins {
    kotlin("js") version "1.6.10"
}

group = "karakum"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinWrappersVersion = "0.0.1-pre.292-kotlin-1.6.10"

dependencies {
    implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:$kotlinWrappersVersion"))
    implementation("org.jetbrains.kotlin-wrappers:kotlin-typescript")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions")

    testImplementation(kotlin("test"))
}

kotlin {
    js(LEGACY) {
        useCommonJs()
        binaries.executable()
        nodejs {

        }
    }
}

tasks.withType<NodeJsExec>().configureEach {
    val resourcesDestinationPath = tasks.named<ProcessResources>("processResources").get().destinationDir

    args(resourcesDestinationPath.toPath().resolve("dependentFunctions.js").toString())
}
