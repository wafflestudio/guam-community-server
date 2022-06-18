dependencies {
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework:spring-context")

    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("io.r2dbc:r2dbc-pool")
//    runtimeOnly("dev.miku:r2dbc-mysql")
    runtimeOnly("com.github.jasync-sql:jasync-r2dbc-mysql:2.0.8")

    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}
