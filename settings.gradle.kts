rootProject.name = "guam"

include(":community:data-jdbc")
project(":community:data-jdbc").projectDir = file("community/data/data-jdbc")
include(":community:data-redis")
project(":community:data-redis").projectDir = file("community/data/data-redis")
include(":community:data")
include(":community:service")
include(":community:api")
include(":community:utils")
include(":community:slack")
