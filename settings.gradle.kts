rootProject.name = "community"

include("data-jdbc")
project(":data-jdbc").projectDir = file("data/data-jdbc")
include("service")
include("api")
