dependencies {
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework:spring-context")

    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("dev.miku:r2dbc-mysql")

    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}
