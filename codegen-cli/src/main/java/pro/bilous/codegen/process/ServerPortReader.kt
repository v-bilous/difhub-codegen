package pro.bilous.codegen.process

import io.swagger.v3.oas.models.OpenAPI

object ServerPortReader {

	const val defaultPort = "8080"

	fun findPort(openApi: OpenAPI): String {
		val servers = openApi.servers
		if (servers.isNullOrEmpty()) {
			return defaultPort
		}
		val serverUrl = servers.first().url
		return serverUrl.removeSuffix("/").split(":").last()
	}
}
