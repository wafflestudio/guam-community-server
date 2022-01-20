plugins {
    idea
    java
    id("com.google.protobuf") version "0.8.17" apply false
}

allprojects {
    dependencyManagement {
        imports {
            mavenBom("io.micrometer:micrometer-bom:1.7.6")
            mavenBom("io.netty:netty-bom:4.1.70.Final")
            mavenBom("com.linecorp.armeria:armeria-bom:1.13.4")
            mavenBom("io.grpc:grpc-bom:1.39.0")
        }
    }
}
