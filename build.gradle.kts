/**
 * plugins
 */
plugins {
    kotlin("jvm") version "1.7.10" apply false
    id("java-gradle-plugin")
    id("nebula.release") version "15.3.1"
    id("nebula.maven-publish") version "17.3.2"
}

/**
 * plugin: nebula.release, nebula.maven-publish
 */
tasks {
    "release" {
        dependsOn(
            "publish"
        )
    }
}

/**
 * plugin: nebula.maven-publish
 */
publishing {
    publications {
        group = "com.github.funczz"
    }

    repositories {
        maven {
            url = uri(
                PublishMavenRepository.url(
                    version = version.toString(),
                    baseUrl = "$buildDir/mvn-repos"
                )
            )
        }
    }
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

dependencies {
    /**
     * dependencies: libs Directory
     */
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    /**
     * dependencies: kotlin for JDK8
     */
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
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


/**
 * rootProject
 * ===========
 */

/**
 * dependencies
 */
dependencies {
    testRuntimeOnly("fish.payara.extras:payara-micro:5.2021.1")
}

/**
 * plugin: java-gradle-plugin
 */
gradlePlugin {
    // Define the plugin
    val payaraMicroGradlePlugin by plugins.creating {
        id = "payara-micro-gradle-plugin"
        implementationClass = "com.github.funczz.gradle.plugin.payara_micro.PayaraMicroGradlePlugin"
    }
}

// Add a source set for the functional test suite
val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

// Add a task to run the functional tests
val functionalTest by tasks.creating(Test::class) {
    group = "verification"
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform() //task: kotlintest-runner-junit5
}

val check by tasks.getting(Task::class) {
    // Run the functional tests as part of `check`
    dependsOn(functionalTest)
}

/**
 * task: test
 * deploy PayaraMicro Jar
 */
fun Project.getPayaraMicroJar(): File? {
    val regex = """fish\.payara\.extras.payara-micro.+payara-micro-.*\.jar$""".toRegex()
    this.configurations
        .asSequence()
        .filter {
            it.toString().contains("""Classpath""".toRegex())
        }.map {
            it.files
        }.flatten()
        .filter {
            it.isFile
        }.filter {
            it.canonicalPath.contains(regex)
        }
        .toList().forEach {
            return it
        }
    return null
}

tasks.test {
    useJUnitPlatform() //task: kotlintest-runner-junit5
    doFirst {
        val payaraMicroJar = project.getPayaraMicroJar()!!
        val testJar = File(buildDir, "payara-micro.jar")
        if (!testJar.exists()) {
            payaraMicroJar.also {
                it.copyTo(testJar, overwrite = true)
            }
        }
    }
}

