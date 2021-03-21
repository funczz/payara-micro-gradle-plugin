/**
 * plugins
 */
plugins {
    kotlin("jvm") version "1.4.30" apply false
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
 * allprojects
 */
allprojects {
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

    /**
     * dependencies: kotlin
     */
    kotlinProjectDependencies()

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
}

/**
 * rootProject
 * ===========
 */

/**
 * dependencies
 */
dependencies {
    //payara-micro
    testImplementation("fish.payara.extras:payara-micro:5.2021.1")
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
    val regex = """fish\.payara\.extras.payara-micro.+payara-micro\-[\d\.]+\.jar$""".toRegex()
    val files = this.configurations
        .filter {
            it.toString().contains("""Classpath""".toRegex())
        }.map {
            it.files
        }.flatten()
    files.filter {
        it.isFile
    }.filter {
        it.canonicalPath.contains(regex)
    }.forEach {
        return it
    }
    return null
}

tasks.test {
    useJUnitPlatform() //task: kotlintest-runner-junit5
    doFirst {
        val payaraMicroJar = File(buildDir, "payara-micro.jar")
        if (!payaraMicroJar.exists()) {
            project.getPayaraMicroJar()!!.copyTo(payaraMicroJar, overwrite = true)
        }
    }
}
