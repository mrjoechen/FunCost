package tech.joechen.funcost.compiler.utils

import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.name.FqName

const val TAG = "[FunCost]"

@OptIn(FirIncompatiblePluginAPI::class)
fun IrPluginContext.printlnFunc(): IrSimpleFunctionSymbol = referenceFunctions(FqName("kotlin.io.println")).single {
  val parameters = it.owner.valueParameters
  parameters.size == 1 && parameters[0].type == irBuiltIns.anyNType
}

@OptIn(FirIncompatiblePluginAPI::class)
fun IrPluginContext.monotonicClass(): IrClassSymbol = referenceClass(FqName("kotlin.time.TimeSource.Monotonic"))!!

@OptIn(FirIncompatiblePluginAPI::class)
fun IrPluginContext.markNowFunc(): IrSimpleFunctionSymbol = referenceFunctions(FqName("kotlin.time.TimeSource.markNow")).single()

@OptIn(FirIncompatiblePluginAPI::class)
fun IrPluginContext.elapsedNowFunc() = referenceFunctions(FqName("kotlin.time.TimeMark.elapsedNow")).single()

@OptIn(FirIncompatiblePluginAPI::class)
fun IrPluginContext.threadClass() = referenceClass(FqName("java.lang.Thread"))

fun IrClassSymbol.currentThreadFunc(): IrSimpleFunctionSymbol {
  return this.functions.single {
    it.owner.name.asString() == "currentThread"
  }
}

@OptIn(FirIncompatiblePluginAPI::class)
fun IrPluginContext.threadNameFunc() = referenceFunctions(FqName("java.lang.Thread.getName")).single()


fun IrBuilderWithScope.costEnter(
  pluginContext: IrPluginContext,
  function: IrFunction,
  thread: IrValueDeclaration? = null
): IrCall {
  println("costEnter:")
  val concat = irConcat() // 拼接目标函数信息 【方法名（参数1，参数2 ... 参数n）】
  concat.addArgument(irString("$TAG ⇢ ${function.name}("))
  function.valueParameters.forEachIndexed { index, irValueParameter ->
    if (index > 0) concat.addArgument(irString(", "))
    concat.addArgument(irString("${irValueParameter.name}="))
    concat.addArgument(irGet(irValueParameter)) // irGet 获取参数具体值是什么
  }
  concat.addArgument(irString(")"))

  thread?.apply {
    concat.addArgument(irString(" on Thread ["))
    concat.addArgument(irCall(pluginContext.threadNameFunc()).also {
      it.dispatchReceiver = irGet(thread)
    })
    concat.addArgument(irString("]"))
  }
  return irCall(pluginContext.printlnFunc()).also { //调用 println()
    it.putValueArgument(0, concat)
  }
}

fun IrBuilderWithScope.costExit(
  pluginContext: IrPluginContext,
  function: IrFunction,
  startTime: IrValueDeclaration,
  result: IrExpression? = null
): IrCall {
  println("costExit:")
  val concat = irConcat()
  concat.addArgument(irString("$TAG ⇠ ${function.name} costs ["))
  concat.addArgument(irCall(pluginContext.elapsedNowFunc()).also { // 调用 elapsedNow()
    it.dispatchReceiver = irGet(startTime) // 通过 irGet 拿到调用者
  })
  if (result != null) {
    concat.addArgument(irString("] return: "))
    concat.addArgument(result)
  } else {
    concat.addArgument(irString("]"))
  }
  return irCall(pluginContext.printlnFunc()).also {
    it.putValueArgument(0, concat)
  }
}

fun IrBuilderWithScope.threadName(
  pluginContext: IrPluginContext,
  function: IrFunction,
  thread: IrValueDeclaration,
): IrCall {
  println("threadName:")
  val concat = irConcat()
  concat.addArgument(irString("$TAG ${function.name} on Thread ["))
  concat.addArgument(irCall(pluginContext.threadNameFunc()).also {
    it.dispatchReceiver = irGet(thread)
  })
  concat.addArgument(irString("]"))
  return irCall(pluginContext.printlnFunc()).also {
    it.putValueArgument(0, concat)
  }
}