package pro.bilous.codegen.configurator

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
		val settings = instance.getCustomSettings()
		if (settings == null) {
 			generateOne(0)
			return
		}
		val props = settings.dynamicProperties["application"]
		val apps = mutableListOf<String>()
		if (props is List<*> && instance.getSpecCopyValue().endsWith("/")) {
			apps.addAll(props.map { it.toString().toLowerCase() })
		}

		val specDir = instance.getSpecCopyValue()
		val basePackage =  settings.dynamicProperties["basePackage"]
		val database = settings.dynamicProperties["database"]?.toString() ?: "MySQL"

		val system = settings.dynamicProperties["system"]!!.toString()
		instance.setCustomProperty("systemLower", system.toLowerCase())
		instance.setCustomProperty("appsLower", apps)
		instance.setCustomProperty("database",
			DatabaseResolver.getByType(database)
		)

		apps.forEachIndexed { index, appName ->
			try {
				val app = appName.toLowerCase()
				val inputSpecFile = "${specDir}$app-api.yaml"
				instance.setCustomInputSpec(inputSpecFile)
				instance.setCustomArtifactId(app)
				instance.setCustomProperty("appPackage", "$basePackage.$app")
				instance.setCustomProperty("appRealName", appName.capitalize())
				instance.setCustomProperty("appNameLower", app)

				generateOne(index)
				log.info("Code generation for spec $appName completed")
			} catch (error: Throwable) {
				log.error("Failed generation for the app $appName", error)
			}
		}
	}

	private fun generateOne(index: Int = 0) {
		val entityOptInput: ClientOptInput = instance.toCustomClientOptInput()
		generateInvoker.invoke(index, entityOptInput)
	}
}
