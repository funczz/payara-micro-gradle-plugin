package com.github.funczz.gradle.plugin.payara_micro

import java.io.File
import java.nio.charset.Charset
import java.util.jar.JarFile

/**
 * payara Micro の起動及びデプロイ、再デプロイ、停止を行う
 * @author funczz
 */
class PayaraMicroProcess(
    /**
     * payara Micro Jar ファイル
     */
    private val payaraMicroJarFile: File,

    /**
     * デプロイする War ファイル
     */
    private val rootWar: File,

    /**
     * デプロイ先のディレクトリ
     */
    private val rootDir: File,

    /**
     * payara Micro コマンドライン オプション
     */
    private val options: List<String> = listOf(),

    /**
     * payara Micro 起動前の遅延時間（ミリ秒)
     */
    private val initialDelay: Long = DEFAULT_INITIAL_DELAY,

    /**
     * payara Micro プロセス起動に制限時間を設けた際の、プロセス確認の間隔（ミリ秒)
     */
    private val period: Long = DEFAULT_PERIOD,

    /**
     * payara Micro プロセス起動の制限時間（ミリ秒)
     * 0L 以下の値を指定した場合は無制限
     */
    private val timeout: Long = DEFAULT_TIMEOUT,

    /**
     * ストリームの文字セット
     */
    private val charset: Charset = DEFAULT_CHARSET,

    /**
     * Java ランタイム起動コマンド
     */
    private val javaBin: String = DEFAULT_JAVA_BIN,
) {

    /**
     * プロセスの標準出力に対する処理
     */
    private var eachStdout: (String) -> Unit = {}

    /**
     * プロセスのエラー出力に対する処理
     */
    private var eachStderr: (String) -> Unit = {}

    /**
     * プロセスの例外エラーに対する処理
     */
    private var onError: (Throwable) -> Unit = {}

    /**
     * プロセスの待機処理
     */
    private val onWaitFor: (Process) -> Unit = {
        val start = System.currentTimeMillis()
        loop@ while (true) {
            if (!reloadFile.exists()) {
                break@loop
            }
            Thread.sleep(period)
            if (timeout > DEFAULT_TIMEOUT && System.currentTimeMillis() - start >= timeout) {
                break@loop
            }
        }
        it.destroy()
        it.waitFor()
    }

    /**
     * .reload ファイル
     * デプロイ先のディレクトリに配置する
     * このファイルの状態で payara Micro の再デプロイ、停止を行う
     */
    private val reloadFile: File by lazy {
        File(rootDir.canonicalFile, ".reload")
    }

    /**
     * プロセスの標準出力を処理する関数を代入
     */
    fun eachStdout(function: (String) -> Unit) {
        eachStdout = function
    }

    /**
     * プロセスのエラー出力を処理する関数を代入
     */
    fun eachStderr(function: (String) -> Unit) {
        eachStderr = function
    }

    /**
     * プロセスの例外エラーを処理する関数を代入
     */
    fun onError(function: (Throwable) -> Unit) {
        onError = function
    }

    /**
     * payara Micro を起動して、 War ファイルをデプロイする
     */
    fun payaraStartWar() {
        payaraRedeployWar()
        Thread.sleep(initialDelay)
        val pb = ProcessBuilder().apply {
            command(*payaraStartWarParams())
            directory(rootWar.parentFile)
        }
        ProcessStreamGobbler().also {
            it.eachStdout = eachStdout
            it.eachStderr = eachStderr
            it.onError = onError
            it.onWaitFor = onWaitFor
        }.start(charset = charset) { pb.start() }
    }

    /**
     * payara Micro に War ファイルを再デプロイする
     */
    fun payaraRedeployWar() {
        createDirectory(rootDir)
        cleanDirectory(rootDir, listOf(reloadFile))
        extractJarFile(JarFile(rootWar), rootDir)
        touchReloadFile()
    }

    /**
     * payara Micro を停止する
     */
    fun payaraStopWar() {
        deleteReloadFile()
    }

    /**
     * payara Micro コマンドを生成する
     */
    private fun payaraStartWarParams(): Array<String> {
        return mutableListOf(
            javaBin,
            "-jar",
            payaraMicroJarFile.canonicalPath,
            "--deploy",
            rootDir.canonicalPath
        ).apply {
            addAll(options)
        }.toTypedArray()
    }

    /**
     * .reload ファイルを生成する
     */
    private fun createReloadFile() = try {
        reloadFile.outputStream().bufferedWriter().use {
            it.write("")
            it.flush()
        }
    } catch (e: Exception) {
        e.stackTrace
    }

    /**
     * .reload ファイルの変更日時を更新する
     */
    private fun touchReloadFile() = try {
        if (!reloadFile.exists()) createReloadFile()
        reloadFile.setLastModified(System.currentTimeMillis())
    } catch (e: Exception) {
        e.stackTrace
    }

    /**
     * .reload ファイルを削除する
     */
    private fun deleteReloadFile() {
        if (!reloadFile.exists()) return
        try {
            reloadFile.deleteRecursively()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    /**
     * ディレクトリを作成する
     */
    private fun createDirectory(directory: File) {
        val canonicalDirectory = directory.canonicalFile
        if (canonicalDirectory.exists()) return
        try {
            canonicalDirectory.mkdirs()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    /**
     * ディレクトリから、例外を除くファイルを削除する
     */
    private fun cleanDirectory(directory: File, exclude: List<File> = listOf()) {
        val excludeCanonicalFile = exclude.map { it.canonicalFile }
        try {
            directory.canonicalFile.listFiles()?.filterNot {
                excludeCanonicalFile.contains(it.canonicalFile)
            }?.forEach {
                it.deleteRecursively()
            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    /**
     * Jar ファイルを展開する
     */
    private fun extractJarFile(jarFile: JarFile, intoDirectory: File) {
        jarFile.entries().iterator().forEach {
            val createFile = File(intoDirectory, it.name)
            try {
                when (it.isDirectory) {
                    true -> if (!createFile.exists()) {
                        createFile.mkdirs()
                    }
                    else -> createFile.outputStream().use { os ->
                        jarFile.getInputStream(it).copyTo(os)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    companion object {
        /**
         * デフォルトのJava ランタイム起動コマンド
         */
        private const val DEFAULT_JAVA_BIN = "java"

        /**
         * デフォルトの payara Micro 起動前の遅延時間（ミリ秒)
         */
        const val DEFAULT_INITIAL_DELAY = 3_000L

        /**
         * デフォルトの プロセス起動に制限時間を設けた際の、プロセス確認の間隔（ミリ秒)
         */
        const val DEFAULT_PERIOD = 1_000L

        /**
         * デフォルトの プロセス起動の制限時間（ミリ秒)
         */
        const val DEFAULT_TIMEOUT = 0L

        /**
         * デフォルトの文字セット
         */
        val DEFAULT_CHARSET = Charset.defaultCharset()!!
    }

}