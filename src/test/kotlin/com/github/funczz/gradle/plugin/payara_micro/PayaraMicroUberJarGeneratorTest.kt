package com.github.funczz.gradle.plugin.payara_micro

import io.kotlintest.matchers.string.shouldEndWith
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.io.File
import java.util.concurrent.TimeoutException

class PayaraMicroUberJarGeneratorTest : StringSpec() {

    private val projectDir = File(".").canonicalFile
    private val buildDir = File(projectDir, "build")
    private var payara = File(buildDir, "/payara-micro.jar")
    private val war = File(projectDir, "src/test/testFiles/ROOT.war")
    private val jar = File(buildDir, "ROOT.jar")

    init {

        "testFiles" {
            buildDir.canonicalPath shouldEndWith "build"
            buildDir.exists() shouldBe true
            payara.canonicalPath shouldEndWith "build/payara-micro.jar"
            payara.exists() shouldBe true
            war.canonicalPath shouldEndWith "payara-micro-gradle-plugin/src/test/testFiles/ROOT.war"
            war.exists() shouldBe true
            jar.canonicalPath shouldEndWith "build/ROOT.jar"
        }

        "outputUberJar" {
            if (jar.exists()) jar.deleteRecursively()
            PayaraMicroUberJarGenerator(
                payaraMicroJarFile = payara,
                workDir = buildDir,
            ).outputUberJar(rootWar = war, uberJar = jar)
            val start = System.currentTimeMillis()
            while (!jar.exists()) {
                Thread.sleep(100L)
                if (System.currentTimeMillis() - start > 5000L) break
            }
            jar.exists() shouldBe true
        }

        "outputUberJar - timeout" {
            shouldThrow<TimeoutException> {
                PayaraMicroUberJarGenerator(
                    payaraMicroJarFile = payara,
                    workDir = buildDir,
                    timeout = 1L,
                ).outputUberJar(rootWar = war, uberJar = jar)
            }.message shouldStartWith "command line: java -jar "
        }

    }
}