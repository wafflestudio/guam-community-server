dependencies {
    api(project(":data-jdbc"))
    api(project(":data-redis"))
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}
