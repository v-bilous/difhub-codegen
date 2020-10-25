package pro.bilous.codegen.process

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
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
}
