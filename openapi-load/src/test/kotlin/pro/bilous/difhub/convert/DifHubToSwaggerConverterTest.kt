package pro.bilous.difhub.convert

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.swagger.util.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pro.bilous.difhub.load.ApplicationsLoader
import pro.bilous.difhub.load.IModelLoader
import pro.bilous.difhub.model.Model

internal class DifHubToSwaggerConverterTest {

	private val appJson = """
{
  "identity": {
    "id": "f94b7b29-5999-4ee7-b77f-0a5ab2e305e0",
    "name": "Organization",
    "description": "Service to manage health care organization resources."
  },
  "object": {
    "parent": {
      "id": "9bd2d779-cb46-4ce4-ab67-fc3417c720a4",
      "name": "/organizations/Infort Technologies/systems/Healthcare"
    },
    "tags": [],
    "documents": [],
    "type": "Application",
    "usage": "Service",
    "access": "External",
    "properties": [],
    "history": {
      "created": "2020-11-30T12:35:17.99",
      "createdBy": "sashaberger@hotmail.com",
      "updated": "2020-11-30T12:35:17.99",
      "updatedBy": "v.bilous@spd-ukraine.com",
      "completions": []
    },
    "alias": "OrgService"
  }
}
	""".trimIndent()

	@Test
	fun `should convert to app`() {
		val mockModelLoader: IModelLoader = mock()
		whenever(mockModelLoader.loadModel(any())).thenReturn(readValue(appJson))

		val appLoader = ApplicationsLoader().apply {
			modelLoader = mockModelLoader
		}

		val converter = DifHubToSwaggerConverter("system")
		converter.appLoader = appLoader
		converter.datasetsLoader = mock()
		converter.interfacesLoader = mock()

		val openApi = converter.convert("Organization")

		assertEquals("Organization API", openApi.info.title)
		assertEquals("OrgService", openApi.info.extensions["x-app-alias"])
	}

	private fun readValue(jsonText: String): Model {
		Json.mapper().registerKotlinModule()
		return Json.mapper().readValue(appJson)
	}

	private inline fun <reified T> ObjectMapper.readValue(json: String): T
			= readValue(json, object : TypeReference<T>(){})
}
