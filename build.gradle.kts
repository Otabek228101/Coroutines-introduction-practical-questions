plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "uz.ttpu"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(21)
}

// Run one task:  ./gradlew run -Pmain=q01.Q01FirstCoroutineKt
application {
    mainClass.set((findProperty("main") as String?) ?: "q01.Q01FirstCoroutineKt")
}
