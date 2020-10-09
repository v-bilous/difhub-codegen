package org.openapitools.codegen.processor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
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
		assertEquals("\${BOOLEAN_VALUE}", ve["columnType"])
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
		assertEquals("datetime", ve["columnType"])
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

	@Test
	fun `should assign UUID type from Guid`() {
		val processor = ModelPropertyProcessor(mock())

		val property = CodegenProperty()
		property.name = "MyUuidProperty"
		property.datatypeWithEnum = "integer"
		property.vendorExtensions["x-data-type"] = "Guid"
		processor.postProcessModelProperty(CodegenModel(), property)

		val ve = property.vendorExtensions
		assertEquals("\${UUID}", ve["columnType"])
		assertEquals("java.lang.String", ve["hibernateType"])
		assertEquals("my_uuid_property", ve["escapedColumnName"])
		assertEquals("my_uuid_property", ve["columnName"])
	}

	@Test
	fun `should assign JSON type`() {
		val codegen  = CodeCodegen()
		codegen.entityMode = true
		val processor = ModelPropertyProcessor(codegen)

		val property = CodegenProperty()
		property.name = "MyJsonProperty"
		property.datatypeWithEnum = "OtherType"
		property.isListContainer = true
		property.example = "null"
		val model = CodegenModel()
		model.name = "Name"

		processor.postProcessModelProperty(model, property)

		val ve = property.vendorExtensions
		assertEquals("\${JSON_OBJECT}", ve["columnType"])
		assertEquals("my_json_property", ve["escapedColumnName"])
		assertEquals("my_json_property", ve["columnName"])
	}

	@Test
	fun `should assign Int for object Unsigned Integer`() {
		testForObject("Unsigned Integer", "int")
	}

	@Test
	fun `should assign datetine for object Date`() {
		testForObject("Date", "datetime")
	}

	@Test
	fun `should assign varchar for object with other types`() {
		testForObject("OtherType", "VARCHAR(255)")
	}

	@Test
	fun `should escape column name`() {
		val processor = ModelPropertyProcessor(mock())

		val property = CodegenProperty()
		property.name = "table"
		property.datatypeWithEnum = "Integer"
		processor.postProcessModelProperty(CodegenModel(), property)

		val ve = property.vendorExtensions
		assertEquals("table_", ve["columnName"])
		assertEquals("table_", ve["escapedColumnName"])
	}

	private fun testForObject(dataType: String, expectedColumnType: String) {
		val processor = ModelPropertyProcessor(mock())

		val property = CodegenProperty()
		property.name = "ObjectProp"
		property.datatypeWithEnum = "Object"
		property.vendorExtensions["x-data-type"] = dataType
		processor.postProcessModelProperty(CodegenModel(), property)

		val ve = property.vendorExtensions
		assertEquals(expectedColumnType, ve["columnType"])
	}
}
