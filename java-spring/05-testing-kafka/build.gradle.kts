// Код модуля 5 трека Java Spring — «Тестирование и Kafka».
// Java 25, JUnit 5 и application-плагин наследуются из корневого build.gradle.kts.
// Интеграционные тесты — на EmbeddedKafka (KRaft, in-JVM): Docker не нужен.

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.kafka)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.kafka.test)
    testImplementation(libs.awaitility)
}
