package pro.bilous.codegen.process.models

import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ModelStrategyResolverTest {

	@Test
	fun `should build args with all false`() {
		val model = CodegenModel()
		val resolver = ModelStrategyResolver(model)
		val args = resolver.buildArgs()

		assertFalse(args.hasEntity)
		assertFalse(args.hasId)
		assertFalse(args.hasExtends)
		assertFalse(args.hasIdentity)
	}

	@Test
	fun `should build args with hasIdentity && hasExtends = true`() {
		val model = CodegenModel().apply {
			name = "Account"
			vars = listOf(
				CodegenProperty().apply { name = "identity" },
				CodegenProperty().apply { name = "entity" }
			)
		}
		val resolver = ModelStrategyResolver(model)
		val args = resolver.buildArgs()

		assertTrue(args.hasIdentity)
		assertTrue(args.hasEntity)
		assertFalse(args.hasId)
		assertFalse(args.hasExtends)
	}

	@Test
	fun `should build args with hasExtends = true`() {
		val model = CodegenModel().apply {
			name = "Account"
			requiredVars = listOf(CodegenProperty().apply {
				name = "_extends"
			})
		}
		val resolver = ModelStrategyResolver(model)
		val args = resolver.buildArgs()

		assertFalse(args.hasIdentity)
		assertFalse(args.hasEntity)
		assertFalse(args.hasId)
		assertTrue(args.hasExtends)
	}


	@Test
	fun `should build args with hasId = true`() {
		val model = CodegenModel().apply {
			name = "Account"
			vars = listOf(CodegenProperty().apply {
				name = "id"
			})
		}
		val resolver = ModelStrategyResolver(model)
		val args = resolver.buildArgs()

		assertFalse(args.hasIdentity)
		assertFalse(args.hasEntity)
		assertTrue(args.hasId)
		assertFalse(args.hasExtends)
	}

	@Test
	fun `should cleanup imports`() {
		val model = CodegenModel().apply {
			imports = setOf("ApiModel", "JsonProperty", "Account")
		}
		val resolver = ModelStrategyResolver(model)
		resolver.cleanupImports()
		assertEquals(1, model.imports.size)
		assertEquals("Account", model.imports.first())
	}

	@Test
	fun `should add extensions to model when all agrs false`() {
		val model = CodegenModel().apply {
			name = "AccountParty"
		}
		val resolver = ModelStrategyResolver(model)
		val testArgs = ModelStrategyResolver.Args()
		resolver.addExtensions(testArgs)

		assertFalse(model.vendorExtensions.containsKey("hasTableEntity"))
		assertEquals("account_party", model.vendorExtensions["tableName"])
		assertTrue(model.vendorExtensions["isEmbeddable"] as Boolean)
		assertFalse(model.vendorExtensions["addIdVar"] as Boolean)
		assertFalse(model.vendorExtensions["isSuperclass"] as Boolean)
		assertFalse(model.vendorExtensions["hasIdentity"] as Boolean)
		assertFalse(model.vendorExtensions["hasEntity"] as Boolean)
	}

	@Test
	fun `should add extensions to model when identity & entity args`() {
		val model = CodegenModel().apply {
			name = "BaseResource"
		}
		val resolver = ModelStrategyResolver(model)
		val testArgs = ModelStrategyResolver.Args(hasIdentity = true, hasEntity = true)
		resolver.addExtensions(testArgs)

		assertFalse(model.vendorExtensions.containsKey("hasTableEntity"))
		assertFalse(model.vendorExtensions["isEmbeddable"] as Boolean)
		assertTrue(model.vendorExtensions["isSuperclass"] as Boolean)
		assertTrue(model.vendorExtensions["hasIdentity"] as Boolean)
		assertTrue(model.vendorExtensions["hasEntity"] as Boolean)
	}

	@Test
	fun `should add parent for model with Entity`() {
		val model = CodegenModel().apply {
			name = "Account"
		}
		val resolver = ModelStrategyResolver(model)
		val testArgs = ModelStrategyResolver.Args(hasEntity = true)
		resolver.resolveParent(testArgs)

		assertEquals("BaseResource()", model.parent)
		assertTrue(model.imports.contains("BaseResource"))
		assertTrue(model.vendorExtensions["hasTableEntity"] as Boolean)
	}

	@Test
	fun `should add parent for model with Id`() {
		val model = CodegenModel().apply {
			name = "Account"
		}
		val resolver = ModelStrategyResolver(model)
		val testArgs = ModelStrategyResolver.Args(hasId = true)
		resolver.resolveParent(testArgs)

		assertEquals("BaseDomain()", model.parent)
		assertTrue(model.imports.contains("BaseDomain"))
		assertFalse(model.vendorExtensions.containsKey("hasTableEntity"))
	}

	@Test
	fun `should add parent for model with Identity`() {
		val model = CodegenModel().apply {
			name = "Account"
		}
		val resolver = ModelStrategyResolver(model)
		val testArgs = ModelStrategyResolver.Args(hasIdentity = true)
		resolver.resolveParent(testArgs)

		assertEquals("BaseDomain()", model.parent)
		assertTrue(model.imports.contains("BaseDomain"))
		assertFalse(model.vendorExtensions.containsKey("hasTableEntity"))
	}

	@Test
	fun `should add parent for model with extends`() {
		val model = CodegenModel().apply {
			name = "Account"
			requiredVars = listOf(CodegenProperty().apply {
				name = "_extends"
				complexType = "BaseResource"
			})
		}
		val resolver = ModelStrategyResolver(model)
		val testArgs = ModelStrategyResolver.Args(hasExtends = true)
		resolver.resolveParent(testArgs)

		assertEquals("BaseResource()", model.parent)
		assertTrue(model.imports.contains("BaseResource"))
		assertFalse(model.vendorExtensions.containsKey("hasTableEntity"))
	}
}
