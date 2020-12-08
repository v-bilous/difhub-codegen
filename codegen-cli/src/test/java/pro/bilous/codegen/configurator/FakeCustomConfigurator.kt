package pro.bilous.codegen.configurator

import io.swagger.v3.oas.models.OpenAPI
import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.config.DynamicSettings
import pro.bilous.codegen.core.ICustomConfigurator

open class FakeCustomConfigurator : ICustomConfigurator {

	var settings: DynamicSettings? = null
	var artifactId: String? = null

	val optInputs = mutableMapOf<String, ClientOptInput>()

	override fun getCustomSettings(): DynamicSettings? {
		return settings
	}

	override fun setCustomInputSpec(inputSpec: String?) {}

	override fun setCustomArtifactId(artifactId: String) {
		this.artifactId = artifactId
	}

	override fun getSpecCopyValue(): String {
		return "/path/to/specs/"
	}

	override fun setCustomProperty(key: String, value: Any) {}

	override fun toCustomClientOptInput(): ClientOptInput {
		if (optInputs.containsKey(artifactId)) {
			return optInputs[artifactId]!!
		}
		return ClientOptInput()
	}
}
