package tech.joechen.funcost.compiler.transformer

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class FunCostTransformer(
    private val pluginContext: IrPluginContext
): IrElementTransformerVoidWithContext() {

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val body = declaration.body
        val annotationClass = pluginContext.referenceClass(ClassId(FqName("tech.joechen.funcost.anno"), Name.identifier("FunCost")))!!
        if (body != null && declaration.hasAnnotation(annotationClass)) {
            declaration.body = irAddCost(declaration, body)
        }
        return super.visitFunctionNew(declaration)
    }

    private fun irAddCost(declaration: IrFunction, body: IrBody): IrBody? {
        return body
    }
}