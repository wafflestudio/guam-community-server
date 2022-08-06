dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("com.infobip:infobip-spring-data-r2dbc-querydsl-boot-starter:7.0.0")

    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("io.r2dbc:r2dbc-pool")
//    runtimeOnly("dev.miku:r2dbc-mysql")
    runtimeOnly("com.github.jasync-sql:jasync-r2dbc-mysql:2.0.8")
}
