package pro.bilous.codegen.process.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenProperty

internal class CommonModelsProcessorTest {

	@Test
	fun `should add common models to list`() {
		val properties = mutableMapOf<String, Any>()
		val model = CodegenModel().apply {
			name = "Entity"
			classname = name
			vars = listOf(
				CodegenProperty().apply {
					isModel = true
					complexType = "Binding"
				},
				CodegenProperty().apply {
					isModel = true
					complexType = "History"
				}
			)
		}
		CommonModelsProcessor(properties).process(model)

		assertTrue(properties.containsKey("commonModels"), "properties should have models set")
		val modelsList = properties["commonModels"] as Set<String>
		assertEquals(3, modelsList.size)
		assertTrue(modelsList.contains("Entity"), "List should contain Entity type")
		model.vars.forEach {
			assertTrue(modelsList.contains(it.complexType), "List should contain inner type")
		}
	}

	@Test
	fun `should add only Entity model to list`() {
		val properties = mutableMapOf<String, Any>()
		val model = CodegenModel().apply {
			name = "Entity"
			classname = name
		}
		CommonModelsProcessor(properties).process(model)

		assertTrue(properties.containsKey("commonModels"), "properties should have models set")
		val modelsList = properties["commonModels"] as Set<String>
		assertEquals(1, modelsList.size)
		assertEquals("Entity", modelsList.first(), "List should contain Entity type")
	}

	@Test
	fun `should ignore model`() {
		val properties = mutableMapOf<String, Any>()
		val model = CodegenModel().apply {
			name = "AnyModel"
			classname = name
		}
		CommonModelsProcessor(properties).process(model)
		assertFalse(properties.containsKey("commonModels"), "properties should have models set")
	}

	@Test
	fun `should add entity id var`() {
		val properties = mutableMapOf<String, Any>()
		val model = CodegenModel().apply {
			name = "ResourceEntity"
			classname = name
		}
		CommonModelsProcessor(properties).process(model)
		assertTrue(model.vendorExtensions["addEntityIdVar"] as Boolean, "should add field")
		assertFalse(model.vendorExtensions["isEmbeddable"] as Boolean, "should not be embedded")
		assertTrue(model.imports.contains("JsonIgnore"))
	}

	@Test
	fun `should make identity as embeddable`() {
		val properties = mutableMapOf<String, Any>()
		val model = CodegenModel().apply {
			name = "Identity"
			classname = name
		}
		CommonModelsProcessor(properties).process(model)
		assertTrue(model.vendorExtensions["isEmbeddable"] as Boolean, "should not be embedded")
		assertTrue(model.vendorExtensions["addIdentityId"] as Boolean, "should not be embedded")
	}

	@Test
	fun `should not add entity id var`() {
		val properties = mutableMapOf<String, Any>()
		val model = CodegenModel().apply {
			name = "Binding"
			classname = name
		}
		CommonModelsProcessor(properties).process(model)
		assertFalse(model.vendorExtensions.containsKey("addEntityIdVar"), "should not add field")
		assertFalse(model.imports.contains("JsonIgnore"))
	}
}
