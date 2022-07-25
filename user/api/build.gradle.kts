plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":service"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
