package com.github.funczz.gradle.plugin.payara_micro

import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File
import java.io.IOException

class ProcessStreamGobblerTest : StringSpec() {

    private val scriptDir = File("src/test/script").canonicalFile

    private val successful = File(scriptDir, "successful")

    private val failed = File(scriptDir, "failed")

    private val loop = File(scriptDir, "loop")

    init {

        "eachStdout" {
            val result = mutableListOf<String>()

            val pb = ProcessBuilder().command(successful.path)
            val psg = ProcessStreamGobbler().also {
                it.eachStdout {
                    result.add(it)
                }
                it.onError {
                    throw it
                }
                it.waitFor {
                    it.waitFor()
                }
            }
            psg.start {pb.start()}

            Thread.sleep(500L)
            println(result)
            result shouldBe listOf("SUCCESSFUL")
        }

        "eachStderr" {
            val result = mutableListOf<String>()

            val pb = ProcessBuilder().command(failed.path)
            val psg = ProcessStreamGobbler().also {
                it.eachStderr {
                    result.add(it)
                }
                it.onError {
                    throw it
                }
                it.waitFor {
                    it.waitFor()
                }
            }
            psg.start {pb.start()}

            Thread.sleep(500L)
            println(result)
            result shouldBe listOf("FAILED")
        }

        "onError" {
            var result: Throwable? = null

            val pb = ProcessBuilder().command("ls not_exists_file")
            val psg = ProcessStreamGobbler().also {
                it.onError {
                    result = it
                }
                it.waitFor {
                    it.waitFor()
                }
            }
            psg.start {pb.start()}

            Thread.sleep(500L)
            result!!::class shouldBe IOException::class
            result!!.message shouldStartWith """Cannot run program "ls not_exists_file""""
        }

        "waitFor" {
            var result = ""

            val pb = ProcessBuilder().command(loop.path)
            val psg = ProcessStreamGobbler().also {
                it.waitFor {
                    Thread.sleep(1000L)
                    it.destroy()
                    result = "destroy"
                }
            }
            psg.start {pb.start()}

            Thread.sleep(2000L)
            result shouldBe "destroy"
        }

    }
}