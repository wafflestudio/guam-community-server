dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.slack.api:slack-api-client:1.24.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.sentry:sentry-spring-boot-starter:6.3.1")
}
