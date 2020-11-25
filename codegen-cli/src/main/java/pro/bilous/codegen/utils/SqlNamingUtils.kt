package pro.bilous.codegen.utils

object SqlNamingUtils {

	private val columnNamesToEscape = arrayOf("use", "open", "drop", "create", "table", "rank", "system", "function", "range")
	fun escapeColumnNameIfNeeded(columnName: String): String {
		return if (columnNamesToEscape.contains(columnName)) "${columnName}_" else columnName
	}

	private val tableNamesToEscape = arrayOf("use", "open", "drop", "create", "table", "rank", "system", "function", "range")
	fun escapeTableNameIfNeeded(tableName: String): String {
		return if (columnNamesToEscape.contains(tableName)) "${tableName}_" else tableName
	}

}
