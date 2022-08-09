plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":service"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // MVC 에서도 일단 사용하는 걸로..
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.9")

    implementation("com.slack.api:slack-api-client:1.24.0")

    compileOnly("ch.qos.logback:logback-classic")
}
