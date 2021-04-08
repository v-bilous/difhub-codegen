package pro.bilous.intellij.plugin.project

import io.swagger.util.Yaml
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

internal class ProjectFilesCreatorTest {

	@Test
	fun `test credentials file creation`() {
		val request = ProjectCreationRequest().apply {
			username = "test@username"
			password = "test password"
			organization = "Demo org"
		}

		val configFolder = Paths.get("build/tmp").toAbsolutePath().toString()

		ProjectFilesCreator().createCredentialsFile(request, configFolder)

		val configFile = File("$configFolder/.credentials.yaml")

		val configTree = Yaml.mapper().readTree(configFile.inputStream())
		val username = configTree.get("username").asText()
		val password = configTree.get("password").asText()
		val organization = configTree.get("organization").asText()

		assertEquals(request.username, username)
		assertEquals(request.password, password)
		assertEquals(request.organization, organization)
	}

	@Test
	fun `test settings file creation`() {
		val request = ProjectCreationRequest().apply {
			enumsType = "SimpleEnum"
			database = "Mysql"
		}

		val configFolder = Paths.get("build/tmp").toAbsolutePath().toString()

		ProjectFilesCreator().createConfigFile(request, configFolder)

		val configFile = File("$configFolder/settings.yaml")

		val configTree = Yaml.mapper().readTree(configFile.inputStream())
		val enumsType = configTree.get("enumsType").asText()
		val database = configTree.get("database").asText()

		assertEquals(request.enumsType, enumsType)
		assertEquals(request.database, database)
	}
}
