dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":slack-notification-spring"))
}
