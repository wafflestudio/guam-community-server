plugins {
    `maven-publish`
    `kotlin-dsl`
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.rootProject.group}"
            artifactId = "${project.name}"
            version = "${project.rootProject.version}"

            from(components["java"])
        }
    }
}