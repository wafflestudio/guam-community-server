plugins {
// https://grpc.io/docs/languages/kotlin/quickstart/
    id("com.google.protobuf")
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

dependencies {
    api(project(":immigration:api"))
    testImplementation(project(":immigration:app"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("io.grpc:grpc-netty")
}

testSets {
    register("clientTest")
}
