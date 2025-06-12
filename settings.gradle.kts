rootProject.name = "discovery-service"
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