group = "waffle.guam"
version = "0.0.1-SNAPSHOT"

// slack
dependencies {
    implementation(project(":community:utils"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.slack.api:slack-api-client:1.+")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.apache.poi:poi-ooxml:4.1.2")
}
