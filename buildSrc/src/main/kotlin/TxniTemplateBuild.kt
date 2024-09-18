import dev.kikugie.stonecutter.StonecutterBuild
import gradle.kotlin.dsl.accessors._523dc74e2e9552463686721a7434f18b.*
import gradle.kotlin.dsl.accessors._523dc74e2e9552463686721a7434f18b.include
import gradle.kotlin.dsl.accessors._523dc74e2e9552463686721a7434f18b.loom
import gradle.kotlin.dsl.accessors._523dc74e2e9552463686721a7434f18b.minecraft
import gradle.kotlin.dsl.accessors._523dc74e2e9552463686721a7434f18b.modApi
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import txnitemplate.ModData

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class TxniTemplateBuild internal constructor(val project: Project)  {
    lateinit var loader : String
    lateinit var sc : StonecutterBuild
    lateinit var mod : ModData

    fun setting(prop : String) : Boolean = project.properties[prop] == "true"
    fun property(prop : String) : Any? = project.properties[prop]

    fun init() {
        loader = project.loom.platform.get().name.lowercase()
        mod = ModData.from(this)

        project.run {
            version = "${mod.version}-${mod.mcVersion}"
            group = mod.group

            base { archivesName.set("${mod.id}-${mod.loader}") }
        }

        project.dependencies.apply(dependencies())
    }

    private fun dependencies(): (DependencyHandler).() -> Unit = {
        minecraft("com.mojang:minecraft:${mod.mcVersion}")

        if (mod.isFabric) {
            // JarJar Forge Config API
            if (setting("options.forgeconfig"))
                include(
                    when (mod.mcVersion) {
                        "1.19.2" -> modApi("net.minecraftforge:forgeconfigapiport-fabric:${property("deps.forgeconfigapi")}")
                        else -> modApi("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${property("deps.forgeconfigapi")}")
                    }!!
                )
        }
    }

}
