rootProject.name = "letter"

include(
    "api",
    "service",
)

// data
include("data")
include(":data-r2dbc")
project(":data-r2dbc").projectDir = file("data/r2dbc")
include(":data-redis")
project(":data-redis").projectDir = file("data/redis")
