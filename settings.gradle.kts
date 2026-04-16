rootProject.name = "psx-plugin"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://cache-redirector.jetbrains.com/intellij-dependencies") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://cache-redirector.jetbrains.com/intellij-repository/releases") }
        maven { url = uri("https://cache-redirector.jetbrains.com/intellij-repository/snapshots") }
    }
}
