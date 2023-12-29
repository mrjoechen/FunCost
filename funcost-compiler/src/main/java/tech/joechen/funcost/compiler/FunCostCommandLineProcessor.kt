package tech.joechen.funcost.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import tech.joechen.funcost.compiler.FunCostPlugin.Companion.ARG_ENABLE
import tech.joechen.funcost_compiler.BuildConfig

@OptIn(ExperimentalCompilerApi::class)
class FunCostCommandLineProcessor: CommandLineProcessor {

    override val pluginId: String
        get() = BuildConfig.KOTLIN_PLUGIN_ID
    override val pluginOptions: Collection<AbstractCliOption>
        get() = listOf(
            CliOption(
                optionName = FunCostPlugin.OPTION_ENABLE,
                valueDescription = "true|false",
                description = "enable fun cost plugin",
                required = false,
                allowMultipleOccurrences = false
            ),
            CliOption(
                optionName = FunCostPlugin.OPTION_SHOW_INPUT_PARAM,
                valueDescription = "true|false",
                description = "show input param",
                required = false,
                allowMultipleOccurrences = false
            ),
            CliOption(
                optionName = FunCostPlugin.OPTION_SHOW_RETURN,
                valueDescription = "true|false",
                description = "show return",
                required = false,
                allowMultipleOccurrences = false
            ),
            CliOption(
                optionName = FunCostPlugin.OPTION_SHOW_THREAD_NAME,
                valueDescription = "true|false",
                description = "show thread name",
                required = false,
                allowMultipleOccurrences = false
            ),
        )


    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        println("processOption:: option=$option value=$value")
        return when (option.optionName) {
            FunCostPlugin.OPTION_ENABLE -> configuration.put(ARG_ENABLE, value)
            FunCostPlugin.OPTION_SHOW_INPUT_PARAM -> configuration.put(FunCostPlugin.ARG_SHOW_INPUT_PARAM, value)
            FunCostPlugin.OPTION_SHOW_RETURN -> configuration.put(FunCostPlugin.ARG_SHOW_RETURN, value)
            FunCostPlugin.OPTION_SHOW_THREAD_NAME -> configuration.put(FunCostPlugin.ARG_SHOW_THREAD_NAME, value)
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}