package com.github.funczz.gradle.plugin.payara_micro

import java.io.File
import java.nio.file.FileSystems
import java.util.Optional
import java.util.concurrent.TimeoutException

/**
 * Java ランタイムを検出する
 * @author funczz
 */
object JavaRuntimeFinder {

    /**
     * デバッグ用の JAVA_HOME 環境変数値
     */
    private var debugJavaHome = Optional.empty<String>()

    /**
     * JAVA_HOME 環境変数名
     */
    private const val ENV_JAVA_HOME = "JAVA_HOME"

    /**
     * デフォルトの Java ランタイム起動コマンド
     */
    private const val DEFAULT_JAVA_BIN = "java"

    /**
     * デフォルトのプロセス起動の制限時間
     */
    const val DEFAULT_TIMEOUT = 60_000L

    /**
     * デバッグ用に JAVA_HOME 環境変数値をセットする
     */
    fun setDebugJavaHome(v: Optional<String>) {
        debugJavaHome = v
    }

    /**
     * Java ランタイムのバージョンを取得する
     * @param javaBin Java ランタイム起動コマンド
     * @param timeout プロセス起動の制限時間
     * @return バージョン情報
     * @throws TimeoutException コマンドが制限時間内に完了しなかった
     */
    fun getVersion(javaBin: String, timeout: Long = DEFAULT_TIMEOUT): String {
        val stderr: MutableList<String> = mutableListOf()
        val commandLine = mutableListOf(
            javaBin,
            "-version",
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
            waitFor {
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


    /**
     * Java ランタイム起動コマンド のパスを取得する。
     * 引数 javaBin が実在するファイルのパスであれば、 フルパスを返す。
     * javaBin がブランクかつ ${JAVA_HOME}/bin/java が実在するファイルのパスであれば、 フルパスを返す。
     * javaBin 及び環境変数がブランクなら、DEFAULT_JAVA_BIN を返す。
     * なお、debugModeJavaHome が Some の場合は 環境変数値の代わりに Some 値が用いられる。
     * @param javaBin 任意のパス
     * @param default 未検出時の返り値
     * @return String フルパス
     */
    fun findBinPath(javaBin: String? = null, default: String = DEFAULT_JAVA_BIN): String {
        getFile(javaBin)?.let {
            return it.canonicalPath
        }
        val separator = FileSystems.getDefault().separator
        if (debugJavaHome.isPresent) {
            return "%s%s%s%s%s".format(debugJavaHome.get(), separator, "bin", separator, "java")
        }
        val env = System.getenv(ENV_JAVA_HOME)
        env?.let { s ->
            getFile("%s%s%s%s%s".format(s, separator, "bin", separator, "java"))?.let {
                return it.canonicalPath
            }
        }
        return default
    }

    /**
     * 引数のパスの File オブジェクトを返す
     * @param path ファイルパス
     * @return パスが実在する場合は File オブジェクト、実在しなない場合は null
     */
    fun getFile(path: String?): File? {
        if (path != null && path.isNotBlank()) {
            val file = File(path)
            if (file.exists()) return file
        }
        return null
    }

}
