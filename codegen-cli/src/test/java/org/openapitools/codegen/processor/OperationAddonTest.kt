package org.openapitools.codegen.processor

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.CodegenParameter
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
}
