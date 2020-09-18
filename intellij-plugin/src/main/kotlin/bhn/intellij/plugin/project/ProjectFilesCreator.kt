package bhn.intellij.plugin.project

import bhn.intellij.plugin.gen.CodeGenerator
import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VirtualFileManager
import org.slf4j.LoggerFactory
import spd.difhub.convert.DifHubToSwaggerConverter
import spd.difhub.write.YamlWriter
import java.lang.Exception

class ProjectFilesCreator {
    private val log = LoggerFactory.getLogger(ProjectFilesCreator::class.java)

    fun createFiles(module: Module, request: ProjectCreationRequest) {
        val project = module.project
        val basePath = project.basePath

        val configFolder = "$basePath/.openapi-generator"

        createCredentialsFile(request, configFolder)
        createOpenApiFiles(request, configFolder)
        createConfigFile(request, configFolder)
        executeCodeGenerator(basePath!!)

        VirtualFileManager.getInstance().syncRefresh()
    }

    private fun createCredentialsFile(request: ProjectCreationRequest, configFolder: String) {
        val fileContent = mapOf(
            "username" to request.username,
            "password" to request.password,
            "organization" to request.organization
        )
        YamlWriter(request.system).writeFile(fileContent, configFolder, ".credentials")
    }

    private fun createOpenApiFiles(request: ProjectCreationRequest, configFolder: String) {
        DifHubToSwaggerConverter(request.system).convertAll().forEach {
            try {
                YamlWriter(request.system).writeFile(it.openApi, configFolder, "${it.appName.toLowerCase()}-api")
            } catch (error: Exception) {
                log.error("Failed system generation $it.appName", error)
            }
        }
    }

    private fun createConfigFile(request: ProjectCreationRequest, configFolder: String) {
        val configMap = mapOf(
            "system" to request.system,
            "application" to request.applications,
            "groupId" to request.groupId,
            "artifactId" to request.artifactId,
            "artifactVersion" to request.version,
            "artifactDescription" to request.description,
            "title" to request.title,
            "basePackage" to request.basePackage,
            "dbName" to request.dbName,
            "addKotlin" to request.addKotlin,
            "dateLibrary" to request.dateLibrary,
            "addBindingEntity" to request.addBindingEntity
        )
        YamlWriter(request.system).writeFile(configMap, configFolder, "settings")
    }

    private fun executeCodeGenerator(projectPath: String) {
        CodeGenerator().generate(projectPath)
    }
}