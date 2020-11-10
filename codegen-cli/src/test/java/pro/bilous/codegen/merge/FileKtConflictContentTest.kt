package pro.bilous.codegen.merge

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FileKtConflictContentTest {

	@Test
	fun `should pass test and merge content`() {
		val fileMerge = FileMerge()
		val fakeFilePath = "/path/to/file/UserApplication.kt"
		val result = fileMerge.mergeFileContent(testKtExisting, testKtNew, fakeFilePath)

		assertEquals(expectedKtResult, result)
	}

	private val testKtExisting = """
	package app.client.party

	//codegen:merge
	import app.client.config.KeycloakConfig
	import app.client.config.WebConfig
	import app.client.config.SpringFoxConfig
	import org.springframework.boot.autoconfigure.SpringBootApplication
	import org.springframework.boot.autoconfigure.domain.EntityScan
	import org.springframework.boot.runApplication
	import org.springframework.context.annotation.Import

	@javax.annotation.Generated(value = ["org.openapitools.codegen.CodeCodegen"])

	@SpringBootApplication
	//codegen:merge
	@Import(SpringFoxConfig::class, WebConfig::class, KeycloakConfig::class)
	@EntityScan(value = ["app.client.party.domain", "app.client.domain"])
	class PartyApplication

	@Suppress("SpreadOperator")
	fun main(args: Array<String>) {
		runApplication<PartyApplication>(*args)
	}

	"""

	private val testKtNew = """
	package app.client.party

	import app.client.config.WebConfig
	import app.client.config.SpringFoxConfig
	import org.springframework.boot.autoconfigure.SpringBootApplication
	import org.springframework.boot.autoconfigure.domain.EntityScan
	import org.springframework.boot.runApplication
	import org.springframework.context.annotation.Import

	@javax.annotation.Generated(value = ["org.openapitools.codegen.CodeCodegen"])

	@SpringBootApplication
	@Import(SpringFoxConfig::class, WebConfig::class)
	@EntityScan(value = ["app.client.party.domain", "app.client.domain"])
	class PartyApplication

	@Suppress("SpreadOperator")
	fun main(args: Array<String>) {
		runApplication<PartyApplication>(*args)
	}

	"""

	private val expectedKtResult = """
	package app.client.party

	//codegen:merge
	import app.client.config.KeycloakConfig
	import app.client.config.WebConfig
	import app.client.config.SpringFoxConfig
	import org.springframework.boot.autoconfigure.SpringBootApplication
	import org.springframework.boot.autoconfigure.domain.EntityScan
	import org.springframework.boot.runApplication
	import org.springframework.context.annotation.Import

	@javax.annotation.Generated(value = ["org.openapitools.codegen.CodeCodegen"])

	@SpringBootApplication
	//codegen:merge
	@Import(SpringFoxConfig::class, WebConfig::class, KeycloakConfig::class)
	@EntityScan(value = ["app.client.party.domain", "app.client.domain"])
	class PartyApplication

	@Suppress("SpreadOperator")
	fun main(args: Array<String>) {
		runApplication<PartyApplication>(*args)
	}

	"""
}
