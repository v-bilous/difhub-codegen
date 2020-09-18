package org.openapitools.codegen.cmd

import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.DataCodeGenerator
import org.openapitools.codegen.InCustomCodegenConfigurator
import org.slf4j.LoggerFactory

class ConfiguratorWrapper(val instance: InCustomCodegenConfigurator) {
	private val log = LoggerFactory.getLogger(ConfiguratorWrapper::class.java)
	fun generate() {
		val settings = instance.settings
		if (settings == null) {
 			generateOne(0)
			return
		}
		val props = settings.dynamicProperties["application"]
		val apps = mutableListOf<String>()
		if (props is List<*> && instance.specCopy!!.endsWith("/")) {
			apps.addAll(props.map { it.toString().toLowerCase() })
		}

		val specDir = instance.specCopy
		val basePackage =  settings.dynamicProperties["basePackage"]

		val system = settings.dynamicProperties["system"]!!.toString()
		instance.addAdditionalProperty("systemLower", system.toLowerCase())
		instance.addAdditionalProperty("appsLower", apps)

		apps.forEachIndexed { index, appName ->
			try {
				val app = appName.toLowerCase()
				val inputSpecFile = "${specDir}$app-api.yaml"
				instance.setInputSpec(inputSpecFile)
				instance.setArtifactId(app)
				instance.addAdditionalProperty("appPackage", "$basePackage.$app")
				instance.addAdditionalProperty("appRealName", appName.capitalize())
				instance.addAdditionalProperty("appNameLower", app)

				generateOne(index)
				log.info("Code generation for spec $appName completed")
			} catch (error: Throwable) {
				log.error("Failed generation for the app $appName", error)
			}
		}
	}

	private fun generateOne(index: Int = 0) {
		val entityOptInput: ClientOptInput = instance.toInClientOptInput()
		DataCodeGenerator(index)
				.opts(entityOptInput).generate()
	}
}
