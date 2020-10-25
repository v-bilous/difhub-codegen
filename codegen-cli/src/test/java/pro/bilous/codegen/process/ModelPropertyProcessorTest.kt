package pro.bilous.codegen.process

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.swagger.v3.oas.models.media.Schema
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
		val result = createModelProcessor().readTypeFromFormat(property)
		assertEquals("UserGroup", result)
	}

	@Test
	fun joinTableName() {
		val result = createModelProcessor().joinTableName("user", "user_group")
		assertEquals("user_to_user_group", result)
	}

	@Test
	fun joinTableNameInverted() {
		val result = createModelProcessor().joinTableName("user_group", "user")
		assertEquals("user_to_user_group", result)
	}

	@Test
	fun `should assign boolean type`() {
		val processor = createModelProcessor()

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
		val processor = createModelProcessor()

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
		val processor = createModelProcessor()

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
	fun `should assign decimal type`() {
		val processor = createModelProcessor()

		val property = CodegenProperty()
		property.datatypeWithEnum = "BigDecimal?"
		property.isNumber = false
		processor.resolvePropertyType(property)

		val ve = property.vendorExtensions
		assertEquals("decimal", ve["columnType"])
		assertTrue(property.isNumber)
	}

	@Test
	fun `should assign db fallback other type`() {
		val processor = createModelProcessor()

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

	@Test
	fun `should assign embedded component`() {
		val type = "AnComplexEntity"
		val model = CodegenModel()
		val property = CodegenProperty().apply {
			isModel = true// only model can be embedded
			complexType = type // with required complex type
		}
		val processor = createModelProcessor(type, true)
		processor.applyEmbeddedComponentOrOneToOne(model, property)

		assertTrue(property.vendorExtensions["isEmbedded"] as Boolean)
		assertFalse(property.vendorExtensions.containsKey("isOneToOne"))
	}

	@Test
	fun `should assign one-to-one relationship`() {
		val type = "AnComplexEntity"

		val model = CodegenModel()
		val property = CodegenProperty().apply {
			isModel = true// only model can be embedded
			complexType = type // with required complex type
		}
		val processor = createModelProcessor(type, false)
		processor.applyEmbeddedComponentOrOneToOne(model, property)

		assertTrue(property.vendorExtensions["isOneToOne"] as Boolean)
		assertFalse(property.vendorExtensions.containsKey("isEmbedded"))
	}

	@Test
	fun `should ignore embedding and one-to-one`() {
		val model = CodegenModel()
		val property = CodegenProperty().apply {
			isModel = false// only model can be embedded
		}
		val processor = createModelProcessor("String", false)
		processor.applyEmbeddedComponentOrOneToOne(model, property)

		assertFalse(property.vendorExtensions.containsKey("isOneToOne"))
		assertFalse(property.vendorExtensions.containsKey("isEmbedded"))
	}

	@Test
	fun `should add embedded column names`() {
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "start"
					vendorExtensions["columnName"] = "start"
				}
			)
		}
		val property = CodegenProperty().apply {
			name = "header"
			vendorExtensions["columnName"] = "header"
		}

		val processor = createModelProcessor("String", false)
		processor.assignEmbeddedModel(property, model, false)

		assertTrue(property.vendorExtensions["isEmbedded"] as Boolean)
		assertEquals(model, property.vendorExtensions["embeddedComponent"])
		assertEquals("header_start", model.vars.first().vendorExtensions["embeddedColumnName"])
		assertEquals("header.start", model.vars.first().vendorExtensions["embeddedVarName"])

	}

	@Test
	fun `should not add embedded column names if history property`() {
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "createdAt"
					vendorExtensions["columnName"] = "created_at"
				}
			)
		}
		val property = CodegenProperty().apply {
			name = "history"
			vendorExtensions["columnName"] = "history"
		}

		val processor = createModelProcessor("String", false)
		processor.assignEmbeddedModel(property, model, false)

		assertTrue(property.vendorExtensions["isEmbedded"] as Boolean)
		assertEquals(model, property.vendorExtensions["embeddedComponent"])
		assertEquals("created_at", model.vars.first().vendorExtensions["embeddedColumnName"])
		assertEquals("history.createdAt", model.vars.first().vendorExtensions["embeddedVarName"])

	}

	@Test
	fun `should add embedded column names with second level embed`() {
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "start"
					vendorExtensions["columnName"] = "start"
				}
			)
		}
		val property = CodegenProperty().apply {
			name = "parent"
			vendorExtensions["columnName"] = "parent"
			vendorExtensions["embeddedColumnName"] = "header_parent"
			vendorExtensions["embeddedVarName"] = "header.parent"
		}

		val processor = createModelProcessor("String", false)
		processor.assignEmbeddedModel(property, model, false)

		assertTrue(property.vendorExtensions["isEmbedded"] as Boolean)
		assertEquals(model, property.vendorExtensions["embeddedComponent"])
		assertEquals("header_parent_start", model.vars.first().vendorExtensions["embeddedColumnName"])
		assertEquals("header.parent.start", model.vars.first().vendorExtensions["embeddedVarName"])
	}

	private fun createModelProcessor(type: String = "String", isEmbeddable: Boolean = false): ModelPropertyProcessor {
		val fakeSchema = Schema<Any>()
		val fakeInnerModel = CodegenModel().apply {
			// isEmbeddable will false because id field will be present in the model body
			vendorExtensions["isEmbeddable"] = isEmbeddable
		}
		val codegen = mock<CodeCodegen>()
		whenever(codegen.findOpenApi()).thenReturn(mock())
		whenever(codegen.fromModel(type, fakeSchema)).thenReturn(fakeInnerModel)

		return ModelPropertyProcessor(codegen).apply {
			openApiWrapper = object : IOpenApiWrapper {
				override fun isOpenApiContainsType(complexType: String?) = type == complexType
				override fun findSchema(complexType: String?) = fakeSchema
			}
		}
	}
}
