package pro.bilous.codegen.process

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals

class OperationAddonImportTest {

	@Test
	fun `should add imports from test model`() {
		val gen: CodeCodegen = mock()
		val addon = OperationAddon(gen)

		whenever(gen.toModelImport("Person")).thenReturn("com.healthcare.register.domain.Person")

		val embeddedModel = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					classname = "Person"
					isString = true
					defaultValue = null
				}
			)
		}
		val testModel = CodegenModel().apply {
			vars = listOf(CodegenProperty().apply { vendorExtensions["embeddedComponent"] = embeddedModel })
		}

		val importList = mutableListOf(mapOf(
			"import" to "com.healthcare.register.domain.Patient",
			"classname" to "Patient"
		))
		val objs: MutableMap<String, Any> = mutableMapOf("imports" to importList)

		addon.applyImportsForTest(objs, testModel)
		val testImports = objs["testImports"] as List<Map<String, String>>

		assertEquals("com.healthcare.register.domain.Person", testImports[0]["import"])
		assertEquals("Person", testImports[0]["classname"])
	}

	@Test
	fun `should add imports with second level embed`() {
		val gen: CodeCodegen = mock()
		val addon = OperationAddon(gen)

		whenever(gen.toModelImport("Person")).thenReturn("com.healthcare.register.domain.Person")
		whenever(gen.toModelImport("UserInfo")).thenReturn("com.healthcare.register.domain.UserInfo")

		val embeddedModel = CodegenModel().apply {
			vars = listOf(
				CodegenProperty().apply {
					name = "name1"
					classname = "Person"
					isString = true
					defaultValue = null
					vendorExtensions["embeddedComponent"] = CodegenModel().apply {
						classname = "UserInfo"
					}
				},
			)
		}
		val testModel = CodegenModel().apply {
			vars = listOf(CodegenProperty().apply { vendorExtensions["embeddedComponent"] = embeddedModel })
		}

		val importList = mutableListOf(mapOf(
			"import" to "com.healthcare.register.domain.Patient",
			"classname" to "Patient"
		))
		val objs: MutableMap<String, Any> = mutableMapOf("imports" to importList)

		addon.applyImportsForTest(objs, testModel)
		val testImports = objs["testImports"] as List<Map<String, String>>

		assertEquals("com.healthcare.register.domain.Person", testImports[0]["import"])
		assertEquals("Person", testImports[0]["classname"])
		assertEquals("com.healthcare.register.domain.UserInfo", testImports[1]["import"])
		assertEquals("UserInfo", testImports[1]["classname"])
	}
}
