package pro.bilous.codegen.configurator

import io.swagger.v3.oas.models.OpenAPI
import org.openapitools.codegen.ClientOptInput
import pro.bilous.codegen.core.ICustomConfigurator
import pro.bilous.codegen.core.IGenerateInvoker
import org.slf4j.LoggerFactory

class ConfiguratorWrapper(
	val instance: ICustomConfigurator,
	private val generateInvoker: IGenerateInvoker
) {

	private val log = LoggerFactory.getLogger(ConfiguratorWrapper::class.java)

	fun generate() {
		val settings = instance.getCustomSettings() ?: throw IllegalArgumentException("Settings file is required")

		val props = settings.dynamicProperties["application"]
		val apps = mutableListOf<String>()
		if (props is List<*> && instance.getSpecCopyValue().endsWith("/")) {
			apps.addAll(props.map { it.toString().toLowerCase() })
		}

		val specDir = instance.getSpecCopyValue()
		val basePackage =  settings.dynamicProperties["basePackage"].toString()
		val database = settings.dynamicProperties["database"]?.toString() ?: "MySQL"

		val system = settings.dynamicProperties["system"]!!.toString()
		instance.setCustomProperty("systemLower", system.toLowerCase())
		instance.setCustomProperty("database", DatabaseResolver.getByType(database))
		instance.setCustomProperty("appsLower", apps)

		generateApps(apps, specDir, basePackage)
	}

	private fun generateApps(apps: List<String>, specDir: String, basePackage: String) {
		val optInputs = mutableMapOf<String, ClientOptInput>()
		apps.forEachIndexed { index, appName ->
			try {
				val optInput = generateOne(GenerateArgs(index, appName, specDir, basePackage))
				optInputs[appName] = optInput
				log.info("ClientOptInput creation for spec $appName completed")
			} catch (error: Throwable) {
				log.error("Failed ClientOptInput creation for app $appName", error)
			}
		}

		optInputs.onEachIndexed { index, entry ->
			try {
				generateInvoker.invoke(index, entry.value)
				log.info("Code generation for app ${entry.key} completed")
			} catch (error: Throwable) {
				log.error("Failed generation for the app ${entry.key}", error)
			}
		}
	}

	private fun generateOne(args: GenerateArgs): ClientOptInput {
		val appName = args.appName.toLowerCase()
		val inputSpecFile = "${args.specDir}$appName-api.yaml"
		instance.setCustomInputSpec(inputSpecFile)
		instance.setCustomArtifactId(appName)
		instance.setCustomProperty("appPackage", "${args.basePackage}.$appName")
		instance.setCustomProperty("appRealName", args.appName.capitalize())
		instance.setCustomProperty("appNameLower", appName)

		val optInput: ClientOptInput = instance.toCustomClientOptInput()

		val appAlias = readAppAlias(optInput.openAPI)
		if (appAlias != null) {
			instance.setCustomProperty("appNameAlias", appAlias)
			instance.setCustomProperty("appNameLower", appAlias)
			instance.setCustomProperty("appPackage", "${args.basePackage}.$appAlias")
		}
		return optInput
	}

	private fun readAppAlias(openAPI: OpenAPI?): String? {
		val ext = openAPI?.info?.extensions
		if (ext != null && ext.containsKey("x-app-alias")) {
			return ext["x-app-alias"].toString()
		}
		return null
	}

	data class GenerateArgs(
		val index: Int,
		val appName: String,
		val specDir: String,
		val basePackage: String
	)
}
