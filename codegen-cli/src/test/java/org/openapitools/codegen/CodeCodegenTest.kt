package org.openapitools.codegen

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CodeCodegenTest {

	@Test
	fun `test package creation`() {
		val gen = CodeCodegen().apply {
			artifactId = "user"
		}
		val testSrc = "app/client/user/repository"
		val result = gen.getTestFolder(testSrc, "")
		assertEquals("app-user/src/test/kotlin/app/client/user/controller", result)
	}
}
