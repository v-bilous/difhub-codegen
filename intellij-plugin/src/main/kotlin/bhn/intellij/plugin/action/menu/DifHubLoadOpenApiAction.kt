package bhn.intellij.plugin.action.menu

import bhn.intellij.plugin.Icons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFileManager
import io.swagger.util.Yaml
import spd.difhub.convert.DifHubToSwaggerConverter
import spd.difhub.write.YamlWriter

class DifHubLoadOpenApiAction : AnAction(Icons.restApi) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val basePath = project!!.basePath

        val configFolder = "$basePath/.openapi-generator"
        val configFilePath = "file://$configFolder/settings.yaml"

        val configFile = VirtualFileManager.getInstance().findFileByUrl(configFilePath)
        if (configFile == null) {
            openProjectProperties()
            return
        }
        val configTree = Yaml.mapper().readTree(configFile.inputStream)
        val system = configTree.get("system").asText()

        createOpenApiFiles(system, configFolder)

        VirtualFileManager.getInstance().syncRefresh()
    }

    private fun createOpenApiFiles(system: String, configFolder: String) {
        DifHubToSwaggerConverter(system).convertAll().forEach {
            YamlWriter(system).writeFile(it.openApi, configFolder, "${it.appName.toLowerCase()}-api")
        }
    }

    private fun openProjectProperties() {
        TODO("not implemented")
    }
}