import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
	id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = "waffle.guam"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

subprojects {
	repositories {
		mavenCentral()
	}

	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("kotlin-allopen")
		plugin("org.jlleitschuh.gradle.ktlint")
	}

	dependencies {
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
	}

	val kotestVersion = "4.4.+"
	val mockkVersion = "1.10.+"

	dependencies {
		testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
		testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
		testImplementation("io.kotest:kotest-property:$kotestVersion")
		testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
		testImplementation("io.mockk:mockk:$mockkVersion")
	}
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
