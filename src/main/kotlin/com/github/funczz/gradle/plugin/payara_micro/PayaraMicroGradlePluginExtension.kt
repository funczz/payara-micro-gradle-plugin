package com.github.funczz.gradle.plugin.payara_micro

/**
 * PluginExtension
 * @author funczz
 */
open class PayaraMicroGradlePluginExtension {
    /**
     * Java ランタイム起動コマンド
     * javaコマンドにパスが通っている場合はブランクのままで良い
     */
    var javaBin = ""

    /**
     * payara Micro コマンドライン オプション
     * 追加のオプションをリストで定義する
     */
    var options: List<String> = listOf()
}

/**
 * build.gradle.kts:
 *
 * payaraMicro {
 *     javaBin = "/opt/java/bin/java"
 *     options = listOf()
 * }
 */
fun org.gradle.api.Project.payaraMicro(configure: PayaraMicroGradlePluginExtension.() -> Unit) {
    this.convention.configure(PayaraMicroGradlePluginExtension::class.java, configure)
}