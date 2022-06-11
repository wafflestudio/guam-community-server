plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":service"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
