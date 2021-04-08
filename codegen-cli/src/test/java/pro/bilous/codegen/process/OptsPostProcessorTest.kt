package pro.bilous.codegen.process

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class OptsPostProcessorTest {

	@Test
	fun `should add enum definitions files to supported files`() {
		val codegen: CodeCodegen = CodeCodegen().apply {
			artifactId = "test"
		}

		val openAPI = OpenAPI()
		openAPI.components = Components().apply { schemas = emptyMap() }
		codegen.setOpenAPI(openAPI)

		val additionalPropertiesField =
			CodeCodegen::class.java.superclass.superclass.declaredFields.find { it.name == "additionalProperties" }
		additionalPropertiesField?.isAccessible = true
		val additionalProperties = mapOf(CodeCodegen.BASE_PACKAGE to "Test", "enumsType" to "MetadataEnums")
		additionalPropertiesField?.set(codegen, additionalProperties)

		val optsPostProcessor = OptsPostProcessor(codegen)
		optsPostProcessor.processOpts()

		val enumValidationTemplateFile = codegen.supportingFiles()
			.find { it.templateFile == "common/src/main/kotlin//config/EnumValidationConfiguration.kt.mustache" }
		assertNotNull(enumValidationTemplateFile)
	}
}
