package pro.bilous.codegen.process

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelPropertyProcessorEnumTest {

	@Test
	fun `should not detect enum property`() {
		val processor = ModelPropertyProcessor(mock())
		val property = CodegenProperty()
		assertFalse(processor.isEnum(property))
	}

	@Test
	fun `should detect enum property`() {
		val processor = ModelPropertyProcessor(mock())
		val property = CodegenProperty().apply {
			allowableValues = mapOf("values" to listOf("value", "value1"))
		}
		assertTrue(processor.isEnum(property))
	}

	@Test
	fun `should detect enum property in list`() {
		val processor = ModelPropertyProcessor(mock())
		val property = CodegenProperty().apply {
			isListContainer = true
			items = CodegenProperty().apply {
				allowableValues = mapOf("values" to listOf("value", "value1"))
			}
		}
		assertTrue(processor.isEnum(property))
	}

	@Test
	fun `should not detect enum property in list`() {
		val processor = ModelPropertyProcessor(mock())
		val property = CodegenProperty().apply {
			isListContainer = true
			items = null
		}
		assertFalse(processor.isEnum(property))
	}

	@Test
	fun `should convert enum to string`() {
		val processor = ModelPropertyProcessor(mock())
		val property = CodegenProperty().apply {
			complexType = "TestType"
		}
		val model = CodegenModel()
		processor.convertToMetadataProperty(property, model)

		assertEquals("String?", property.datatypeWithEnum)
	}

	@Test
	fun `should convert enum to list of string`() {
		val processor = ModelPropertyProcessor(mock())
		val property = CodegenProperty().apply {
			isListContainer = true
			required = true
			complexType = "TestType"
		}
		val model = CodegenModel()
		processor.convertToMetadataProperty(property, model)

		assertEquals("List<String>", property.datatypeWithEnum)
	}

	@Test
	fun `should detect property as simple enum`() {
		val codegen: CodeCodegen = mock()
		val propertyMap = HashMap<String, Any>()
		propertyMap["enumsType"] = "SimpleEnums"
		whenever(codegen.additionalProperties()).thenReturn(propertyMap)
		val processor = ModelPropertyProcessor(codegen)
		val property = CodegenProperty().apply {
			name = "TestName"
			isListContainer = false
			required = true
			complexType = "TestType"
			allowableValues = mapOf("values" to listOf("TestValues"))
		}
		val model = CodegenModel()
		processor.postProcessModelProperty(model, property)

		assertEquals("TestType", property.datatypeWithEnum)
	}

	@Test
	fun `should detect property as metadata enum`() {
		val codegen: CodeCodegen = mock()
		val propertyMap = HashMap<String, Any>()
		propertyMap["enumsType"] = "MetadataEnums"
		whenever(codegen.additionalProperties()).thenReturn(propertyMap)
		val processor = ModelPropertyProcessor(codegen)
		val property = CodegenProperty().apply {
			name = "TestName"
			isListContainer = false
			required = true
			complexType = "TestType"
			allowableValues = mapOf("values" to listOf("TestValues"))
		}
		val model = CodegenModel()
		processor.postProcessModelProperty(model, property)

		assertEquals("String", property.datatypeWithEnum)
	}
}
