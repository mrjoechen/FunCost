package tech.joechen.funcost.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import tech.joechen.funcost.compiler.utils.Logger

@OptIn(ExperimentalCompilerApi::class)
class FunCostCompilerPluginRegistrar: CompilerPluginRegistrar() {

    lateinit var logger: Logger

    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        logger = Logger(configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)!!)
        logger.info("registerExtensions")
        val enable = configuration.get(FunCostPlugin.ARG_ENABLE, "true")
        val showInputParam = configuration.get(FunCostPlugin.ARG_SHOW_INPUT_PARAM, "true")
        val showReturn = configuration.get(FunCostPlugin.ARG_SHOW_RETURN, "true")
        val showThreadName = configuration.get(FunCostPlugin.ARG_SHOW_THREAD_NAME, "true")

        logger.info("enable: $enable")
        logger.info("showInputParam: $showInputParam")
        logger.info("showReturn: $showReturn")
        logger.info("showThreadName: $showThreadName")
        IrGenerationExtension.registerExtension(FunCostIrExtension())
    }
}