package com.github.funczz.gradle.plugin.payara_micro

/**
 * PluginExtension
 * @author funczz
 */
open class PayaraMicroGradlePluginExtension {
    /**
     * Java ランタイム起動コマンドのフルパス
     * "java" コマンドにパスが通っている場合はブランクのままで良い
     */
    var javaBin = ""

    /**
     * payara Micro コマンドライン オプション
     * 追加のオプションをリストで定義する
     */
    var options: List<String> = listOf()

    /**
     * 成果物生成の制限時間(ミリ秒)
     * 0 未満を指定時はデフォルト値
     */
    var archiveTimeout = -1L

    /**
     * uberJar 生成の制限時間(ミリ秒)
     * 0 未満を指定時はデフォルト値
     */
    var uberJarTimeout = -1L

    /**
     * Payara Micro バージョン情報取得の制限時間(ミリ秒)
     * 0 未満を指定時はデフォルト値
     */
    var versionTimeout = -1L
}

/**
 * build.gradle.kts:
 *
 * payaraMicro {
 *     javaBin = "/path/to/java"
 *     options = listOf()
 *     archiveTimeout = 60_000L
 *     uberJarTimeout = 60_000L
 *     versionTimeout = 60_000L
 * }
 */
fun org.gradle.api.Project.payaraMicro(configure: PayaraMicroGradlePluginExtension.() -> Unit) {
    this.convention.configure(PayaraMicroGradlePluginExtension::class.java, configure)
}