val springCloudVersion = "3.1.0"

dependencies {
    api(project(":immigration:api"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config:$springCloudVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testRuntimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("dev.miku:r2dbc-mysql")

    implementation("com.google.firebase:firebase-admin:7.1.0")
}
