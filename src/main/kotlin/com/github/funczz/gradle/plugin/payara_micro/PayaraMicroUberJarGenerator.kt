package com.github.funczz.gradle.plugin.payara_micro

import java.io.File

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
     * 生成元の War ファイル
     */
    private val rootWar: File,

    /**
     * 生成先の UberJar ファイル
     */
    private val uberJar: File,

    /**
     * payara Micro コマンドライン オプション
     */
    private val options: List<String> = listOf(),

    /**
     * payara Micro 起動前の遅延時間（ミリ秒)
     */
    private val initialDelay: Long = 0L,

    /**
     * 生成時の作業ディレクトリの指定
     */
    workDir: File? = null,

    /**
     * payara Micro 起動用のプロセスビルダー
     */
    private val processBuilder: ProcessBuilder = ProcessBuilder().inheritIO(),
) {

    /**
     * 作業ディレクトリ
     * workDir を割り当てる。未指定時は UberJar ファイルの親ディレクトリ
     */
    private val workingDirectory = workDir ?: uberJar.parentFile

    /**
     * 生成元の War ファイルのコピー先ファイル
     * 生成時の War ファイル名を ROOT.war に統一する
     */
    private val currentRootWar = File(workingDirectory, "ROOT.war")

    /**
     * 生成元の War ファイル を currentRootWar へコピー
     */
    private fun createCurrentRootWarFile() {
        if (rootWar.canonicalPath != currentRootWar.canonicalPath) {
            if (currentRootWar.exists()) currentRootWar.delete()
            rootWar.copyTo(currentRootWar)
        }
    }

    /**
     * payara Micro コマンド生成
     */
    private fun outputUberJarParams(): Array<String> {
        return mutableListOf(
            "java",
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

    /**
     * UberJar ファイルを生成
     */
    fun outputUberJar() {
        createCurrentRootWarFile()
        Thread.sleep(initialDelay)
        processBuilder.apply {
            command(*outputUberJarParams())
            directory(workingDirectory)
        }.start().apply {
            waitFor()
        }
    }

}