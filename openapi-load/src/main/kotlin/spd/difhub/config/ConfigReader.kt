package spd.difhub.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object ConfigReader {

	private val mapper = createMapper()

	private fun createMapper(): ObjectMapper {
		return ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
	}

	fun loadConfig(): Config {
		var orgName = System.getProperty("DIFHUB_ORG_NAME")
		if (orgName.isNullOrEmpty()) {
			orgName = System.getenv("DIFHUB_ORG_NAME") ?: "Demo org"
		}
		return Config(DifHub("https://metaserviceprod.azurewebsites.net/api", "/$orgName"))
//		val configUrl = ConfigReader::class.java.getResource("/config.yaml")
//		val path = Paths.get(configUrl.toURI())
//		return loadFromFile(path)
	}

//	private fun loadFromFile(path: Path): Config {
//		return Files.newBufferedReader(path).use {
//			mapper.readValue(it, Config::class.java)
//		}
//	}
}
