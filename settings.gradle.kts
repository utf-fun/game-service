rootProject.name = "game-service"
include(
    "modules:common",
    "modules:api",
    "modules:server",
    "modules:client",
)
include(
    "nodes:test-server",
    "nodes:plugin"
)
include("nodes:plugin")
include("nodes:proxy")