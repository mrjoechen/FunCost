package tech.joechen.funcost.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import tech.joechen.funcost_compiler.BuildConfig

@OptIn(ExperimentalCompilerApi::class)
class FunCostCommandLineProcessor: CommandLineProcessor {
    override val pluginId: String
        get() = BuildConfig.KOTLIN_PLUGIN_ID
    override val pluginOptions: Collection<AbstractCliOption>
        get() = listOf()
}