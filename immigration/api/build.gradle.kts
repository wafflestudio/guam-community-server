plugins {
    id("com.google.protobuf")
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-annotations")
}

val grpcKotlinVersion = "1.1.0"
val grpcVersion = "1.39.0"
val protobufVersion = "3.17.3"

dependencies {
    // protobuf kotlin client를 위한 의존성
    api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    // protobuf server를 위한 의존성
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    // kotlin을 protobuf로 컴파일 하기 위한 의존성
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
}

sourceSets {
    getByName("main") {
        java {
            srcDirs(
                "build/generated/source/proto/main/grpc",
                "build/generated/source/proto/main/java",
                "build/generated/source/proto/main/grpckt"
            )
        }
        proto {
            // src/main/kotlin에서 .proto를 찾도록 함
            srcDir("src/main/kotlin")
        }
    }
}
