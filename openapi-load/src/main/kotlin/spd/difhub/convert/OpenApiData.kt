package spd.difhub.convert

import io.swagger.v3.oas.models.OpenAPI

data class OpenApiData(
	val openApi: OpenAPI,
	val appName: String,
	val system: String
)
