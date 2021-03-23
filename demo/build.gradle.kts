/**
 * plugins
 */
plugins {
    id("payara-micro-gradle-plugin")
    kotlin("jvm") version "1.4.30" apply false
}

/**
 * build script
 */
buildscript {
    /**
     * repositories
     */
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

/**
 * repositories
 */
repositories {
    mavenLocal()
    mavenCentral()
}

/**
 * plugins
 */
apply(plugin = "java")
apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "jacoco")
apply(plugin = "war")

/**
 * dependencies: kotlin
 */
kotlinProjectDependencies()

/**
 * dependencies
 */
dependencies {
    "javax.servlet:javax.servlet-api:3.1.0".also {
        "compileOnly"(it)
    }
    "testRuntimeOnly"("fish.payara.extras:payara-micro:5.2021.1")
}

/**
 * task: JavaCompile
 */
org.gradle.api.Action<org.gradle.api.plugins.JavaPluginExtension> {
    sourceCompatibility = CommonDeps.Java.version
    targetCompatibility = CommonDeps.Java.version
}
tasks.withType(JavaCompile::class) {
    options.encoding = CommonDeps.Java.encoding
}

/**
 * task: KotlinCompile
 */
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = CommonDeps.Kotlin.jvmTarget
        freeCompilerArgs = CommonDeps.Kotlin.freeCompilerArgs
    }
}

/**
 * task: Test
 */
tasks.withType(Test::class.java) {
    useJUnitPlatform() //task: kotlintest-runner-junit5
    testLogging {
        events(*CommonTasks.Test.loggingEvent)
    }
}

