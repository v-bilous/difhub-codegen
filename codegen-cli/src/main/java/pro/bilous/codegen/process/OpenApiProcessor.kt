package pro.bilous.codegen.process

import io.swagger.v3.oas.models.OpenAPI
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.utils.StringUtils.camelize
import org.openapitools.codegen.utils.URLPathUtils
import java.util.*

class OpenApiProcessor(val codegen: CodeCodegen) {

	private val additionalProperties: MutableMap<String, Any> = codegen.additionalProperties()

	fun preprocessOpenAPI(openAPI: OpenAPI) {
		setupServerPort(openAPI)

		if (!additionalProperties.containsKey(CodeCodegen.TITLE)) {
			// From the title, compute a reasonable name for the package and the API
			var title: String? = openAPI.info.title

			// Drop any API suffix
			if (title != null) {
				title = title.trim { it <= ' ' }.replace(" ", "-")
				if (title.toUpperCase(Locale.ROOT).endsWith("API")) {
					title = title.substring(0, title.length - 3)
				}

				codegen.setTitle(camelize(codegen.sanitizeName(title), true))
			}
			additionalProperties[CodeCodegen.TITLE] = codegen.getTitle()
		}

		if (!additionalProperties.containsKey(CodeCodegen.SERVER_PORT)) {
			val url = URLPathUtils.getServerURL(openAPI, null)
			this.additionalProperties[CodeCodegen.SERVER_PORT] = URLPathUtils.getPort(url, 8080)
		}

		if (openAPI.paths == null) {
			return
		}
		for (pathname in openAPI.paths.keys) {
			val path = openAPI.paths[pathname] ?: continue
			if (path.readOperations() == null) continue

			for (operation in path.readOperations()) {
				if (operation.tags == null) continue

				val tags = mutableListOf<MutableMap<String, String>>()
				for (tag in operation.tags) {
					val value = mutableMapOf<String, String>()
					value["tag"] = tag
					value["hasMore"] = "true"
					tags.add(value)
				}
				if (tags.size > 0) {
					tags[tags.size - 1].remove("hasMore")
				}
				if (operation.tags.size > 0) {
					val tag = operation.tags[0]
					operation.tags = mutableListOf(tag)
				}
				operation.addExtension("x-tags", tags)
			}
		}
	}

	private fun setupServerPort(openAPI: OpenAPI) {
		additionalProperties["serverPort"] = ServerPortReader.findPort(openAPI)
	}
}
