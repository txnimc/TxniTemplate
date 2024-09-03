import org.gradle.api.Plugin
import org.gradle.api.plugins.ExtensionAware


class TxniTemplatePlugin : Plugin<ExtensionAware> {


    override fun apply(target: ExtensionAware) {
        target.extensions.create("txnitemplate", TxniTemplateBuild::class.java, target)
    }
}
