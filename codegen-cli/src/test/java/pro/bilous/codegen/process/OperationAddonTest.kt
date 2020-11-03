package pro.bilous.codegen.process

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.CodegenParameter
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class OperationAddonTest {

	@Test
	fun `check fixOperationParams pageable added`() {
		val addon = OperationAddon(mock())

		val op = CodegenOperation().apply {
			allParams = mutableListOf(CodegenParameter())
			httpMethod = "get"
			isListContainer = true
		}
		addon.fixOperationParams(op)

		assertNotNull(op.allParams.any { it.paramName == "page" })
	}

	@Test
	fun `should add default string values to model vars`() {
		val addon = OperationAddon(mock())
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					isString = true
					defaultValue = null
				},
				CodegenProperty().apply {
					name = "name2"
					isModel = true
					isString = false
					datatypeWithEnum = "String?"
				}
			)
		}
		addon.applyTestVars(model)
		assertEquals("test string value", model.vars[0].defaultValue)
		assertEquals("test_enum_value", model.vars[1].defaultValue)
		assertEquals(true, model.vars[1].isString)
	}

	@Test
	fun `should keep default value in var`() {
		val addon = OperationAddon(mock())
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					isString = true
					defaultValue = "this is default"
				}
			)
		}
		addon.applyTestVars(model)
		assertEquals("this is default", model.vars[0].defaultValue)
	}

	@Test
	fun `should add default value for integer`() {
		val addon = OperationAddon(mock())
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					isInteger = true
					defaultValue = null
				}
			)
		}
		addon.applyTestVars(model)
		assertEquals("8", model.vars[0].defaultValue)
	}

	@Test
	fun `should add default value for date`() {
		val addon = OperationAddon(mock())
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					datatype = "Date"
					defaultValue = null
				}
			)
		}
		addon.applyTestVars(model)
		assertEquals("Date()", model.vars[0].defaultValue)
	}

	@Test
	fun `should add default value for boolean`() {
		val addon = OperationAddon(mock())
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					isBoolean = true
					defaultValue = null
				}
			)
		}
		addon.applyTestVars(model)
		assertEquals("false", model.vars[0].defaultValue)
	}

	@Test
	fun `should add default value for guid`() {
		val addon = OperationAddon(mock())
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					vendorExtensions["x-data-type"] = "Guid"
					defaultValue = null
				}
			)
		}
		addon.applyTestVars(model)
		assertEquals(36, model.vars[0].defaultValue.length)
	}

	@Test
	fun `should apply null value for var`() {
		val addon = OperationAddon(mock())
		val model = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					isContainer = true
					defaultValue = null
				}
			)
		}
		addon.applyTestVars(model)
		assertEquals("null", model.vars[0].defaultValue)
	}

	@Test
	fun `should apply test to embedded value`() {
		val addon = OperationAddon(mock())
		val embeddedModel = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					isString = true
					defaultValue = null
				}
			)
		}
		val model = CodegenModel().apply {
			vars = listOf(CodegenProperty().apply { vendorExtensions["embeddedComponent"] = embeddedModel })
		}
		addon.applyTestVars(model)
		assertEquals("test string value", embeddedModel.vars[0].defaultValue)
	}
}
