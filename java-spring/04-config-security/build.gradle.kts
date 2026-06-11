// Код модуля 4 трека Java Spring — «Конфигурация и безопасность».
// Java 25, JUnit 5 и application-плагин наследуются из корневого build.gradle.kts.

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}
