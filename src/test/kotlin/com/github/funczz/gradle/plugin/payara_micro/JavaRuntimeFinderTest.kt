package com.github.funczz.gradle.plugin.payara_micro

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.io.IOException

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
            JavaRuntimeFinder.apply {
                //テスト環境で 環境変数 JAVA_HOME が定義されている事
                System.getenv("JAVA_HOME") shouldContain "java"
                //環境変数 JAVA_HOME からパスを取得
                findBinPath(javaBin = null) shouldContain """bin[/|\\]java""".toRegex()
                findBinPath(javaBin = "notExists") shouldContain """bin[/|\\]java""".toRegex()
                //javaBin からパスを取得
                findBinPath(javaBin = "./src/test/fake_jre/bin/java") shouldContain "src"
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