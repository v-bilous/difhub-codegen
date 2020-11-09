package pro.bilous.codegen.process

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals

class PropertyTableNameTest {

	@Test
	fun `should return name from complex type`() {
		val processor = ModelPropertyProcessor(mock())
		processor.openApiWrapper = mock()

		val complexType = "UserAddress"
		whenever(processor.openApiWrapper.isOpenApiContainsType(complexType)).thenReturn(true)

		val property = CodegenProperty().apply {
			name = "address"
			isListContainer = false
		}

		val name = processor.readPropertyTableName(complexType, property)
		assertEquals("user_address", name)
	}

	@Test
	fun `should return name from property name`() {
		val processor = ModelPropertyProcessor(mock())
		processor.openApiWrapper = mock()

		val property = CodegenProperty().apply {
			name = "address"
			isListContainer = false
		}

		val name = processor.readPropertyTableName("UserAddress", property)
		assertEquals("address", name)
	}

	@Test
	fun `should return name from data type reference`() {
		val processor = ModelPropertyProcessor(mock())
		processor.openApiWrapper = mock()

		val property = CodegenProperty().apply {
			name = "parties"
			isListContainer = true
		}

		val name = processor.readPropertyTableName("Party", property)
		assertEquals("party", name)
	}
}
