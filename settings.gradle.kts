rootProject.name = "community"

include("data-jdbc")
project(":data-jdbc").projectDir = file("data/data-jdbc")
include("data-redis")
project(":data-redis").projectDir = file("data/data-redis")
include("service")
include("api")
