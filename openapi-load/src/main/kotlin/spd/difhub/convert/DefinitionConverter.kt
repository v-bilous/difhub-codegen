package spd.difhub.convert

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.swagger.v3.core.util.PrimitiveType
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import spd.difhub.load.DefLoader
import spd.difhub.load.ModelLoader
import spd.difhub.model.FieldsItem
import spd.difhub.model.Model

class DefinitionConverter(private val source: Model) {
	private val definitions = mutableMapOf<String, Schema<*>>()

	fun convert(): Map<String, Schema<*>>  {
		val schema = createModelImpl(source)
		definitions[schema.name] = schema
		return definitions
	}

	private fun createModelImpl(model: Model) : Schema<*> {
		val schema = if (model.`object`?.usage == "Enum") {
			createEnumSchema(model)
		} else {
			createObjectSchema(model)
		}
		schema.name = normalizeTypeName(model.identity.name)
		schema.description = model.identity.description

		if (model.`object` != null) {
			schema.addExtension("x-data-type", model.`object`.usage)
			val version = model.version!!
			schema.addExtension("x-version", "${version.major}.${version.minor}.${version.revision}")
			schema.addExtension("x-path", "${model.`object`.parent!!.name}/datasets/${model.identity.name}")
		}
		return schema
	}

	private fun createObjectSchema(model: Model): ObjectSchema {
		val schema = ObjectSchema()
		val identityName = model.identity.name
		model.structure?.fields?.forEach {
			val name = getFieldName(it)
			if (!shouldIgnore(identityName, name)) {
				schema.addProperties(name, fieldToProperty(it))
			}
		}
		schema.required = model.structure?.fields?.filter { !it.optional }?.map { getFieldName(it) }
		return schema
	}

	private fun createEnumSchema(model: Model): StringSchema {
		val enumModel = EnumConverter(model).convert()

		val schema = StringSchema()
		val validEnums = enumModel.values.filter { it.value != null }
		schema.enum = validEnums.map { it.value }

		schema.addExtension("x-enum-metadata", validEnums)
		return schema
	}

	private fun getFieldName(fieldsItem: FieldsItem): String {
		return fieldsItem.identity.name.decapitalize()
	}

	private fun shouldIgnore(modelName: String, propertyName: String): Boolean {
		if (modelName.startsWith("_") || propertyName.startsWith("_")) {
			return true
		}
		return (modelName == "Identity" && propertyName == "translations")
			|| ((modelName == "Entity" || modelName == "Error") && propertyName == "properties")
	}

	private fun fieldToProperty(item: FieldsItem): Schema<Any> {
		val property = when (item.type) {
			"Structure" -> createStructureProperty(item)
			"Reference" -> createReferenceProperty(item)
			"Enum" -> createEnumProperty(item)
			else -> createProperty(item)
		}

		if (item.count != null && item.count == 0) {
			return ArraySchema().apply {
				description = property.description
				items = property
			}
		}
		return property
	}

	private fun createEnumProperty(item: FieldsItem): Schema<Any> {
		return createStructureProperty(item)
	}

	private fun createReferenceProperty(item: FieldsItem): Schema<Any> {
		return createProperty(item)
	}

	private fun createStructureProperty(item: FieldsItem): ComposedSchema {
		val property = ComposedSchema()

		val array = item.reference.split("/")
		property.description = item.identity.description
		property.allOf = listOf(ObjectSchema().apply { `$ref` = normalizeTypeName(array[array.lastIndexOf("datasets") + 1]) })
		//property.`$ref` = normalizeTypeName(array[array.lastIndexOf("datasets") + 1])

		property.description = item.identity.description
		if (!definitions.containsKey(property.`$ref`)) {

			val source = ModelLoader(DefLoader()).loadModel(item.reference)
			if (source != null) {
				val schema = createModelImpl(source)
				definitions[schema.name] = schema
			}
		}
		return property
	}

	private fun createProperty(item: FieldsItem): Schema<Any> {
		val type = normalizeType(item.type)

		val description = item.identity.description
		val format = if (type.format == "reference") readRefFormat(item) else type.format

		val primitiveType = PrimitiveType.fromName(type.type)
			?: error("Can not create primitive for type: ${type.type} , format: $format")

		val schema = primitiveType.createProperty()
		schema.format = format
		if (!description.isNullOrEmpty()) {
			schema.description = description
		}
		if (item.type == "Enum") {
			var source : Model? = null
			try {
				source = ModelLoader(DefLoader()).loadModel(item.reference)
			} catch (e: MismatchedInputException) {
				println("Failed to load Enum ${item.reference}")
				println(e)
			}
			if (source != null) {
				val enumModel = EnumConverter(source).convert()
				schema.enum = enumModel.values.mapNotNull { it.value }
			}
		}
		schema.addExtension("x-data-type", item.type)
		item.properties?.forEach {
			if (it.identity != null) {
				schema.addExtension("x-${it.identity.name}", it.value)
			}
		}
		return schema
	}

	private fun readRefFormat(item: FieldsItem): String {
		val refParts = item.reference.split("/")
		val dataType = readReference("datasets", refParts)
		val application = readReference("applications", refParts)
		val system =  readReference("systems", refParts)
		return "system: $system | application: $application | dataType: $dataType"
	}

	private fun readReference(name: String, parts: List<String>): String {
		return parts[parts.lastIndexOf(name) + 1]
	}

	private fun normalizeType(type: String) = TypesConverter.convert(type)

}
