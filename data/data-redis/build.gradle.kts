dependencies {
    api(project(":utils"))

    api("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("it.ozimov:embedded-redis:0.7.2")
}
