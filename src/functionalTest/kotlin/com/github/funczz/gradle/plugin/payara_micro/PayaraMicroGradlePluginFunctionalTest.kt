/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.funczz.gradle.plugin.payara_micro

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.StringSpec
import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * A simple functional test for the plugin.
 */
class PayaraMicroGradlePluginFunctionalTest : StringSpec() {

    // build.gradle.kts
    private val kotlinBuildGradle = """
        plugins {
            id("payara-micro-gradle-plugin")
            id("java")
        }
        repositories {
            mavenLocal()
            mavenCentral()
        }
        dependencies {
            testRuntimeOnly("fish.payara.extras:payara-micro:5.2021.1")
        }
        """.trimIndent()

    // Setup the test build
    private fun createGradleRunner(arguments: String): GradleRunner {
        val projectDir = File("build/functionalTest").apply {
            mkdirs()
            resolve("settings.gradle.kts").writeText("")
            resolve("build.gradle.kts").writeText(kotlinBuildGradle)
        }
        return GradleRunner.create().apply {
            withProjectDir(projectDir)
            withPluginClasspath()
            withDebug(true)
            forwardOutput()
            withArguments(arguments)
        }
    }

    init {

        /**
         * payaraVersion
         */

        "payaraVersion タスクが登録されている" {
            // Run the build
            val runner = createGradleRunner(arguments = "tasks")
            val result = runner.build()
            // Verify the result
            result.output shouldContain "payaraVersion"
        }

        "payaraVersion タスクを実行してバージョン情報を取得する" {
            // Run the build
            val runner = createGradleRunner(arguments = "payaraVersion")
            val result = runner.build()
            // Verify the result
            result.output shouldContain "Payara Micro "
        }

        /**
         * payaraUberJar
         */

        "payaraUberJar タスクが登録されている" {
            // Run the build
            val runner = createGradleRunner(arguments = "tasks")
            val result = runner.build()
            // Verify the result
            result.output shouldContain "payaraUberJar"
        }

        /**
         * payaraStartWar
         */

        "payaraStartWar タスクが登録されている" {
            // Run the build
            val runner = createGradleRunner(arguments = "tasks")
            val result = runner.build()
            // Verify the result
            result.output shouldContain "payaraStartWar"
        }

        /**
         * payaraRedeployWar
         */

        "payaraRedeployWar タスクが登録されている" {
            // Run the build
            val runner = createGradleRunner(arguments = "tasks")
            val result = runner.build()
            // Verify the result
            result.output shouldContain "payaraRedeployWar"
        }

        /**
         * payaraStopWar
         */

        "payaraStopWar タスクが登録されている" {
            // Run the build
            val runner = createGradleRunner(arguments = "tasks")
            val result = runner.build()
            // Verify the result
            result.output shouldContain "payaraStopWar"
        }

    }
}
