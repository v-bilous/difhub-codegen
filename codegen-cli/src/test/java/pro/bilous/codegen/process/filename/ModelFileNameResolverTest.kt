package pro.bilous.codegen.process.filename

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenModel

internal class ModelFileNameResolverTest {

	@Test
	fun `should return the same name`() {
		val resolver = ModelFileNameResolver(ModelFileNameArgs(mapOf(), ""))
		val result = resolver.resolve("ModelName")

		assertEquals("ModelName", result)
	}

	@Test
	fun `should remove suffix from name`() {
		val resolver = ModelFileNameResolver(ModelFileNameArgs(mapOf(
			"ModelNameModel" to CodegenModel()
		), "Model"))
		val result = resolver.resolve("ModelNameModel")

		assertEquals("ModelName", result)
	}

	@Test
	fun `should replace Entity name`() {
		val resolver = ModelFileNameResolver(ModelFileNameArgs(mapOf(), "Model"))
		val result = resolver.resolve("Entity")

		assertEquals("ResourceEntity", result)
	}
}
