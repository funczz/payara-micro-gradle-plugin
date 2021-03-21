package com.github.funczz.gradle.plugin.payara_micro

import java.io.File
import java.util.concurrent.TimeoutException

/**
 * PayaraMicro バージョン取得
 * @author funczz
 */
object PayaraMicroVersion {

    /**
     * デフォルトのJava ランタイム起動コマンド
     */
    private const val DEFAULT_JAVA_BIN = "java"

    /**
     * payara Micro Jar のバージョンを取得する
     * @param payaraMicroJarFile payara Micro Jar ファイル
     * @param timeout プロセス起動の制限時間
     * @return バージョン番号
     * @throws TimeoutException コマンドが制限時間内に完了しなかった
     */
    fun get(payaraMicroJarFile: File, timeout: Long = 5000L): String {
        return get(javaBin = DEFAULT_JAVA_BIN, payaraMicroJarFile = payaraMicroJarFile, timeout = timeout)
    }

    /**
     * payara Micro Jar のバージョンを取得する
     * @param javaBin Java ランタイム起動コマンド
     * @param payaraMicroJarFile payara Micro Jar ファイル
     * @param timeout プロセス起動の制限時間
     * @return バージョン番号
     * @throws TimeoutException コマンドが制限時間内に完了しなかった
     */
    fun get(javaBin: String, payaraMicroJarFile: File, timeout: Long = 5000L): String {
        val stderr: MutableList<String> = mutableListOf()
        val commandLine = mutableListOf(
            javaBin,
            "-jar",
            payaraMicroJarFile.canonicalPath,
            "--version",
        ).toTypedArray()
        val processBuilder = ProcessBuilder().apply {
            command(*commandLine)
        }
        var count: Long = 0L
        ProcessStreamGobbler().apply {
            eachStderr {
                stderr.add(it)
            }
            onError {
                throw it
            }
            onWaitFor {
                while (it.isAlive) {
                    if (count >= timeout) break
                    Thread.sleep(1L)
                    count++
                }
                val isAlive = it.isAlive
                it.destroy()
                if (isAlive) throw TimeoutException("command line: ${commandLine.joinToString(" ")}")
            }
        }.start {
            processBuilder.start()
        }

        return stderr.joinToString("\n")
    }

}