plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
}

rootProject.name = "game-service"

include(
    "discovery:common",
    "discovery:api",
    "discovery:server",
    "discovery:client",
    "nodes:plugin",
    "nodes:proxy",
    "nodes:lobby",
    "game:core",
    "game:tnttag",
    "matchmaker:server",
)

include("matchmaker:matchmaker-client")