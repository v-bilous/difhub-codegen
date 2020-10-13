package org.openapitools.codegen.cmd

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DatabaseResolverTest {

	@Test
	fun `should return postgres`() {
		val db = DatabaseResolver.getByType("PostgreSQL")

		assertEquals("postgresql", db.name)
		assertEquals("org.postgresql.Driver", db.driver)
		assertEquals("org.postgresql:postgresql", db.dependency)
	}

	@Test
	fun `should return mysql`() {
		val db = DatabaseResolver.getByType("")

		assertEquals("mysql", db.name)
		assertEquals("com.mysql.cj.jdbc.Driver", db.driver)
		assertEquals("mysql:mysql-connector-java", db.dependency)
	}
}
