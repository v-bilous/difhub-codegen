package pro.bilous.codegen.process.filename

import org.slf4j.LoggerFactory

class FilePathResolver {

	companion object {
		private val log = LoggerFactory.getLogger(FilePathResolver::class.java)
	}

	data class ResultArgs(
		val targetPath: String,
		val shouldWriteFile: Boolean = true
	)

	/**
	 * Should resolve destination file name.
	 * @param templateData – data to use for the template generation
	 * 		useful entries to resolve the target path:
	 * 			- appPackage (example: app.client.user), usually full application package
	 * 			- package (example: app.client.user.domain)
	 * 			- basePackage (example: app.client) usually system package
	 * 		 	- entityPackage (example: app.client.user.domain), entity package
	 * 		 	- modelPackage – the same as entity package
	 * 		 	- artifactId – holds name of the application (example: user)
	 * 		 	- appNameLower - same as above, artifactId
	 * 		  	- classname - name of the file, should be as follows {classname}.kt (example: Binding.kt)
	 * @param templateName – name of the input template to use for the codegen,
	 * 			example: common/src/main/kotlin/domain/commonEntity.mustache
	 * @param filePath – full path of the directory, including file name of the target file,
	 * 			example /Users/bilous/Projects/IN/client-system/app-folder/src/main/kotlin/app/client/user/domain/Binding.kt
	 * @return path to the file
	 */
	fun resolve(templateData: Map<String, Any>, templateName: String, filePath: String): ResultArgs {
		// we push all files having templates starting with common/ to common destination

		val isCommonModel = isCommonModel(templateData)

		return when (templateName) {
			"common/src/main/kotlin/domain/commonEntity.mustache" -> {
				log.debug("common template detected, name: $templateName")
				try {
					ResultArgs(resolveCommon(templateData, templateName, filePath), isCommonModel)
				} catch (e: Exception) {
					log.error("Failed while resolving the common file target", e)
					ResultArgs(filePath, isCommonModel)
				}
			}
			else -> ResultArgs(filePath, !isCommonModel)
		}
	}

	private fun resolveCommon(templateData: Map<String, Any>, templateName: String, filePath: String): String {
		val appPackagePath = (templateData["appPackage"] as String).replace(".", "/")
		val appName = templateData["appNameLower"] as String
		val basePackagePath = (templateData["basePackage"] as String).replace(".", "/")
		log.debug("found appName: $appName, appPackagePath: $appPackagePath, basePackagePath: $basePackagePath")
		return filePath
			// 1st replace app folder to common. example: app-user to common
			.replace("app-$appName", "common")
			// 2nd replace app package to the common package (base) example: /app/client/user/ to /app/client/
			.replace("/$appPackagePath/", "/$basePackagePath/")
	}

	private fun isCommonModel(templateData: Map<String, Any>): Boolean {
		if (!templateData.containsKey("classname")) {
			return false
		}
		val modelName = templateData["classname"] as String
		return if (templateData.containsKey("commonModels")) {
			(templateData["commonModels"] as Set<String>).contains(modelName)
		} else false
	}
}
