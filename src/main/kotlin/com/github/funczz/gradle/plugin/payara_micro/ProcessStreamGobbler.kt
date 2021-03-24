package com.github.funczz.gradle.plugin.payara_micro

import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

/**
 * プロセスに対して標準出力、エラー出力、待機処理を行う
 * @author funczz
 */
open class ProcessStreamGobbler {

    /**
     * プロセスの標準出力に対する処理
     */
    open var eachStdout: (String) -> Unit = {}

    /**
     * プロセスのエラー出力に対する処理
     */
    open var eachStderr: (String) -> Unit = {}

    /**
     * プロセスの例外エラーに対する処理
     */
    open var onError: (Throwable) -> Unit = {}

    /**
     * プロセスの待機処理
     */
    open var waitFor: (Process) -> Unit = {
        it.waitFor()
    }

    /**
     * プロセスの標準出力を処理する関数を代入する
     */
    open fun eachStdout(function: (String) -> Unit) {
        eachStdout = function
    }

    /**
     * プロセスのエラー出力を処理する関数を代入する
     */
    open fun eachStderr(function: (String) -> Unit) {
        eachStderr = function
    }

    /**
     * プロセスの例外エラーを処理する関数を代入する
     */
    open fun onError(function: (Throwable) -> Unit) {
        onError = function
    }

    /**
     * プロセスの待機処理を行う関数を代入
     */
    open fun waitFor(function: (Process) -> Unit) {
        waitFor = function
    }

    /**
     * プロセスを開始する
     * @param charset 標準出力、エラー出力の文字セット
     * @param buildProcess プロセスを生成する関数
     */
    open fun start(
        charset: Charset = Charset.defaultCharset(),
        buildProcess: () -> Process
    ) {
        try {
            val process = buildProcess()
            process.inputStream.gobblerThread(charset) {
                eachStdout(it)
            }.start()
            process.errorStream.gobblerThread(charset) {
                eachStderr(it)
            }.start()
            waitFor(process)
        } catch (e: Throwable) {
            onError(e)
        }

    }

    /**
     * InputStream 出力文字列を処理するスレッドを生成する
     * @param charset 出力文字列の文字セット
     * @param eachLine 出力文字列を引数とする関数
     * @return Thread
     * @throws RuntimeException ストリームの読み込み、
     *     もしくは eachLine の処理で例外エラーが発生した
     */
    private fun InputStream.gobblerThread(
        charset: Charset,
        eachLine: (stream: String) -> Unit
    ): Thread {
        return Thread {
            try {
                val sc = Scanner(this.bufferedReader(charset))
                while (sc.hasNextLine()) {
                    eachLine(sc.nextLine())
                }
            } catch (e: Throwable) {
                throw RuntimeException("problem with executing program", e)
            }
        }
    }
}

