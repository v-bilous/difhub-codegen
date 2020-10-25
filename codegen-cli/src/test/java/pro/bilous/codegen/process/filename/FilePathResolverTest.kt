package pro.bilous.codegen.process.filename

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FilePathResolverTest {

	@Test
	fun `should not change file path`() {
		val resolver = FilePathResolver()
		val originalPath = "/Users/bilous/client-system/app-folder/src/main/kotlin/app/client/user/domain/Binding.kt"
		val data = mapOf("appNameLower" to "user")
		val result = resolver.resolve(data, "app-data/entity.kt", originalPath)

		assertEquals(originalPath, result.targetPath)
		assertTrue(result.shouldWriteFile, "Should write file to app target")
	}

	@Test
	fun `should change file path to common one`() {
		val resolver = FilePathResolver()
		val originalPath = "/Users/bilous/client-system/app-user/src/main/kotlin/app/client/user/domain/Binding.kt"
		val data = mapOf(
			"appNameLower" to "user",
			"appPackage" to "app.client.user",
			"basePackage" to "app.client",
			"classname" to "History",
			"commonModels" to setOf("Entity", "History")
		)
		val result = resolver.resolve(data, "common/src/main/kotlin/domain/commonEntity.mustache", originalPath)
		assertEquals("/Users/bilous/client-system/common/src/main/kotlin/app/client/domain/Binding.kt", result.targetPath)
		assertTrue(result.shouldWriteFile, "Should write common file to common target")
	}

	@Test
	fun `should ignore file path change when exception but write file anyway`() {
		val resolver = FilePathResolver()
		val originalPath = "/Users/bilous/client-system/app-user/src/main/kotlin/app/client/user/domain/Binding.kt"
		val data = mapOf(
			"appNameLower" to "user",
			"classname" to "Entity",
			"commonModels" to setOf("Entity", "History")
		)
		val result = resolver.resolve(data, "common/src/main/kotlin/domain/commonEntity.mustache", originalPath)
		assertEquals(originalPath, result.targetPath)
		assertTrue(result.shouldWriteFile, "Should write common file to app target")
	}

	@Test
	fun `should ignore any common path except entity`() {
		val resolver = FilePathResolver()
		val originalPath = "/Users/bilous/client-system/app-user/src/main/kotlin/app/client/user/domain/Binding.kt"
		val data = mapOf("appNameLower" to "user")

		val result = resolver.resolve(data, "common/src/main/kotlin/commonModel.mustache", originalPath)
		assertEquals(originalPath, result.targetPath)
		assertTrue(result.shouldWriteFile, "Should write common file to app target")
	}

	@Test
	fun `should not write common file to app target`() {
		val resolver = FilePathResolver()
		val originalPath = "/Users/bilous/client-system/app-user/src/main/kotlin/app/client/user/domain/Binding.kt"
		val data = mapOf(
			"appNameLower" to "user",
			"appPackage" to "app.client.user",
			"basePackage" to "app.client",
			"classname" to "AnyModel",
			"commonModels" to setOf("Entity", "History")
		)

		val result = resolver.resolve(data, "app/src/main/kotlin/model.mustache", originalPath)
		assertEquals(originalPath, result.targetPath)
		assertTrue(result.shouldWriteFile, "Should not write common file to app target")
	}
}
