plugins {
    id("org.springframework.boot")
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

testSets {
    register("integrationTest")
}

dependencies {
    implementation(project(":service"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
