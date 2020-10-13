package org.openapitools.codegen.cmd

object DatabaseResolver {
	fun getByType(type: String) : Database {
		return when(type) {
			"PostgreSQL" -> Database("postgresql", "org.postgresql.Driver", "POSTGRESQL", 5432,"org.postgresql:postgresql")
			else -> Database("mysql", "com.mysql.cj.jdbc.Driver", "MYSQL",3306, "mysql:mysql-connector-java") //force use mysql
		}
	}
}