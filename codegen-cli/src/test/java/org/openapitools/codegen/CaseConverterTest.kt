package org.openapitools.codegen

import org.junit.jupiter.api.Test
import org.openapitools.codegen.utils.CamelCaseConverter
import kotlin.test.assertEquals

class CaseConverterTest {

	@Test
	fun converterTest1() {
		val result = CamelCaseConverter.convert("PLProductCategory")
		assertEquals("PL_PRODUCT_CATEGORY", result)
	}

	@Test
	fun converterTest2() {
		val result = CamelCaseConverter.convert("NMIDType")
		assertEquals("NMID_TYPE", result)
	}


	@Test
	fun converterTest4() {
		val result = CamelCaseConverter.convert("Dim_Affiliate")
		assertEquals("DIM_AFFILIATE", result)
	}

	@Test
	fun converterTest5() {
		val result = CamelCaseConverter.convert("VoidMIDSupportMode")
		assertEquals("VOID_MID_SUPPORT_MODE", result)
	}

	@Test
	fun converterTest6() {
		val result = CamelCaseConverter.convert("MCCIDtype")
		assertEquals("MCCI_DTYPE", result)	}
}
