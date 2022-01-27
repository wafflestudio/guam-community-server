plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation(project(":community::service"))
    implementation(project(":community::slack"))
    implementation(project(":community::utils"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // MVC 에서도 일단 사용하는 걸로..
    implementation("com.google.firebase:firebase-admin:7.1.0")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.+")
}
