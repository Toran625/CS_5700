import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "2.2.0"
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta03")

            // Move Ktor dependencies here so they're available in commonMain
            val ktorVersion = "2.3.7"
            implementation("io.ktor:ktor-server-core:$ktorVersion")
            implementation("io.ktor:ktor-server-netty:$ktorVersion")
            implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            implementation("io.ktor:ktor-server-resources:${ktorVersion}")
            implementation("io.ktor:ktor-server-host-common:${ktorVersion}")
            implementation("io.ktor:ktor-server-auto-head-response:${ktorVersion}")
            implementation("io.ktor:ktor-server-html-builder:${ktorVersion}")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.mockito:mockito-core:5.1.1")
            implementation("io.ktor:ktor-server-test-host:2.3.7")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            implementation("org.junit.platform:junit-platform-suite:1.10.2") // or latest

            implementation("org.junit.jupiter:junit-jupiter:5.10.0")
            implementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
            implementation("org.mockito:mockito-inline:5.2.0")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("ch.qos.logback:logback-classic:1.4.11") // For logging

        }
    }
}


compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}

tasks.named<Test>("desktopTest") {
    useJUnitPlatform()
}