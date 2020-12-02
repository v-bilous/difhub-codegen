package pro.bilous.codegen.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class SqlNamingUtilsTest {

	val namesList = listOf("use", "open", "drop", "create", "table", "rank", "system", "function", "range")
	val regularName = "regular_column_name"

	@Test
	fun shouldEscapeAllColumnNames() {
		val escapedNamesList = namesList.map{SqlNamingUtils.escapeColumnNameIfNeeded(it)}
		assertEquals(namesList.size, escapedNamesList.filter { it.endsWith('_') }.size)
	}

	@Test
	fun shouldEscapeAllTableNames() {
		val escapedNamesList = namesList.map{SqlNamingUtils.escapeTableNameIfNeeded(it)}
		assertEquals(namesList.size, escapedNamesList.filter { it.endsWith('_') }.size)
	}

	@Test
	fun shouldNotEscapeRegularColumnName() {
		val escapedName = SqlNamingUtils.escapeColumnNameIfNeeded(regularName)
		assertEquals(regularName, escapedName)
	}

	@Test
	fun shouldNotEscapeRegularTableName() {
		val escapedName = SqlNamingUtils.escapeTableNameIfNeeded(regularName)
		assertEquals(regularName, escapedName)
	}

}
