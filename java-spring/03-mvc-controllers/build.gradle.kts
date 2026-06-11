// Код модуля 3 трека Java Spring — «MVC и контроллеры».
// Java 25, JUnit 5 и application-плагин наследуются из корневого build.gradle.kts.

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.websocket)

    // gRPC: учебная in-process демонстрация без protobuf-кодогенерации
    implementation(libs.grpc.api)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.inprocess)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation(libs.spring.boot.starter.test)
}
