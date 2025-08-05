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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tele_Tv"
include(":app")
include(":domain")
include(":data")
include(":resources")
include(":login")
include(":player")
include(":channels")
include(":app_tv")
include(":splash_screen")
include(":base_module")
include(":app_mobile")
include(":channels_mobile")
include(":login_mobile")
include(":ptk")
