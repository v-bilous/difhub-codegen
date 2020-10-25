package pro.bilous.codegen.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.guava.GuavaModule
import io.swagger.v3.core.util.Json
import io.swagger.v3.core.util.Yaml
import org.apache.commons.io.FilenameUtils
import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.config.CodegenConfigurator
import org.openapitools.codegen.config.DynamicSettings
import org.openapitools.codegen.config.GeneratorSettings
import org.openapitools.codegen.config.WorkflowSettings
import pro.bilous.codegen.utils.setValueToField
import java.io.File
import java.io.IOException

class InCustomCodegenConfigurator(val settings: DynamicSettings? = null) : CodegenConfigurator(),
	ICustomConfigurator {

	fun hasEntityMode(): Boolean {
		return true
	}

	var specCopy: String? = null

	private fun toInClientOptInput(): ClientOptInput {
		setModelNameSuffix("")
		//setAdditionalProperties(mutableMapOf())
		return super.toClientOptInput()
	}

	override fun setInputSpec(inputSpec: String?): CodegenConfigurator {
		this.specCopy = inputSpec
		return super.setInputSpec(inputSpec)
	}

	override fun getCustomSettings(): DynamicSettings? {
		return settings
	}

	override fun setCustomInputSpec(inputSpec: String?) {
		setInputSpec(inputSpec)
	}

	override fun setCustomArtifactId(artifactId: String) {
		setArtifactId(artifactId)
	}

	override fun getSpecCopyValue(): String {
		return specCopy!!
	}

	override fun setCustomProperty(key: String, value: Any) {
		addAdditionalProperty(key, value)
	}

	override fun toCustomClientOptInput(): ClientOptInput {
		return toInClientOptInput()
	}

	companion object {

		fun fromFile(configFile: String): InCustomCodegenConfigurator? {
			if (configFile.isEmpty()) {
				return null
			}

			val mapper: ObjectMapper = if (FilenameUtils.isExtension(configFile, arrayOf("yml", "yaml"))) {
                Yaml.mapper()
			} else {
                Json.mapper()
			}
			mapper.registerModule(GuavaModule())

			try {
				val settings = mapper.readValue(File(configFile), DynamicSettings::class.java)
				val configurator = InCustomCodegenConfigurator(settings)
				setValueToField(
					CodegenConfigurator::class,
					configurator,
					"generatorSettingsBuilder",
					GeneratorSettings.newBuilder(settings.generatorSettings)
				)
				setValueToField(
					CodegenConfigurator::class,
					configurator,
					"workflowSettingsBuilder",
					WorkflowSettings.newBuilder(settings.workflowSettings)
				)
				return configurator
			} catch (ex: IOException) {
				LOGGER.error("Unable to deserialize config file: $configFile", ex)
			}
			return null
		}


	}
}
