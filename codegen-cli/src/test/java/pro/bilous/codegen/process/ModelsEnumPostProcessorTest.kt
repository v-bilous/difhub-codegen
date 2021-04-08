package pro.bilous.codegen.process

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.DefaultCodegen
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.assertTrue

internal class ModelsEnumPostProcessorTest {

	@Test
	fun `should mark metadata enums as alias`() {
		val allowableValuesMap = HashMap<String, Any>()
		allowableValuesMap["values"] = listOf("TEST_VALUE")
		val codegenModel = CodegenModel().apply {
			classname = "ClassName"
			isEnum = true
			allowableValues = allowableValuesMap
			imports = TreeSet()
			vendorExtensions = HashMap<String, Any>()
		}
		val models = mutableListOf<Any>(mapOf("model" to codegenModel))
		val imports = mutableListOf(mapOf("TEST" to "TEST"))

		val codegenImports = HashMap<String, String>()
		codegenImports["JsonValue"] = "JSON"
		val codegen = mock<CodeCodegen>()

		val importMapping = CodeCodegen::class.java.superclass.superclass.declaredFields.find { it.name == "importMapping" }
		importMapping?.isAccessible = true
		importMapping?.set(codegen as DefaultCodegen, codegenImports)

		val metadataEnums = CodeCodegen::class.java.declaredFields.find { it.name == "metadataEnums" }
		metadataEnums?.isAccessible = true
		metadataEnums?.set(codegen, HashMap<String, CodegenModel>())

		val additionalProperties = HashMap<String, Any>()
		additionalProperties["enumsType"] = "MetadataEnums"
		doReturn(additionalProperties).`when`(codegen).additionalProperties()

		val processor = ModelsEnumPostProcessor(codegen)

		val objs = mapOf("models" to models, "imports" to imports)
		processor.process(objs)

		assertTrue(codegenModel.isAlias)
	}


}
