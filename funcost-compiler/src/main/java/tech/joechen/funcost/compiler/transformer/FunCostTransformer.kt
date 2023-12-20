package tech.joechen.funcost.compiler.transformer

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irBlock
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import tech.joechen.funcost.compiler.utils.costEnter
import tech.joechen.funcost.compiler.utils.currentThreadFunc
import tech.joechen.funcost.compiler.utils.markNowFunc
import tech.joechen.funcost.compiler.utils.monotonicClass
import tech.joechen.funcost.compiler.utils.threadClass
import tech.joechen.funcost.compiler.utils.threadName

class FunCostTransformer(
    private val pluginContext: IrPluginContext
): IrElementTransformerVoidWithContext() {

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val body = declaration.body
        val annotationClass = pluginContext.referenceClass(ClassId(FqName("tech.joechen.funcost.anno"), Name.identifier("FunCost")))!!
        if (body != null && declaration.hasAnnotation(annotationClass)) {
            println("Annotated function: ${declaration.name}")
            declaration.body = irAddCost(declaration, body)
        }
        return super.visitFunctionNew(declaration)
    }

    private fun irAddCost(irFunction: IrFunction, irBody: IrBody): IrBody {

        return DeclarationIrBuilder(pluginContext, irFunction.symbol).irBlockBody {
            +costEnter(pluginContext, irFunction)

            val thread = irTemporary(irCall(pluginContext.threadClass()!!.currentThreadFunc()))

            val startTime = irTemporary(irCall(pluginContext.markNowFunc()).also {
                it.dispatchReceiver = irGetObject(pluginContext.monotonicClass())
            })
            +irBlock(resultType = irFunction.returnType) {
                for(statement in irBody.statements) {
                    +statement
                }
            }.transform(CostTimeReturnTransformer(pluginContext, irFunction, startTime, thread), null)
        }
    }
}