import com.github.funczz.gradle.plugin.payara_micro.payaraMicro

/**
 * plugins
 */
plugins {
    kotlin("jvm") version "1.7.10" apply false
    id("payara-micro-gradle-plugin")
}

/**
 * plugin: payara-micro-gradle-plugin
 */
payaraMicro {
    options = "--nocluster --port 8080".split(" ")
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
 * dependencies
 */
dependencies {
    /**
     * dependencies: libs Directory
     */
    "implementation"(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    /**
     * dependencies: kotlin for JDK8
     */
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    "testImplementation"("io.kotlintest:kotlintest-runner-junit5:3.4.2")

    "javax.servlet:javax.servlet-api:3.1.0".also {
        "compileOnly"(it)
    }
    "testRuntimeOnly"("fish.payara.extras:payara-micro:5.2021.1")
}

/**
 * task: JavaCompile
 */
org.gradle.api.Action<org.gradle.api.plugins.JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

/**
 * task: KotlinCompile
 */
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = BuildSrcUtil.javaVersionToJvmTarget(JavaVersion.VERSION_1_8)
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

/**
 * task: Test
 */
tasks.withType(Test::class.java) {
    useJUnitPlatform() //task: kotlintest-runner-junit5
    testLogging {
        events("passed", "skipped", "failed")
    }
}

