dependencies {
    api(project(":community:data-jdbc"))
    api(project(":community:data-redis"))
    implementation(project(":community:utils"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("com.google.firebase:firebase-admin:7.1.0")
}
