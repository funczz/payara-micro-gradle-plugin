package com.github.funczz.gradle.plugin.payara_micro

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.io.File.separator
import java.io.IOException
import java.nio.file.FileSystems
import java.util.*

class JavaRuntimeFinderTest : StringSpec() {

    init {

        "getFile" {
            JavaRuntimeFinder.apply {
                getFile(null) shouldBe null
                getFile("notExists") shouldBe null
                getFile("./src/test/fake_jre/bin/java")!!.name shouldBe "java"
            }
        }

        "findBinPath" {
            val debugJavaHome = "/path/to/java/home".split("/").joinToString(FileSystems.getDefault().separator)
            JavaRuntimeFinder.apply {
                setDebugJavaHome(Optional.of(debugJavaHome))
                findBinPath(javaBin = null) shouldContain """bin[/|\\]java""".toRegex() //環境変数 JAVA_HOME からパスを取得
                findBinPath(javaBin = "notExists") shouldContain """bin[/|\\]java""".toRegex() //環境変数 JAVA_HOME からパスを取得
                findBinPath(javaBin = "./src/test/fake_jre/bin/java") shouldContain "src" //javaBin からパスを取得
                setDebugJavaHome(Optional.empty())
            }
        }

        "getVersion" {
            JavaRuntimeFinder.getVersion(JavaRuntimeFinder.findBinPath()) shouldContain "version"
        }

        "getVersion - java コマンドが実在しない場合" {
            shouldThrow<IOException> {
                JavaRuntimeFinder.getVersion("notExists")
            }.message shouldBe """Cannot run program "notExists": error=2, No such file or directory"""
        }

    }
}