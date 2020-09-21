package spd.difhub.convert

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.slf4j.LoggerFactory
import spd.difhub.load.ApplicationsLoader
import spd.difhub.load.DatasetsLoader
import spd.difhub.load.DefLoader
import spd.difhub.load.InterfacesLoader
import spd.difhub.load.ModelLoader
import spd.difhub.model.Model
import java.lang.IllegalStateException

class DifHubToSwaggerConverter(val system: String) {
	private val log = LoggerFactory.getLogger(DifHubToSwaggerConverter::class.java)

	private val appLoader = ApplicationsLoader()

	fun convertAll(): List<OpenApiData> {
		ModelLoader.globalModelCache.clear()

		val appModels = appLoader.loadAll(system)

		val result = mutableListOf<OpenApiData>()
		appModels?.forEach {
			if (it.`object`!!.usage == "Service") {
				val appName = it.identity.name
				result.add(OpenApiData(convert(appName), appName, system))
			} else {
				log.warn("Ignoring application with name ${it.identity.name}. Usage =`Service` required to enable code generation.")
			}
		}
		return result
	}

	fun convert(application: String): OpenAPI {
		val openApi = OpenAPI()

		val appModel = appLoader.loadOne(system, application)
		if (appModel!!.`object`!!.usage != "Service") {
			throw IllegalStateException("Only Service application open for Generation. Please change Usage on Difhub!")
		}
		val appSettings = appLoader.loadAppSettings(system, application)
		openApi.info = readInfo(appModel)
		openApi.servers = buildServers(appSettings)

		//addPathAndDefRecursively()
		convertModelsToDefinitions(application, openApi)

		convertInterfacesToPaths(application, openApi)

		return openApi
	}

	private fun convertInterfacesToPaths(application: String, openApi: OpenAPI) {
		val interfaces = InterfacesLoader().load(system, application)

		val modelsToLoad = mutableMapOf<String, String>()
		val parameters = mutableMapOf<String, Parameter>()

		val tags = mutableListOf<Tag>()

		interfaces
				?.sortedBy { it.identity.name }
				?.filter {
					it.identity.name != "Entities"
				}
				?.forEach {
			val converter = InterfaceToPathConverter(it, openApi)
			converter.convert().forEach { (key, path) ->
				if (path.readOperations().isEmpty()) {
					System.err.println("Missing operations for the PATH: $key, ignoring path...")
				} else {
					openApi.path(key, path)
				}
			}
			val tag = if (it.`object`?.tags.isNullOrEmpty()) {
				it.identity
			} else {
				it.`object`!!.tags!!.first()
			}
			tags.add(Tag().name(tag.name).description(
					tag.description ?: it.identity.description
			))

			modelsToLoad.putAll(converter.pathModelsToLoad)
			parameters.putAll(converter.parameters)
		}

		tags
			.forEach {
				if (openApi.tags == null) {
					openApi.tags = mutableListOf()
				}
				if (!openApi.tags.any { tag -> tag.name == it.name }) {
					openApi.addTagsItem(it)
				}
			}

		modelsToLoad.forEach {
			val model = ModelLoader(DefLoader()).loadModel(it.value)
			if (model != null) {
				addDefRecursively(model, openApi)
			} else {
				System.err.println("Model failed when load: ${it.value}")
			}
		}
		parameters.forEach{
			openApi.components.addParameters(it.key, it.value)
		}
	}

	private fun convertModelsToDefinitions(application: String, openApi: OpenAPI) {
		val datatests = DatasetsLoader().load(system, application, type = "Resource")

		datatests?.forEach {
			addDefRecursively(it, openApi)
		}
	}

	private val processedDefinitions = mutableMapOf<String, MutableList<String>>()
	//private val innerPaths = mutableListOf<Model>()

	private fun addDefRecursively(source: Model, openApi: OpenAPI) {
		val definition = processedDefinitions.getOrPut(openApi.info.title) { mutableListOf() }

		val targetName = normalizeTypeName(source.identity.name)
		if (definition.contains(targetName)) {
			return
		}

		definition.add(targetName)
		val defConverter = DefinitionConverter(source)
		defConverter.convert()
				.forEach {
					openApi.schema(it.key, it.value)
				}
	}

	private fun buildServers(source: Model?): List<Server> {
		val defaultPort = "8090"
		val serverPort = if (source != null) {
			findServerPort(source) ?: defaultPort
		} else defaultPort

		return listOf(
			Server().apply {
				url = "http://localhost:$serverPort"
				description = "Development server"
			}
		)
	}

	private fun findServerPort(source: Model): String? {
		return source.`object`?.properties?.find {
			it.identity?.name == "serverPort"
		}?.value
	}

	private fun readInfo(source: Model): Info {
		val info = Info()
//		source.version!!
		info.version = "1.0.0" // "${source.version.major}.${source.version.minor}.${source.version.revision}"
		info.title = "${source.identity.name} API"
		info.description = "${source.identity.description}"
			.trimEnd()
			.removeSuffix("\n").removeSuffix("\n").removeSuffix("\t")

		return info
	}
}
