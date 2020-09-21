package pro.bilous.difhub.write

import io.swagger.v3.core.util.Yaml
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

class YamlWriter(private val apiName: String) {

	companion object {
		const val ANSI_GREEN = "\u001B[32m"
	}

	private val writer = Yaml.mapper().writerWithDefaultPrettyPrinter()

	fun write(openApi: OpenAPI) {
		val outFolder = System.getenv("OUT_FOLDER") ?: "./codegen-cli/.test"
		writeToFolder(openApi, outFolder)
	}

	private fun convertToText(openApi: Any): String {
		return writer.writeValueAsString(openApi)
	}

	private fun writeToFolder(openApi: OpenAPI, folderPath: String) {
		val targetYamlFileName = "${apiName.toLowerCase()}-api"
		writeFile(openApi, folderPath, targetYamlFileName)
	}

	fun writeFile(obj: Any, folderPath: String, fileName: String) {
		val yamlText = convertToText(obj)
		val folder = File(folderPath)
		if (!folder.exists()) {
			folder.mkdir()
		}
		val targetFilePath = "$folderPath/$fileName.yaml"
		File(targetFilePath).bufferedWriter().use {
			out -> out.write(yamlText)
		}
		print("YAML file successfully written to $targetFilePath")
	}

	private fun print(message: String) {
		println("$ANSI_GREEN$message$ANSI_GREEN")
	}
}
