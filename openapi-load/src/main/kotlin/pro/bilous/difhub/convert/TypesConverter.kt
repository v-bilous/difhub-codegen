package pro.bilous.difhub.convert

object TypesConverter {

	private val genericType = Type("object")

	// TODO makes sense to take type map from difhub, hence there is no 100% valid model for the swagger.
	private val typesMap = mapOf(
			"Structure" to Type("object"),
			"Reference" to Type("string", "reference"),
			"String" to Type("string"),
			"Guid" to Type("string", "uuid"),
			"Decimal" to Type("number", "decimal"),
			"Integer" to Type("integer"),
			"Short" to Type("integer", "short"),
			"DateTime" to Type("string", "date-time"),
			"Enum" to Type("string"),
			"Money" to Type("number", "money"),
			"Long" to Type("integer", "int64"),
			"Boolean" to Type("boolean"),
			"Text" to Type("string")
	)

	fun convert(type: String): Type {
		return if (typesMap.containsKey(type)) typesMap[type]!! else genericType
	}
}

data class Type(
		var type: String = "",
		val format: String? = null
)
