package pro.bilous.difhub.convert

import io.swagger.v3.core.util.PrimitiveType
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.HeaderParameter
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.PathParameter
import io.swagger.v3.oas.models.parameters.QueryParameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import pro.bilous.difhub.model.Field
import pro.bilous.difhub.model.Model
import pro.bilous.difhub.model.OperationsItem
import pro.bilous.difhub.model.ParametersItem
import pro.bilous.difhub.model.ResponsesItem

class InterfaceToPathConverter(private val source: Model, private val openApi: OpenAPI) {

	val pathModelsToLoad = mutableMapOf<String , String>()
	val parameters = mutableMapOf<String, Parameter>()

	private val sourcePath = source.path.replace("\u200B", "")

	fun convert(): Map<String, PathItem> {

		val paths = mutableMapOf<String, PathItem>()

		val path = PathItem()

		source.parameters?.forEach {
			// fix issue with difhub model.

			var location = it.location
			if(sourcePath.contains("{${it.field!!.identity.name}}")) {
				location = "Path"
			}
			val identityName = it.field.identity.name;
			var param: Parameter? = null
			if (location == "Body") {
				val body = createBodyParameter(it)
				openApi.components.addRequestBodies(identityName, body)
			} else {
				param = when(location) {
					"Header" -> createHeaderParameter(it)
					"Path" -> createPathParameter(it)
					"Query" -> createQueryParameter(it)
					//"Body" -> createBodyParameter(it)
					else -> throw IllegalStateException()
				}
			}
			if (param != null) {
				parameters[param.name] = param
			}
		}
		source.operations?.forEach {
			addOperation(it, path)
		}
		paths[sourcePath] = path
		postProcessOperations(paths)
		return paths
	}

	private fun postProcessOperations(paths: MutableMap<String, PathItem>) {
		if (!sourcePath.endsWith("}")) {
			return
		}
		val primaryPathParamName = sourcePath.split("/").last().removePrefix("{").removeSuffix("}")
		val pathParam = source.parameters?.find { it.field!!.identity.name == primaryPathParamName }
		if (pathParam == null || !pathParam.field!!.optional) {
			return
		}
		val extraPathItem = PathItem()
		val pathItem = paths[sourcePath]!!

		val postOperation = pathItem.post
		pathItem.post = null

		val getOperation = pathItem.get
		val getResponses = wrap200ResponseAsArray(getOperation.responses)

		extraPathItem.get = Operation().apply {
			operationId = "${getOperation.operationId}List"
			responses = getResponses
			tags = getOperation.tags
			summary = "${getOperation.summary}List"
			description = getOperation.description
			parameters = getOperation.parameters
		}
		extraPathItem.post = postOperation
		if (pathItem.parameters != null) {
			extraPathItem.parameters = pathItem.parameters.filter {
				when {
					it.`$ref` != null -> it.`$ref`.split("/").last() != primaryPathParamName
					it.name != null -> it.name != primaryPathParamName
					else -> false
				}
			}
		}
		val extraPathLine = sourcePath.removeSuffix("/{$primaryPathParamName}")

		paths[extraPathLine] = extraPathItem
		if (pathItem.get.parameters.isNullOrEmpty()) {
			System.err.println("Illegal missing get parameters: ${pathItem.get.summary}")
		} else {
			pathItem.get.parameters = pathItem.get.parameters.filter { it.`$ref`.split("/").last() != "search" }
		}
	}

	private fun wrap200ResponseAsArray(apiResponses: ApiResponses): ApiResponses {
		val get200Response = apiResponses["200"]!!
		if (get200Response.content == null) {
			return apiResponses
		}
		val objectSchema = get200Response.content["application/json"]?.schema ?: return apiResponses

		return ApiResponses().apply {
			apiResponses.forEach {
				addApiResponse(it.key, it.value)
			}
			addApiResponse("200", ApiResponse().apply {
				description = get200Response.description
				content = Content().addMediaType("application/json", MediaType().schema(ArraySchema().items(objectSchema)))
			})
		}
	}

	private fun addOperation(item: OperationsItem, path: PathItem) {
		val op = Operation()

		if (!source.`object`?.tags.isNullOrEmpty()) {
			source.`object`?.tags?.forEach {
				op.addTagsItem(it.name)
			}
		} else {
			op.addTagsItem(source.identity.name)
		}

		val operationId = "${source.identity.name.decapitalize()}${item.identity.name.capitalize()}"

		op.operationId = operationId
		op.summary = item.identity.name
		op.description = item.identity.description

		item.parameters?.forEach {
			addParameter(it, op, path)
		}
		item.responses?.forEach {
			if (op.responses == null) {
				op.responses = ApiResponses()
			}
			if (findResponse(it.name) != null) {
				val (key, value) = createResponse(it)
				op.responses.addApiResponse(key, value)
			}
		}
		path.operation(PathItem.HttpMethod.valueOf(item.action.toUpperCase()), op)
	}

	private val pathRegisteredParams = mutableListOf<String>()
	private fun addParameter(paramSource: ParametersItem, op: Operation, path: PathItem) {
		val param = createParameter(paramSource)

		val sourceParam = findParameter(paramSource.name) ?: return

		if (sourceParam.location == "Body") {
			op.requestBody(RequestBody().`$ref`(paramSource.name))
			return
		}

		when(parameters[paramSource.name]) {
			is PathParameter -> {
				if (!pathRegisteredParams.contains(paramSource.name)) {
					path.addParametersItem(param)
					pathRegisteredParams.add(paramSource.name)
				}
			}
			else -> op.addParametersItem(param)
		}
	}

	private fun findParameter(name: String): ParametersItem? {
		return source.parameters?.findLast { it.field!!.identity.name == name }
	}

	private fun findResponse(name: String): ResponsesItem? {
		return source.responses?.findLast { it.field != null && it.field.identity.name == name }
	}

	private fun createParameter(item: ParametersItem): Parameter {
		return Parameter().apply { `$ref` = item.name }
	}

	private fun createHeaderParameter(item: ParametersItem): Parameter {
		val param = HeaderParameter()
		item.field!!
		param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.schema = createSchema(item.field)
		param.required = !item.field.optional
		return param
	}

	private fun createPathParameter(item: ParametersItem): Parameter {
		val param = PathParameter()
		item.field!!
		param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.schema = createSchema(item.field)
		param.required = true //!item.field.optional should always be true.
		return param
	}

	private fun createSchema(field: Field): Schema<Any> {
		val type = PrimitiveType.fromTypeAndFormat(field.type.toLowerCase(), field.format.toLowerCase()) ?: return ObjectSchema()

		return if (field.count == 0) {
			ArraySchema().apply {
				items = type.createProperty()
			}
		} else {
			type.createProperty()
		}
	}


	private fun createQueryParameter(item: ParametersItem): Parameter {
		val param = QueryParameter()
		item.field!!
		param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.schema = createSchema(item.field)
		param.required = !item.field.optional
		if (item.field.count == 0) {
			param.explode = false
		}
		return param
	}

	private fun createBodyParameter(item: ParametersItem): RequestBody {
		val param = RequestBody()
		item.field!!
		//param.name = item.field.identity.name
		param.description = item.field.identity.description
		param.required = !item.field.optional
		param.content(createContent(item.field))
		return param
	}

	private fun createResponse(it: ResponsesItem): Pair<String, ApiResponse> {
		val item = findResponse(it.name)!!

		val response = ApiResponse()
		response.description = item.field!!.identity.description

		if ("Structure" == item.field.type) {
			response.content(createContent(item.field))
			pathModelsToLoad[getDefType(item.field.reference)] = item.field.reference
		}

		return Pair(item.code, response)
	}

	private fun createContent(field: Field): Content {
		val typeRef = "#/components/schemas/${getDefType(field.reference)}"
		val typeSchema = Schema<Any>().`$ref`(typeRef)
		return Content().addMediaType("application/json",
			MediaType().schema(
					if (field.count == 0) {
						ArraySchema().items(typeSchema)
					} else {
						typeSchema
					}
			)
		)
	}

	private fun getDefType(reference: String): String {
		val array = reference.split("/")
		return array[array.lastIndexOf("datasets") + 1]
	}
}
