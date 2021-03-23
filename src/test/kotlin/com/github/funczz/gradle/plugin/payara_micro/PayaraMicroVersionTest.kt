package com.github.funczz.gradle.plugin.payara_micro

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldEndWith
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.io.File
import java.util.concurrent.TimeoutException

class PayaraMicroVersionTest : StringSpec() {

    private val projectDir = File(".").canonicalFile
    private val buildDir = File(projectDir, "build")
    private var payara = File(buildDir, "/payara-micro.jar")

    init {

        "testFiles" {
            buildDir.canonicalPath shouldEndWith "build"
            buildDir.exists() shouldBe true
            payara.canonicalPath shouldEndWith "build/payara-micro.jar"
            payara.exists() shouldBe true
        }

        "get" {
            PayaraMicroVersion.get(payaraMicroJarFile = payara) shouldBe "Payara Micro 5.2021.1. Build Number 2818"
        }

        "get - timeout" {
            shouldThrow<TimeoutException> {
                PayaraMicroVersion.get(payaraMicroJarFile = payara, timeout = 1L)
            }.message shouldStartWith "command line: java -jar "
        }

    }
}