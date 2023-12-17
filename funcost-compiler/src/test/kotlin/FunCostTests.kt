import kotlin.test.Test
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import tech.joechen.funcost.compiler.FunCostCommandLineProcessor
import tech.joechen.funcost.compiler.FunCostCompilerPluginRegistrar
import kotlin.test.assertEquals

@OptIn(ExperimentalCompilerApi::class)
class FunCostTests {

    @Test
    fun `test fun cost`() {
        val result = compile(
            sourceFile = SourceFile.kotlin(
                "main.kt", """ 
        import tech.joechen.funcost.anno.FunCost      

        fun main() {
          println(foo())
          println(foo("Transform", "Kotlin IR"))
        }

        @FunCost
        fun foo(param1: String? = "Hello", param2: String? = "World"): String {
          println("foo called param1=[${'$'}param1], param2=[${'$'}param2]") //注意 $ 需要转义
          return param1 + param2
        }
      """.trimIndent()
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val ktClazz = result.classLoader.loadClass("MainKt")
        val main = ktClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
        main.invoke(null)
    }

    fun compile(
        sourceFiles: List<SourceFile>,
        compilerPluginRegistrar: CompilerPluginRegistrar = FunCostCompilerPluginRegistrar(),
        commandLineProcessor: CommandLineProcessor = FunCostCommandLineProcessor(),
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = sourceFiles
            useIR = true
            compilerPluginRegistrars = listOf(compilerPluginRegistrar)
            commandLineProcessors = listOf(commandLineProcessor)
            inheritClassPath = true
        }.compile()
    }

    fun compile(
        sourceFile: SourceFile,
        plugin: CompilerPluginRegistrar = FunCostCompilerPluginRegistrar(),
        commandLineProcessor: CommandLineProcessor = FunCostCommandLineProcessor(),
    ): KotlinCompilation.Result {
        return compile(listOf(sourceFile), plugin, commandLineProcessor)
    }

}
