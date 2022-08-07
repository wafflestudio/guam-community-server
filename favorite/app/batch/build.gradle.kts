plugins {
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.jpa") version "1.5.31"
}

apply {
    plugin("kotlin-allopen")
}

allOpen {
    annotation("javax.persistence.Entity")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation(project(":service"))

    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")

    testImplementation("org.springframework.batch:spring-batch-test")
}
