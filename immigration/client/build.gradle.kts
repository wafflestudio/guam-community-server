plugins {
// https://grpc.io/docs/languages/kotlin/quickstart/
    id("com.google.protobuf")
}

dependencyManagement {
    imports {
        mavenBom("io.micrometer:micrometer-bom:1.7.6")
        mavenBom("io.netty:netty-bom:4.1.70.Final")
        mavenBom("com.linecorp.armeria:armeria-bom:1.13.4")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api(project(":immigration:api"))
    implementation("io.grpc:grpc-netty:1.41.1")

    api("com.linecorp.armeria:armeria-bom:1.13.4")
    implementation("com.linecorp.armeria:armeria-spring-boot2-webflux-starter")
    implementation("com.linecorp.armeria:armeria-grpc")
}
