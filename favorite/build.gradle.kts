import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_11

allprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo1.maven.org/maven2/") }
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
            mavenBom("org.springframework.boot:spring-boot-dependencies:2.6.7")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.2")
        }
    }

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    val mockkVersion = "1.10.+"
    dependencies {
        testImplementation("io.mockk:mockk:$mockkVersion")
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
    dependsOn("api:bootJar")

    doLast {
        val dir = project.mkdir(File(project.buildDir, "tmp"))
        val dockerFile = File(dir, "Dockerfile")

        dockerFile.writeText(
            project.file("Dockerfile")
                .inputStream()
                .use { it.reader().readText() }
        )

        project.copy {
            val jar = project
                .childProjects["api"]!!
                .tasks.getByName<Jar>("bootJar")

            from(jar.archiveFile) {
                rename { "app.jar" }
            }

            into(dir)
        }

        project.exec {
            workingDir(dir)
            commandLine("docker", "build", "-t", "pfcjeong/guam-favorite:${project.version}", ".")
            commandLine("docker", "build", "-t", "pfcjeong/guam-favorite:latest", ".")
        }
    }
}

task("dockerPush") {
    dependsOn("dockerBuild")

    doLast {
        project.exec {
            commandLine("docker", "push", "pfcjeong/guam-favorite:${project.version}")
            commandLine("docker", "push", "pfcjeong/guam-favorite:latest")
        }
    }
}
