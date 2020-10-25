package pro.bilous.codegen.core

import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.config.DynamicSettings

interface ICustomConfigurator {

	fun getCustomSettings(): DynamicSettings?

	fun setCustomInputSpec(inputSpec: String?)

	fun setCustomArtifactId(artifactId: String)

	fun getSpecCopyValue(): String

	fun setCustomProperty(key: String, value: Any)

	fun toCustomClientOptInput(): ClientOptInput
}
