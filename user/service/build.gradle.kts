dependencies {
    api(project(":infra"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")
}
