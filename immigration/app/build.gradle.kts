plugins {
    id("org.springframework.boot")
}

dependencyManagement {
    imports {
        mavenBom("io.micrometer:micrometer-bom:1.7.6")
        mavenBom("io.netty:netty-bom:4.1.70.Final")
        mavenBom("com.linecorp.armeria:armeria-bom:1.13.4")
    }
}

dependencies {
    implementation(project(":immigration:server"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // armeria로 grpc와 http를 단일 포트로 지원
    api("com.linecorp.armeria:armeria-bom:1.13.4")
    implementation("com.linecorp.armeria:armeria-spring-boot2-webflux-starter")
    implementation("com.linecorp.armeria:armeria-grpc")
}
