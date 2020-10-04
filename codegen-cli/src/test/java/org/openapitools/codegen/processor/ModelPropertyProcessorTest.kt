package org.openapitools.codegen.processor

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ModelPropertyProcessorTest {

	@Test
	fun readTypeFromFormat() {
		val property = CodegenProperty().apply {
			complexType = null
			dataFormat = "system: Security | application: Auth | dataType: UserGroup"
		}
		val result = ModelPropertyProcessor(CodeCodegen()).readTypeFromFormat(property)
		assertEquals("UserGroup", result)
	}

	@Test
	fun joinTableName() {
		val result = ModelPropertyProcessor(CodeCodegen()).joinTableName("user", "user_group")
		assertEquals("user_to_user_group", result)
	}

	@Test
	fun joinTableNameInverted() {
		val result = ModelPropertyProcessor(CodeCodegen()).joinTableName("user_group", "user")
		assertEquals("user_to_user_group", result)
	}

	@Test
	fun `should assign boolean type`() {
		val processor = ModelPropertyProcessor(mock())

		val property = CodegenProperty()
		property.datatypeWithEnum = "Boolean"
		property.isBoolean = false
		processor.resolvePropertyType(property)

		assertTrue(property.isBoolean)
		val ve = property.vendorExtensions
		assertEquals("TINYINT(1)", ve["columnType"])
		assertEquals("java.lang.Boolean", ve["hibernateType"])
	}

	@Test
	fun `should assign date type`() {
		val processor = ModelPropertyProcessor(mock())

		val property = CodegenProperty()
		property.datatypeWithEnum = "Date?"
		property.isDate = false
		processor.resolvePropertyType(property)

		assertTrue(property.isDate)
		val ve = property.vendorExtensions
		assertEquals("datetime(6)", ve["columnType"])
		assertEquals("java.util.Date", ve["hibernateType"])
	}

	@Test
	fun `should assign int type`() {
		val processor = ModelPropertyProcessor(mock())

		val property = CodegenProperty()
		property.datatypeWithEnum = "Int"
		property.isInteger = false
		processor.resolvePropertyType(property)

		assertTrue(property.isInteger)
		val ve = property.vendorExtensions
		assertEquals("int", ve["columnType"])
		assertNull(ve["hibernateType"])
	}

	@Test
	fun `should assign db fallback other type`() {
		val processor = ModelPropertyProcessor(mock())

		val property = CodegenProperty()
		property.datatypeWithEnum = "OtherType"
		property.isString = false
		processor.resolvePropertyType(property)

		assertFalse(property.isString) // for other type we don't see isString=true
		val ve = property.vendorExtensions
		assertEquals("VARCHAR(255)", ve["columnType"])
		assertEquals("java.lang.String", ve["hibernateType"])
	}
}
