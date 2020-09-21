package pro.bilous.intellij.plugin.action.menu

import pro.bilous.intellij.plugin.PathTools
import pro.bilous.intellij.plugin.project.ProjectFileManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import io.swagger.util.Yaml
import pro.bilous.difhub.convert.DifHubToSwaggerConverter
import pro.bilous.difhub.write.YamlWriter
import java.lang.IllegalArgumentException

class DifHubLoadOpenApiAction : AnAction() {

	private val fileManager = ProjectFileManager()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val basePath = project!!.basePath ?: throw IllegalArgumentException("Base path not found")

        val configFolder = PathTools.getHomePath(basePath)

		if (!loadCredentials(configFolder, project)) {
			return
		}
		val system = loadSystem(configFolder, project) ?: return

		createOpenApiFiles(system, configFolder)

        VirtualFileManager.getInstance().syncRefresh()
    }

	private fun loadSystem(configFolder: String, project: Project): String? {
		val configFilePath = "file://$configFolder/settings.yaml"

		val configFile = VirtualFileManager.getInstance().findFileByUrl(configFilePath)
		if (configFile == null) {
			fileManager.createAndOpenProjectSettings(configFolder, project)
			return null
		}
		val configTree = Yaml.mapper().readTree(configFile.inputStream)
		return configTree.get("system").asText()
	}

	private fun loadCredentials(configFolder: String, project: Project): Boolean {
		val configFilePath = "file://$configFolder/.credentials.yaml"
		val configFile = VirtualFileManager.getInstance().findFileByUrl(configFilePath)
		if (configFile == null) {
			fileManager.createAndOpenProjectCredentials(configFolder, project)
			return false
		}
		val fileTree = Yaml.mapper().readTree(configFile.inputStream)

		System.setProperty("DIFHUB_ORG_NAME", fileTree.get("organization").asText())
		System.setProperty("DIFHUB_USERNAME", fileTree.get("username").asText())
		System.setProperty("DIFHUB_PASSWORD", fileTree.get("password").asText())
		return true
	}

	private fun createOpenApiFiles(system: String, configFolder: String) {
        DifHubToSwaggerConverter(system).convertAll().forEach {
            YamlWriter(system).writeFile(it.openApi, configFolder, "${it.appName.toLowerCase()}-api")
        }
    }


}
