pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "TAO Log"

println(">>> SETTINGS.GRADLE.KTS FROM LATEST COMMIT EXECUTED! <<<")

include(":logger")

val isOnJitPack = System.getenv("JITPACK") == "true"
println(">>> JITPACK ENV DETECTED: $isOnJitPack <<<")

if (!isOnJitPack) {
    println(">>> INCLUDING :demo MODULE FOR LOCAL BUILD <<<")
    include(":demo")
}
