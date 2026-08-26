pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // yt-dlp (youtubedl-android) kütüphanesini bulabilmesi için JitPack deposu
        maven { url = java.net.URI("https://jitpack.io") }
    }
}
rootProject.name = "MediaApp"
include(":app")
