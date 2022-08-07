plugins {
    kotlin("kapt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("io.r2dbc:r2dbc-pool")
    runtimeOnly("com.github.jasync-sql:jasync-r2dbc-mysql:2.0.8")
}

repositories {
    mavenCentral()
}
