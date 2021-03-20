package com.github.funczz.gradle.plugin.payara_micro

/**
 * PluginExtension
 */
open class PayaraMicroGradlePluginExtension {
    var value: String = ""
}

/**
 * build.gradle.kts:
 *
 * example { value = "hello world." }
 *
 */
fun org.gradle.api.Project.example(configure: PayaraMicroGradlePluginExtension.() -> Unit) {
    this.convention.configure(PayaraMicroGradlePluginExtension::class.java, configure)
}