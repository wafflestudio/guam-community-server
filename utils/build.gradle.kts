group = "waffle.guam"
version = "0.0.1-SNAPSHOT"

val springCloudVersion = "3.1.0"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config:$springCloudVersion")
    implementation("org.slf4j:jcl-over-slf4j")
}
