plugins {
// https://grpc.io/docs/languages/kotlin/quickstart/
    id("com.google.protobuf")
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

dependencies {
    api(project(":immigration:api"))
    testImplementation(project(":immigration:app"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("io.grpc:grpc-netty")
    implementation("com.linecorp.armeria:armeria-spring-boot2-webflux-starter")
    implementation("com.linecorp.armeria:armeria-grpc")
}

testSets {
    register("clientTest")
}
