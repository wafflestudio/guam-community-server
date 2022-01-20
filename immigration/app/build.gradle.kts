plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":immigration:server"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("com.linecorp.armeria:armeria-spring-boot2-webflux-starter")
    implementation("com.linecorp.armeria:armeria-grpc")
}
