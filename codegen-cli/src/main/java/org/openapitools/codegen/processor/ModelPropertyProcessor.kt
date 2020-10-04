package org.openapitools.codegen.processor

import io.swagger.v3.oas.models.media.Schema
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenProperty
import org.openapitools.codegen.utils.CamelCaseConverter

class ModelPropertyProcessor(val codegen: CodeCodegen) {

	private val additionalProperties = codegen.additionalProperties()
	private val entityMode = codegen.entityMode
	private val importMappings = codegen.importMapping()

	private val joinProperties: MutableList<CodegenProperty>

	init {
		if (additionalProperties["joinTables"] == null) {
			additionalProperties["joinTables"] = mutableListOf<CodegenProperty>()
		}
		joinProperties = additionalProperties["joinTables"] as MutableList<CodegenProperty>
	}

	fun postProcessModelProperty(model: CodegenModel, property: CodegenProperty) {
		property.name = property.name.trimEnd()
		if ("null" == property.example) {
			property.example = null
		}

		if (property.datatypeWithEnum == "Object" && property.vendorExtensions.containsKey("x-data-type")) {
			val ktType = when (property.vendorExtensions["x-data-type"]) {
				"Unsigned Integer" -> "Int"
				"Date" -> "Date"
				else -> "String"
			}
			property.dataType = ktType
			property.datatypeWithEnum = ktType
		} else if (property.datatypeWithEnum == "Integer") {
			property.dataType = "Int"
			property.datatypeWithEnum = "Int"
		}
		if (property.datatypeWithEnum == "Date") {
			model.imports.add("Date")
		}

		if (model.name == "HumanName") {
			println("")
		}
		// set property as optional
		if (!property.required) {
			val realType = "${property.datatypeWithEnum}?"
			property.datatypeWithEnum = realType
		}

		populateTableExtension(model, property)
		// TODO support all possible types
		property.vendorExtensions["isNeedSkip"] = "id" == property.name.toLowerCase()

		if (property.vendorExtensions.containsKey("x-codegen-type")) {
			val codegenType = property.vendorExtensions["x-codegen-type"].toString()
			if (importMappings.containsKey(codegenType)) {
				property.datatypeWithEnum = codegenType
				model.imports.add(codegenType)
				if (codegenType == "JSONObject" && entityMode) {
					property.vendorExtensions["hibernateType"] = "com.bhn.datamanagement.usertype.JSONObjectUserType"
					property.vendorExtensions["columnType"] = "TEXT"
					model.imports.add("Type")
				}
				return
			}
		}

		if (property.isListContainer && property.datatypeWithEnum.startsWith("List")) {
//			property.datatypeWithEnum = "Set" + property.datatypeWithEnum.removePrefix("List")
			property.defaultValue = "listOf()"
			model.imports.remove("List")
			model.imports.remove("ArrayList")
		} else if (property.isModel && property.complexType.endsWith("IdentityModel") && property.name.endsWith("Id")) {
			// convert Reference Type to the String
			property.dataType = "String"
			property.datatypeWithEnum = property.dataType
			property.isModel = false
			property.isString = true
		}

		if (hasEnumValues(property)) {
			convertToMetadataProperty(property, model)
		}
		addGuidAnnotation(property, model)
	}

	fun resolvePropertyType(property: CodegenProperty) {
		if (property.vendorExtensions["columnType"] != null) {
			return
		}
		when (property.datatypeWithEnum) {
			"Boolean", "Boolean?" -> {
				property.vendorExtensions["columnType"] = "TINYINT(1)"
				property.vendorExtensions["hibernateType"] = "java.lang.Boolean"
				property.isBoolean = true
			}
			"Date", "Date?" -> {
				property.vendorExtensions["columnType"] = "datetime(6)"
				property.vendorExtensions["hibernateType"] = "java.util.Date"
				property.isDate = true
			}
			"Int", "Int?" -> {
				property.vendorExtensions["columnType"] = "int"
				property.isInteger = true
			}
			else -> {
				property.vendorExtensions["columnType"] = "VARCHAR(255)"
				property.vendorExtensions["hibernateType"] = "java.lang.String"
			}
		}
	}

	private fun addGuidAnnotation(property: CodegenProperty, model: CodegenModel) {
		if (property.datatypeWithEnum != "String") {
			return
		}
		val xDataType = property.vendorExtensions.getOrDefault("x-data-type", null)
		if (xDataType != null && xDataType == "Reference") {
			property.vendorExtensions["isGuid"] = true
			if (!codegen.entityMode) {
				model.imports.add("Guid")
			}
		}
	}

	private fun convertToMetadataProperty(property: CodegenProperty, model: CodegenModel) {
		property.vendorExtensions["isMetadataAnnotation"] = true

		property.vendorExtensions["metaGroupName"] = CamelCaseConverter.convert(property.complexType.removeSuffix("Model"))
		property.datatypeWithEnum = if(property.required) "String" else "String?"
		model.imports.removeIf { it == property.complexType }
		if (!codegen.entityMode) {
			model.imports.add("MetaDataAnnotation")
		}
	}

	private fun hasEnumValues(property: CodegenProperty): Boolean {
		if (property.allowableValues == null || !property.allowableValues.containsKey("values")) {
			return false
		}
		val enumValues = property.allowableValues["values"] as List<String>
		return enumValues.isNotEmpty()
	}

	private fun populateTableExtension(model: CodegenModel, property: CodegenProperty) {
		applyColumnNames(model, property)
		applyEmbeddedComponent(model, property)

		if (entityMode && property.isListContainer) {
			val modelTableName = CamelCaseConverter.convert(model.name).toLowerCase()
			val complexType = if (property.complexType.isNullOrEmpty()) {
				readTypeFromFormat(property)
			} else property.complexType

			// if we do not have information for the join table. set it to JSON field
			if (complexType == null) {
				property.vendorExtensions["hasJsonType"] = true
				property.vendorExtensions["columnType"] = "JSON"
				model.imports.add("JsonType")
				return
			} else if (property.complexType != null) {
				val realType = property.complexType.removeSuffix("Model")
				val innerModelSchema = codegen.getOpenApi().components.schemas[realType] as Schema<*>
				val innerModel = codegen.fromModel(realType, innerModelSchema)

				val forceToJson = innerModel.parent == null
							&& innerModel.allVars.find { it.name == "id" || it.name == "identity" } == null
				if (forceToJson) {
					property.vendorExtensions["hasJsonType"] = true
					property.vendorExtensions["columnType"] = "JSON"
					model.imports.add("JsonType")
					return
				}
			}

			val propertyTableName = if (isCurrentAppModel(complexType)) {
				CamelCaseConverter.convert(complexType).toLowerCase()
			} else {
				CamelCaseConverter.convert(property.name).toLowerCase()
			}
			val joinTableName = joinTableName(modelTableName, propertyTableName)
			property.getVendorExtensions()["modelTableName"] = modelTableName
			property.getVendorExtensions()["propertyTableName"] = propertyTableName
			property.vendorExtensions["hasPropertyTable"] = isCurrentAppModel(complexType)
			property.getVendorExtensions()["joinTableName"] = joinTableName
			property.getVendorExtensions()["joinColumnName"] = "${modelTableName}_id"
			property.getVendorExtensions()["inverseJoinColumnName"] = "${propertyTableName}_id"
			property.vendorExtensions["isReferenceElement"] = property.complexType.isNullOrEmpty()

			if (!joinProperties.any { it.vendorExtensions["joinTableName"] == joinTableName}) {
				joinProperties.add(property)
			}
		}
	}

	private val columnNamesToEscape = arrayOf("use", "open", "drop", "create", "table", "rank", "system")
	fun applyColumnNames(model: CodegenModel, property: CodegenProperty) {
		val columnName = CamelCaseConverter.convert(property.name).toLowerCase()
		property.getVendorExtensions()["columnName"] = columnName
		property.getVendorExtensions()["escapedColumnName"] = if (columnNamesToEscape.contains(columnName)) {
			"${columnName}_"
		} else columnName
		property.getVendorExtensions()["columnName"] = property.getVendorExtensions()["escapedColumnName"]

	}

	fun applyEmbeddedComponent(model: CodegenModel, property: CodegenProperty) {
		val isInnerModel
				= property.isModel && !property.complexType.isNullOrEmpty()
				&& !importMappings.containsKey(property.nameInCamelCase)
				&& !importMappings.containsKey(property.complexType)
				&& codegen.getOpenApi().components.schemas.containsKey(property.complexType)
				&& !hasEnumValues(property)

		if (!isInnerModel) {
			return
		}
		val realType = property.complexType.removeSuffix("Model")
		val innerModelSchema = codegen.getOpenApi().components.schemas[realType] as Schema<*>
		val innerModel = codegen.fromModel(realType, innerModelSchema)
		if (innerModel.vendorExtensions["isEmbeddable"] == true) {
			property.vendorExtensions["embeddedComponent"] = innerModel
			property.vendorExtensions["isEmbedded"] = true
			// add embedded column names since we are in embedded mode.
			innerModel.vars.forEach { prop ->
				val originalName = prop.vendorExtensions["columnName"]
				val parentName = property.vendorExtensions["columnName"]
				prop.vendorExtensions["embeddedColumnName"] = "${parentName}_${originalName}"
			}
		}
	}

	private fun isCurrentAppModel(modelName: String): Boolean {
		return codegen.getOpenApi().components.schemas.containsKey(modelName)
	}

	fun readTypeFromFormat(property: CodegenProperty): String? {
		val dataKey = "dataType:"
		if (property.dataFormat.isNullOrEmpty()) {
			return null
		}
		val dataFormat = property.dataFormat.replace(" ", "")
		return dataFormat.split("|")
			.first { it.startsWith(dataKey) }
			.removePrefix(dataKey)
	}

	fun joinTableName(first: String, second: String): String {
		return arrayOf(first, second).sortedArray().joinToString(separator = "_to_")
	}
}
