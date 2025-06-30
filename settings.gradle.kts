plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
}

rootProject.name = "game-service"
include(
    "modules:common",
    "modules:api",
    "modules:server",
    "modules:client",
)
include("nodes:plugin")
include("nodes:proxy")