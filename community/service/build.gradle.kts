dependencies {
    api(project(":data"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.1.RELEASE")
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config:3.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}
