plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":service"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // MVC 에서도 일단 사용하는 걸로..
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("org.springdoc:springdoc-openapi-ui:1.5.+")

    implementation("com.github.wafflestudio.kotlin-lib:slack-notification-spring-boot-starter:0.0.1-SNAPSHOT")

    compileOnly("ch.qos.logback:logback-classic")
}
