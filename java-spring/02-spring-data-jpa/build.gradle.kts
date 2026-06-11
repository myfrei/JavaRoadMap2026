// Код модуля 2 трека Java Spring — «Spring Data JPA».
// Java 25, JUnit 5 и application-плагин наследуются из корневого build.gradle.kts.
// Версии Spring-зависимостей управляются BOM-ом (gradle/libs.versions.toml).

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.liquibase.core)
    runtimeOnly(libs.h2)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.spring.boot.starter.test)
}
