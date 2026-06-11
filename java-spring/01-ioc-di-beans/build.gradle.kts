// Код модуля 1 трека Java Spring — «IoC, DI и бины».
// Java 25, JUnit 5 и application-плагин наследуются из корневого build.gradle.kts.
// Версии Spring-зависимостей управляются BOM-ом (gradle/libs.versions.toml).

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.spring.boot.starter.test)
}
