plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":service"))

    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")

    testImplementation("org.springframework.batch:spring-batch-test")
}
