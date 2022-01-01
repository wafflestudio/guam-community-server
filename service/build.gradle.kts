dependencies {
    api(project(":data-jdbc"))
    implementation(project(":utils"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")
}
