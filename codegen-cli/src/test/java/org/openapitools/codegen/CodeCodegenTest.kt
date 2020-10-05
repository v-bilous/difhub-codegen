package org.openapitools.codegen

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
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

	@Test
	fun `fromOperation check redundant imports not present`() {
		val gen = CodeCodegen()
		val operation = Operation().apply {
			operationId = "opId"
			responses = ApiResponses().addApiResponse(
				"200", ApiResponse()
			)
		}
		val result = gen.fromOperation(path = "", httpMethod = "", operation = operation, servers = null)

		assertNotNull(result)
		assertFalse(result.imports.contains("List"))
		assertFalse(result.imports.contains("Error"))
	}
}
