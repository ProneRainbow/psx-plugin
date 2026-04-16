plugins {
    id("java")
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.github.mark"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        clion("2026.1") // Adjust as needed
        bundledPlugin("com.intellij.clion")
        instrumentationTools()
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "com.github.mark.psx-plugin"
        name = "PSX MIPS and PsyQ Support"
        vendor {
            name = "Mark"
        }
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("261")
        untilBuild.set("261.*")
    }
}
