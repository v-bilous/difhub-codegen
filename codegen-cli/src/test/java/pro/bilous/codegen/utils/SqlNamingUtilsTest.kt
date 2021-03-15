package pro.bilous.codegen.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class SqlNamingUtilsTest {

	val namesList = listOf("use", "open", "drop", "create", "table", "rank", "system", "function", "range", "condition")
	val regularName = "regular_column_name"

	@Test
	fun shouldEscapeAllColumnNames() {
		val escapedNamesList = namesList.map{SqlNamingUtils.escapeSqlNameIfNeeded(it)}
		assertEquals(namesList.size, escapedNamesList.filter { it.endsWith('_') }.size)
	}

	@Test
	fun shouldNotEscapeRegularColumnName() {
		val escapedName = SqlNamingUtils.escapeSqlNameIfNeeded(regularName)
		assertEquals(regularName, escapedName)
	}
}
