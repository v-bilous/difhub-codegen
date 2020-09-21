package org.openapitools.codegen.processor

import org.junit.jupiter.api.Test
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenProperty
import kotlin.test.assertEquals

class ModelPropertyProcessorTest {

	@Test
	fun readTypeFromFormat() {
		val property = CodegenProperty().apply {
			complexType = null
			dataFormat = "system: Security | application: Auth | dataType: UserGroup"
		}
		val result = ModelPropertyProcessor(CodeCodegen()).readTypeFromFormat(property)
		assertEquals("UserGroup", result)
	}

	@Test
	fun joinTableName() {
		val result = ModelPropertyProcessor(CodeCodegen()).joinTableName("user", "user_group")
		assertEquals("user_to_user_group", result)
	}

	@Test
	fun joinTableNameInverted() {
		val result = ModelPropertyProcessor(CodeCodegen()).joinTableName("user_group", "user")
		assertEquals("user_to_user_group", result)
	}
}
