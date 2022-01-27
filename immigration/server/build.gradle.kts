dependencies {
    api(project(":immigration:api"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.r2dbc:r2dbc-h2")

    implementation("com.google.firebase:firebase-admin:7.1.0")
}
