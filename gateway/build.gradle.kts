import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_11

allprojects {
    repositories {
        maven { url = uri("https://repo1.maven.org/maven2/") }
        mavenCentral()
    }

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("io.spring.dependency-management")
        plugin("kotlin-allopen")
        plugin("kotlin-spring")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:2.6.1")
        }
    }

    dependencies {
        implementation("org.springframework.cloud:spring-cloud-starter-gateway:3.1.0")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// docker
task("dockerBuild") {
    dependsOn("bootJar")

    doLast {
        val dir = project.mkdir(File(project.buildDir, "tmp"))
        val dockerFile = File(dir, "Dockerfile")

        dockerFile.writeText(
            project.file("Dockerfile")
                .inputStream()
                .use { it.reader().readText() }
        )

        project.copy {
            val jar = project.tasks.getByName<Jar>("bootJar")

            from(jar.archiveFile) {
                rename { "app.jar" }
            }

            into(dir)
        }

        project.exec {
            workingDir(dir)
            commandLine("docker", "build", "-t", "pfcjeong/guam-gateway:${project.version}", ".")
            commandLine("docker", "build", "-t", "pfcjeong/guam-gateway:latest", ".")
        }
    }
}

task("dockerPush") {
    dependsOn("dockerBuild")

    doLast {
        project.exec {
            commandLine("docker", "push", "pfcjeong/guam-gateway:${project.version}")
            commandLine("docker", "push", "pfcjeong/guam-gateway:latest")
        }
    }
}
