import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

// ─────────────────────────────────────────────────────────────────────────────
// Корневой build-скрипт JavaRoadMap2026.
//
// Тексты уроков и теория живут в course/*.md — здесь только запускаемый КОД-СКЕЛЕТ
// модулей (modules/mNN-*). Каждый код-модуль 1:1 соответствует файлу course/NN-*.md.
//
// Для простоты используется общая настройка через subprojects {}. Это сознательно:
// «взрослый» путь (convention-плагины в build-logic/) разбирается в Модуле 09 (DevOps).
// ─────────────────────────────────────────────────────────────────────────────

subprojects {
    apply(plugin = "java")
    apply(plugin = "application")

    repositories {
        mavenCentral()
    }

    // Java 25 LTS. JDK скачивается автоматически через Foojay-резолвер
    // (см. settings.gradle.kts), если не установлен локально.
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    // main-класс выводится из имени модуля: "m07-testing" -> com.javaroadmap.m07.Main
    extensions.configure<JavaApplication> {
        mainClass.set("com.javaroadmap.${project.name.substringBefore("-")}.Main")
    }

    dependencies {
        "testImplementation"(platform("org.junit:junit-bom:5.11.4"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    // ── Домашка: «красные» задания (отдельная задача homeworkTest) ───────────────
    // Учащийся реализует homework/src/main/java, делая homework/src/test/java зелёными.
    // Общий ./gradlew build остаётся ЗЕЛЁНЫМ — homeworkTest НЕ входит в check/build.
    // Запуск:  ./gradlew :modules:mNN-slug:homeworkTest
    if (file("homework").exists()) {
        val sourceSets = extensions.getByType<SourceSetContainer>()
        val mainOutput = sourceSets.named("main").get().output
        val homework = sourceSets.create("homework") {
            java.setSrcDirs(listOf("homework/src/main/java", "homework/src/test/java"))
        }
        configurations.named("homeworkImplementation") {
            extendsFrom(configurations.named("testImplementation").get())
        }
        configurations.named("homeworkRuntimeOnly") {
            extendsFrom(configurations.named("testRuntimeOnly").get())
        }
        dependencies {
            "homeworkImplementation"(mainOutput)
        }
        tasks.register<Test>("homeworkTest") {
            description = "«Красные» тесты домашки: сделай их зелёными (см. homework/README.md)."
            group = "verification"
            testClassesDirs = homework.output.classesDirs
            classpath = homework.runtimeClasspath
        }
    }
}
