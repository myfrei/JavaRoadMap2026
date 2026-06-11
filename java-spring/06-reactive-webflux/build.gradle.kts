// Код модуля 6 трека Java Spring — «Реактивный стек».
// Java 25, JUnit 5 и application-плагин наследуются из корневого build.gradle.kts.

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.r2dbc)
    runtimeOnly(libs.r2dbc.h2)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.reactor.test)
}
