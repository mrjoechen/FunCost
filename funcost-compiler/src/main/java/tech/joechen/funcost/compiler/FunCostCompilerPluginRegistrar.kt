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
        IrGenerationExtension.registerExtension(FunCostIrExtension())
    }
}