import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

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

// https://grpc.io/docs/languages/kotlin/quickstart/의 설명대로 적용
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}
