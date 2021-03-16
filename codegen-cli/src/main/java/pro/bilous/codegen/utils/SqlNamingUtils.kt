package pro.bilous.codegen.utils

object SqlNamingUtils {

	private val sqlReservedWordsToEscape = arrayOf("use", "open", "drop", "create", "table", "rank", "system", "function",
		"range", "from", "order", "condition", "limit", "procedure", "check", "index", "column", "database")

	fun escapeSqlNameIfNeeded(tableName: String): String {
		return if (sqlReservedWordsToEscape.contains(tableName)) "${tableName}_" else tableName
	}

}
