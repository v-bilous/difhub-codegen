package pro.bilous.codegen.process.models

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommonModelsProcessorImportsTest {

	@Test
	fun `should return true for import resolve`() {
		val properties = mutableMapOf<String, Any>()
		val processor = CommonModelsProcessor(properties)
		assertTrue(processor.canResolveImport("Entity"))
	}

	@Test
	fun `should return false import resolve`() {
		val properties = mutableMapOf<String, Any>()
		val processor = CommonModelsProcessor(properties)
		assertFalse(processor.canResolveImport("AnyModel"))
	}

	@Test
	fun `should return true to custom common import resolve`() {
		val properties = mutableMapOf<String, Any>()
		properties["commonModels"] = setOf("AnySomeModel")
		val processor = CommonModelsProcessor(properties)
		assertTrue(processor.canResolveImport("AnySomeModel"))
	}

	@Test
	fun `should return false to custom common import resolve`() {
		val properties = mutableMapOf<String, Any>()
		properties["commonModels"] = setOf("AnySomeModel")
		val processor = CommonModelsProcessor(properties)
		assertFalse(processor.canResolveImport("AnyModel"))
	}

	@Test
	fun `should resolve import for common`() {
		val properties = mutableMapOf<String, Any>()
		properties["basePackage"] = "app.client"
		val processor = CommonModelsProcessor(properties)
		assertEquals("app.client.domain.Binding", processor.resolveImport("Binding"))
	}
}
