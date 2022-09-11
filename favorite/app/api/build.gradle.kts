plugins {
    id("org.springframework.boot")
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

testSets {
    register("integrationTest")
}

dependencies {
    implementation(project(":service"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.11")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.11")

    implementation("com.github.wafflestudio.kotlin-lib:slack-notification-spring-boot-starter:0.0.1")
}
