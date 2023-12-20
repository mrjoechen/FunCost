package tech.joechen.funcost.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump
import tech.joechen.funcost.compiler.transformer.FunCostTransformer

class FunCostIrExtension: IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        println("------ before transform dump IR -------")
        println(moduleFragment.dump())

        println("------  Transforming... -------")
        moduleFragment.transform(FunCostTransformer(pluginContext), null)

        println("------ after transform dump IR -------")
        println(moduleFragment.dump())
    }
}