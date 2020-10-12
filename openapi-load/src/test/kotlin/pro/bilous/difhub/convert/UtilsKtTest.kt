package pro.bilous.difhub.convert

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class UtilsKtTest {

	@Test
	fun `should capitalize`() {
		val source = "emailAddresses"
		assertEquals("EmailAddresses", normalizeTypeName(source))
	}

	@Test
	fun `should keep as is`() {
		val source = "EmailAddresses"
		assertEquals("EmailAddresses", normalizeTypeName(source))
	}

	@Test
	fun `should trim end and start and capitalize`() {
		val source = "  emailAddresses   "
		assertEquals("EmailAddresses", normalizeTypeName(source))
	}

	@Test
	fun `should remove whitespace`() {
		val source = "email Addresses"
		assertEquals("EmailAddresses", normalizeTypeName(source))
	}
}
