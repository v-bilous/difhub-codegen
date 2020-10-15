package pro.bilous.difhub.convert

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TypesConverterTest {

	@Test
	fun `should return type for Guid`() {
		val result = TypesConverter.convert("Guid")

		assertEquals("string", result.type)
		assertEquals("uuid", result.format)
	}

	@Test
	fun `should return type for Long`() {
		val result = TypesConverter.convert("Long")

		assertEquals("integer", result.type)
		assertEquals("int64", result.format)
	}

	@Test
	fun `should return type for generic`() {
		val result = TypesConverter.convert("AnyModelType")

		assertEquals("object", result.type)
		assertNull(result.format)
	}
}
