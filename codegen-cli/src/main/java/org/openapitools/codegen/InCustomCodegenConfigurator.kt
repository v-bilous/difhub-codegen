package org.openapitools.codegen

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.guava.GuavaModule
import io.swagger.v3.core.util.Json
import io.swagger.v3.core.util.Yaml
import org.apache.commons.io.FilenameUtils
import org.openapitools.codegen.config.CodegenConfigurator
import org.openapitools.codegen.config.DynamicSettings
import org.openapitools.codegen.config.GeneratorSettings
import org.openapitools.codegen.config.WorkflowSettings
import org.openapitools.codegen.utils.setValueToField
import java.io.File
import java.io.IOException

class InCustomCodegenConfigurator(val settings: DynamicSettings? = null) : CodegenConfigurator() {

	fun hasEntityMode(): Boolean {
		return true
	}

	var specCopy: String? = null

	fun toInClientOptInput(): ClientOptInput {
		setModelNameSuffix("")
		//setAdditionalProperties(mutableMapOf())
		return super.toClientOptInput()
	}

	override fun setInputSpec(inputSpec: String?): CodegenConfigurator {
		this.specCopy = inputSpec
		return super.setInputSpec(inputSpec)
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
                setValueToField(CodegenConfigurator::class, configurator, "generatorSettingsBuilder", GeneratorSettings.newBuilder(settings.generatorSettings))
                setValueToField(CodegenConfigurator::class, configurator, "workflowSettingsBuilder", WorkflowSettings.newBuilder(settings.workflowSettings))
				return configurator
			} catch (ex: IOException) {
				LOGGER.error("Unable to deserialize config file: $configFile", ex)
			}
			return null
		}


	}
}
