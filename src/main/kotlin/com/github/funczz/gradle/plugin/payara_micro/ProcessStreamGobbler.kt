package com.github.funczz.gradle.plugin.payara_micro

import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

open class ProcessStreamGobbler {

    open var eachStdout: (String) -> Unit = {}

    open var eachStderr: (String) -> Unit = {}

    open var onError: (Throwable) -> Unit = {}

    open var waitFor: (Process) -> Unit = {
        it.waitFor()
    }

    open fun eachStdout(function: (String) -> Unit) {
        eachStdout = function
    }

    open fun eachStderr(function: (String) -> Unit) {
        eachStderr = function
    }

    open fun onError(function: (Throwable) -> Unit) {
        onError = function
    }

    open fun waitFor(function: (Process) -> Unit) {
        waitFor = function
    }

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

