package com.github.funczz.gradle.plugin.payara_micro

import com.github.funczz.gradle.plugin.payara_micro.PayaraMicroProcess.Companion.DEFAULT_CHARSET

/**
 * PluginExtension
 * @author funczz
 */
open class PayaraMicroGradlePluginExtension {
    /**
     * Java ランタイム起動コマンドのフルパス
     * $JAVA_HOME が定義されているか、
     * "java" コマンドにパスが通っている場合はブランクのままで良い
     */
    var javaBin = ""

    /**
     * payara Micro コマンドライン オプション
     * 追加のオプションをリストで定義する
     */
    var options: List<String> = listOf()

    /**
     * JRE バージョン情報取得の制限時間(ミリ秒)
     * 0 未満を指定時はデフォルト値
     */
    var javaVersionTimeout = -1L

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
    var payaraMicroVersionTimeout = -1L

    /**
     * payara Micro プロセス起動前の遅延時間（ミリ秒)
     * 0 未満を指定時はデフォルト値
     */
    var processInitialDelay = -1L

    /**
     * payara Micro プロセス起動に制限時間を設けた際の、プロセス確認の間隔（ミリ秒)
     * 0 未満を指定時はデフォルト値
     */
    var processPeriod = -1L

    /**
     * payara Micro プロセス起動の制限時間（ミリ秒)
     * 0 未満を指定時はデフォルト値
     */
    var processTimeout = -1L

    /**
     * payara Micro プロセス標準出力、エラー出力の文字セット
     */
    var processCharset = DEFAULT_CHARSET
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
 *     processInitialDelay = 3_000L
 *     processPeriod = 1_000L
 *     processTimeout = -1L
 *     processCharset = Charset.defaultCharset()
 * }
 */
fun org.gradle.api.Project.payaraMicro(configure: PayaraMicroGradlePluginExtension.() -> Unit) {
    this.convention.configure(PayaraMicroGradlePluginExtension::class.java, configure)
}