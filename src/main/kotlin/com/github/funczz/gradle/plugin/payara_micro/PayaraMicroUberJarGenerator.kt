package com.github.funczz.gradle.plugin.payara_micro

import java.io.File
import java.util.concurrent.TimeoutException

/**
 * PayaraMicro UberJar ファイル生成
 * @author funczz
 */
class PayaraMicroUberJarGenerator(
    /**
     * payara Micro Jar ファイル
     */
    private val payaraMicroJarFile: File,

    /**
     * payara Micro 起動前の遅延時間（ミリ秒)
     */
    private val initialDelay: Long = DEFAULT_INITIAL_DELAY,

    /**
     * プロセス起動の制限時間
     */
    private val timeout: Long = DEFAULT_TIMEOUT,

    /**
     * 生成時の作業ディレクトリの指定
     */
    private val workDir: File? = null,

    /**
     * Java ランタイム起動コマンド
     */
    private val javaBin: String = DEFAULT_JAVA_BIN,

    ) {

    /**
     * UberJar ファイルを生成
     * @param rootWar 生成元の War ファイル
     * @param uberJar 生成先の UberJar ファイル
     * @param options payara Micro コマンドライン オプション
     * @throws TimeoutException コマンドが制限時間内に完了しなかった
     */
    fun outputUberJar(rootWar: File, uberJar: File, options: List<String> = listOf()) {
        /**
         * 作業ディレクトリ
         * workDir を割り当てる。未指定時は UberJar ファイルの親ディレクトリ
         */
        val workingDirectory = workDir ?: uberJar.parentFile

        /**
         * 生成元の War ファイルのコピー先ファイル
         * 生成時の War ファイル名を ROOT.war に統一する
         */
        val currentRootWar = File(workingDirectory, "ROOT.war")

        /**
         * UberJar ファイル生成のコマンドライン配列
         */
        val commandLine = outputUberJarParams(currentRootWar = currentRootWar, uberJar = uberJar, options = options)

        createCurrentRootWarFile(rootWar = rootWar, currentRootWar = currentRootWar)
        Thread.sleep(initialDelay)
        var count: Long = 0L
        ProcessBuilder().apply {
            command(*commandLine)
            directory(workingDirectory)
        }.start().also {
            while (it.isAlive) {
                if (count >= timeout) break
                Thread.sleep(1L)
                count++
            }
            val isAlive = it.isAlive
            it.destroy()
            if (isAlive) throw TimeoutException("command line: ${commandLine.joinToString(" ")}")
        }
    }

    /**
     * 生成元の War ファイル を currentRootWar へコピー
     * @param rootWar 生成元の War ファイル
     * @param currentRootWar 生成用に ファイル名を ROOT.war とした War ファイル
     */
    private fun createCurrentRootWarFile(rootWar: File, currentRootWar: File) {
        if (rootWar.canonicalPath != currentRootWar.canonicalPath) {
            if (currentRootWar.exists()) currentRootWar.delete()
            rootWar.copyTo(currentRootWar)
        }
    }

    /**
     * payara Micro コマンド生成
     * @param currentRootWar 生成用に ファイル名を ROOT.war とした War ファイル
     * @param uberJar 生成先の UberJar ファイル
     * @param options payara Micro コマンドライン オプション
     * @return コマンドラインの配列
     */
    private fun outputUberJarParams(
        currentRootWar: File,
        uberJar: File,
        options: List<String>
    ): Array<String> {
        return mutableListOf(
            javaBin,
            "-jar",
            payaraMicroJarFile.canonicalPath,
            "--deploy",
            currentRootWar.canonicalPath,
            "--outputUberJar",
            uberJar.canonicalPath
        ).apply {
            addAll(options)
        }.toTypedArray()
    }

    companion object {
        /**
         * デフォルトのJava ランタイム起動コマンド
         */
        private const val DEFAULT_JAVA_BIN = "java"

        /**
         * デフォルトの payara Micro 起動前の遅延時間（ミリ秒)
         */
        private const val DEFAULT_INITIAL_DELAY = 0L

        /**
         * デフォルトのプロセス起動の制限時間
         */
        private const val DEFAULT_TIMEOUT = 5000L
    }
}