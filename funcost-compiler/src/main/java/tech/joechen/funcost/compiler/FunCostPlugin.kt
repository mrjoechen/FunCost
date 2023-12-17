package tech.joechen.funcost.compiler

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import tech.joechen.funcost_compiler.BuildConfig

class FunCostPlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        val funCostExtension = target.extensions.create("funcost", FunCostExtension::class.java)
        if (funCostExtension.enable){
            operator fun Configuration.plusAssign(dependency: String) {
                dependencies.add(target.dependencies.create(dependency))
            }

            target.afterEvaluate {
                target.configurations.all { config ->
                    val name = config.name
                    if (name != "api") return@all
                    config += "${BuildConfig.KOTLIN_PLUGIN_GROUP}:funcost-annotations:${BuildConfig.KOTLIN_PLUGIN_VERSION}"
                }
            }

        }
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val options = ArrayList<SubpluginOption>()

        try {
            val extension = project.extensions.getByType(FunCostExtension::class.java)
            if (extension.enable) {
                options += SubpluginOption("enable", "true")
            }
        }catch (ex: Exception){
            ex.printStackTrace()

        }
        return project.provider { options }
    }

    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
            artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
            version = BuildConfig.KOTLIN_PLUGIN_VERSION,
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.target.project.plugins.hasPlugin(FunCostPlugin::class.java)
}