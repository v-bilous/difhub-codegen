package pro.bilous.codegen.configurator

object DatabaseResolver {
	fun getByType(type: String) : Database {
		return when(type) {
			"PostgreSQL" -> Database(
				"postgresql",
				"org.postgresql.Driver",
				"POSTGRESQL",
				5432,
				"org.postgresql:postgresql",
				"postgres:13.0"
			)
			else -> Database(
				"mysql",
				"com.mysql.cj.jdbc.Driver",
				"MYSQL",
				3306,
				"mysql:mysql-connector-java",
				"mysql:8.0.21"
			) //force use mysql
		}
	}
}
