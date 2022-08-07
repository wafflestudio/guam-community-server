plugins {
    kotlin("kapt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("com.infobip:infobip-spring-data-r2dbc-querydsl-boot-starter:6.2.0")
    kapt("com.infobip:infobip-spring-data-jdbc-annotation-processor-common:6.2.0")

    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("io.r2dbc:r2dbc-pool")
    runtimeOnly("com.github.jasync-sql:jasync-r2dbc-mysql:2.0.8")



    // http://querydsl.com/static/querydsl/latest/reference/html/ch03s03.html
//    kapt("com.querydsl:querydsl-apt:5.0.0:general")
//    kapt(group = "com.querydsl", name = "querydsl-apt", classifier = "general")
}
repositories {
    mavenCentral()
}

// QueryDSL
sourceSets {
    named("main") {
        java.srcDir("$buildDir/generated/source/kapt/main")
    }
}
