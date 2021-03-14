package pro.bilous.codegen.process

import io.swagger.v3.oas.models.media.Schema
import org.apache.commons.lang3.StringUtils
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.CodegenParameter
import java.util.*

class OperationAddon(val codegen: CodeCodegen) {

	@Suppress("UNCHECKED_CAST")
	fun populate(objs: MutableMap<String, Any>) {
		val operations = objs["operations"] as MutableMap<String, Any>
		val ops = operations["operation"] as MutableList<CodegenOperation>
		populateClassnames(objs, operations, ops)

		for (operation in ops) {
			// fix operation responses
			fixOperationResponses(operation)
			fixOperationParams(operation)
			// set summary as operationId
			operation.vendorExtensions["operationMethodName"] = operation.operationId
			operation.httpMethod = StringUtils.capitalize(operation.httpMethod.toLowerCase())
			if (operation.hasConsumes) {
				operation.vendorExtensions["x-operationConsumes"] = "APPLICATION_JSON_VALUE"
			}
			operation.hasProduces = true
			operation.vendorExtensions["x-operationProduces"] = "APPLICATION_JSON_VALUE"
			populateBooleanHttpMethods(operation)
			populatePrimaryQueryParam(operation)
			populatePrimaryPathParam(operation, objs)
			populateEventMapping(operation)
		}
		objs["hasEventMappingImport"] = ops.any {
			it.vendorExtensions[OptsPreProcessor.EVENT_MAPPING].toString().toBoolean()
		}
	}

	private fun populateEventMapping(operation: CodegenOperation) {
		if (!codegen.hasEventMapping) {
			operation.vendorExtensions[OptsPreProcessor.EVENT_MAPPING] = false
			return
		}
		when (operation.httpMethod.toLowerCase()) {
			"post", "put", "patch", "delete" -> operation.vendorExtensions[OptsPreProcessor.EVENT_MAPPING] = true
			else -> operation.vendorExtensions[OptsPreProcessor.EVENT_MAPPING] = false
		}
	}

	private fun populatePrimaryQueryParam(operation: CodegenOperation) {
		if (operation.queryParams.isNotEmpty()) {
			val firstQueryParam = operation.queryParams.first()
			operation.vendorExtensions["primaryQueryParamName"] = firstQueryParam.paramName
		}
	}

	private fun populatePrimaryPathParam(operation: CodegenOperation, objs: MutableMap<String, Any>) {
		val pathParam = operation.pathParams.findLast {
			operation.path.endsWith("{${it.paramName}}")
		}
		var pathParts = operation.path.split("/")
		if (pathParts.last() == "") {
			pathParts = pathParts.dropLast(1)
		}

		if (pathParam != null) {
			operation.vendorExtensions["primaryPathParamName"] = pathParam.paramName
			pathParts = pathParts.dropLast(1)
//			if (pathParam.dataType == "String") {
//				// force system to use UUID as id
//				pathParam.dataType = "UUID"
//			}
		}
		for (pathPart in pathParts.reversed()) {
			val parentParam = operation.pathParams.findLast { pathPart == "{${it.paramName}}" }
			if (parentParam != null) {
				operation.vendorExtensions["parentPathParamName"] = parentParam.paramName
				operation.vendorExtensions["hasPathParent"] = true
				objs["hasPathParent"] = true
				objs["parentPathParamName"] = parentParam.paramName
				// add also test version of controller path
				val controllerPath = objs["controllerPath"].toString()
				val testPath = controllerPath.replace("{${parentParam.paramName}}", "parent-id")
				// also remove all curly braces
				objs["testParentControllerPath"] = testPath.replace("{", "").replace("}", "")
				break
			}
		}

	}

	private fun populateBooleanHttpMethods(operation: CodegenOperation) {
		val extensionMethod = when (operation.httpMethod.toLowerCase()) {
			"get" -> if (operation.returnContainer == "List") "isGetListMethod" else "isGetMethod"
			"post" -> "isPostMethod"
			"put" -> "isPutMethod"
			"patch" -> "isPatchMethod"
			"delete" -> "isDeleteMethod"
			else -> "isNoHttpMethod"
		}
		operation.vendorExtensions[extensionMethod] = true
	}

	private fun fixOperationResponses(operation: CodegenOperation) {
		for (response in operation.responses) {
			if (response.message.isNullOrEmpty()) {
				if ("200" == response.code) {
					response.message = "Success response"
					if (response.baseType == null) {
						response.baseType = operation.returnBaseType
						//response.containerType = operation.returnContainer;
					}
				} else if ("400" == response.code) {
					response.message = "Something goes wrong"
					response.baseType = "ErrorResponse"
				} else if ("404" == response.code) {
					response.message = "Resource not found"
					response.baseType = "ErrorResponse"
				}
			} else {
				response.message = response.message.trimEnd()
				if (response.baseType == "Error") {
					response.baseType = "ErrorResponse"
				}
			}
		}
	}

	fun fixOperationParams(operation: CodegenOperation) {
		// remove bearer token header param if exist.
		operation.allParams.removeIf { it.isHeaderParam && it.paramName == "bearer" }
		operation.allParams.removeIf {
			it.isQueryParam &&
					arrayOf("options", "startTime", "endTime").contains(it.paramName)
		}

		operation.allParams.forEach {
			if (!it.required && it.isPrimitiveType) {
				it.dataType = "${it.dataType}?"
			}
		}

		// add all query params
		val searchParam = operation.queryParams.find { it.paramName == "search" }
		if (searchParam == null) {
			operation.allParams.forEach { it.hasMore = true }
			operation.allParams.last().hasMore = false
		}

		if (operation.returnContainer != "List") {
			val removed = operation.queryParams.removeIf { it.paramName == "search" }
			operation.allParams.removeIf { it.paramName == "search" }
			if (removed && operation.allParams.isNotEmpty()) {
				operation.allParams.last().hasMore = false
			}
			return
		}
		// add page parameter
		if (operation.httpMethod.toLowerCase() == "get" && operation.isListContainer) {
			val pageParameter = CodegenParameter()
					.apply {
						dataType = "Pageable"
						baseType = "Pageable"
						paramName = "page"
						baseName = "page"
						isPrimitiveType = false
						isInteger = false
						vendorExtensions["isPageParam"] = true
					}
			operation.allParams.add(pageParameter)
		}

		//operation.queryParams.add(pageParameter)
		//operation.queryParams.forEach { it.isQueryParam = true}



		operation.allParams.forEach { it.hasMore = true }
		operation.allParams.last().hasMore = false

	}

	private fun populateClassnames(objs: MutableMap<String, Any>, operations: MutableMap<String, Any>, ops: MutableList<CodegenOperation>) {
		val returnType = resolveClassname(objs, ops).removeSuffix("Model")
		objs["returnEntityType"] = returnType
		val classPrefix = operations["classname"]
				.toString().removeSuffix("Api").removeSuffix("Repository")

		objs["controllerClassname"] = classPrefix + "Controller"
		objs["serviceClassname"] = classPrefix + "Service"
		objs["repositoryClassname"] = classPrefix + "Repository"
		objs["mapperClassname"] = classPrefix + "Mapper"
		objs["converterClassname"] = classPrefix + "Converter"
		objs["controllerPath"] = findControllerPath(ops)

		val testModel = findTestCodegenModel(returnType)
		objs["testModel"] = testModel
		applyImportsForTest(objs, testModel)

		objs["converterLinkMethodname"] = ops.find { operation ->
			val idPathParam = operation.path.split("/").last().removePrefix("{").removeSuffix("}")
			operation.httpMethod == "GET" && operation.pathParams.any { it.paramName == idPathParam }
		}?.operationId ?: "getMethodNotFound"

		objs["validationRuleClassname"] = classPrefix + "ValidationRule"
		objs["returnModelType"] = returnType
	}

	fun applyImportsForTest(objs: MutableMap<String, Any>, testModel: CodegenModel) {
		val importList = mutableListOf<Map<String, String>>()
		val mappingSet = mutableSetOf<String>()
		addImportElements(testModel, mappingSet, importList)
		objs["testImports"] = importList
	}

	private fun addImportElements(testModel: CodegenModel,
								  mappingSet: MutableSet<String>,
								  importList: MutableList<Map<String, String>>,) {
		testModel.vars.forEach {
			if (it.vendorExtensions.containsKey("testModel")) {
				val inner = it.vendorExtensions["testModel"] as CodegenModel
				if (!mappingSet.contains(inner.classname)) {
					mappingSet.add(inner.classname)
					importList.add(mapOf(
						"import" to codegen.toModelImport(inner.classname),
						"classname" to inner.classname
					))
					addImportElements(inner, mappingSet, importList)
				}
			}
		}
	}

	private fun findTestCodegenModel(returnType: String): CodegenModel {
		val model = readModelByType(returnType)
		applyTestVars(model)
		return model
	}

	private fun readModelByType(type: String): CodegenModel {
		val schema = codegen.getOpenApi().components.schemas[type] as Schema<*>
		return codegen.fromModel(type, schema)
	}

	fun applyTestVars(model: CodegenModel) {
		model.vars.forEach {
			if (it.vendorExtensions.containsKey("isOneToOne") && it.vendorExtensions["isOneToOne"] as Boolean) {
				val embeddedModel = readModelByType(it.complexType)
				applyTestVars(embeddedModel)
				it.vendorExtensions["testModel"] = embeddedModel
				it.vendorExtensions["hasTestModel"] = true
			} else if (it.vendorExtensions.containsKey("embeddedComponent")) {
				val embeddedModel = it.vendorExtensions["embeddedComponent"] as CodegenModel
				applyTestVars(embeddedModel)
				it.vendorExtensions["testModel"] = embeddedModel
				it.vendorExtensions["hasTestModel"] = true
			} else if (it.isListContainer && !it.complexType.isNullOrEmpty() && !arrayOf("List<String>", "List<String>?").contains(it.datatypeWithEnum)) {
				val embeddedInListModel = readModelByType(it.complexType)
				applyTestVars(embeddedInListModel)
				it.vendorExtensions["testModel"] = embeddedInListModel
				it.vendorExtensions["hasTestModel"] = true
			} else {
				it.defaultValue = when {
					!it.defaultValue.isNullOrEmpty() && it.defaultValue != "null" && it.defaultValue != "listOf()" -> {
						it.defaultValue
					}
					it.vendorExtensions["x-data-type"] == "Guid" -> UUID.randomUUID().toString()
					it.isString -> {
						val maxLength = it.maxLength
						if (it.maxLength != null && it.maxLength > 0 && it.maxLength <= 16) {
							"test string value".substring(0, maxLength)
						} else "test string value"
					}
					it.isInteger -> "8"
					it.dataType == "Date" -> {
						"Date()"
					}
					it.isBoolean -> "false"
					it.isLong -> "9223372036854775807L"
					it.dataType == "BigDecimal" -> "777.toBigDecimal()"
					it.isModel && arrayOf("String", "String?").contains(it.datatypeWithEnum) -> {
						it.isString = true
						"test_enum_value"
					}
					it.isListContainer && arrayOf("List<String>", "List<String>?").contains(it.datatypeWithEnum) -> {
						"\"test_list_string_value\""
					}
					it.isFreeFormObject && arrayOf("String", "String?").contains(it.datatypeWithEnum) -> {
						it.isString = true
						"test string (was object) value"
					}
					else -> "null"
				}
			}
		}
	}

	private fun findControllerPath(ops: List<CodegenOperation>): String {
		val path = ops.find { operation ->
			val idPathParam = operation.path.split("/").last().removePrefix("{").removeSuffix("}")
			operation.httpMethod == "GET" && operation.pathParams.any { it.paramName == idPathParam }
		}?.path ?: "/path-not-found"

		if (path.endsWith("}")) {
			return path.split("/").dropLast(1).joinToString("/")
		}
		return path
	}

	private fun resolveClassname(objs: MutableMap<String, Any>, ops: MutableList<CodegenOperation>): String {
		val imports = objs["imports"] as List<Any>
		if (imports.isNotEmpty()) {
			val importMap = imports.first() as Map<String, Any>
			if (importMap.containsKey("classname")) {
				return importMap["classname"].toString()
			}
		}
		return ops.find { it.returnType != "Void" }?.returnType ?: "Void"
	}
}
