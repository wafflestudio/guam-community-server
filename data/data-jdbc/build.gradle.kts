plugins {
    id("org.jetbrains.kotlin.plugin.jpa") version "1.5.31"
    kotlin("kapt")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.hibernate:hibernate-jpamodelgen")
    implementation("org.hibernate:hibernate-jpamodelgen")

    // QueryDSL
    val querydslVersion = "5.0.0"
    implementation("com.querydsl:querydsl-jpa:$querydslVersion")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")
    kapt(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
}

// QueryDSL
sourceSets {
    named("main") {
        java.srcDir("$buildDir/generated/source/kapt/main")
    }
}
