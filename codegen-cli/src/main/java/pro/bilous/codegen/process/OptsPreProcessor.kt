package pro.bilous.codegen.process

import org.apache.commons.lang3.tuple.Pair
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenConstants
import org.slf4j.LoggerFactory

class OptsPreProcessor(val codegen: CodeCodegen) {
	companion object {
		private val LOGGER = LoggerFactory.getLogger(OptsPreProcessor::class.java)

		const val BASE_PACKAGE = "basePackage"

		const val API_PACKAGE = "apiPackage"
		const val MODEL_PACKAGE = "modelPackage"
		const val CONFIG_PACKAGE = "configPackage"
		const val REPOSITORY_PACKAGE = "repositoryPackage"
		const val ENTITY_PACKAGE = "entityPackage"
		const val SERVICE_PACKAGE = "servicePackage"
		const val CONVERTER_PACKAGE = "converterPackage"
		const val MAPPER_PACKAGE = "mapperPackage"
		const val VALIDATION_PACKAGE = "validationPackage"
		const val EVENT_MAPPING = "hasEventMapping"
	}

	private val additionalProperties = codegen.additionalProperties()
	private val cliOptions = codegen.cliOptions()

	fun process() {
		val configOptions = additionalProperties.entries
				.filter { e -> !arrayOf("hideGenerationTimestamp").contains(e.key) }
				.filter { e -> cliOptions.map{ it.opt }.any { opt -> opt == e.key } }
				.map { e -> Pair.of(e.key, e.value.toString()) }
				.toList()
		additionalProperties["configOptions"] = configOptions

		if (!additionalProperties.containsKey(BASE_PACKAGE) && additionalProperties.containsKey(CodegenConstants.INVOKER_PACKAGE)) {
			// set invokerPackage as basePackage:
			codegen.basePackage = additionalProperties[CodegenConstants.INVOKER_PACKAGE] as String
			additionalProperties[BASE_PACKAGE] = codegen.basePackage
			LOGGER.info("Set base package to invoker package (${codegen.basePackage})")
		}

		codegen.basePackage = additionalProperties.getOrDefault(BASE_PACKAGE, codegen.basePackage).toString()

		if (codegen.entityMode) {
			setDefaultPackageProperty(API_PACKAGE, "repository") { codegen.setApiPackage(it) }
			setDefaultPackageProperty(MODEL_PACKAGE, "domain") { codegen.setModelPackage(it) }
		} else {
			setDefaultPackageProperty(API_PACKAGE, "controllers") { codegen.setApiPackage(it) }
			setDefaultPackageProperty(MODEL_PACKAGE, "restmodel") { codegen.setModelPackage(it) }
		}

		setDefaultPackageProperty(REPOSITORY_PACKAGE, "repository") { codegen.repositoryPackage = it }
		setDefaultPackageProperty(CONFIG_PACKAGE, "configuration") { codegen.setConfigPackage(it) }
		setDefaultPackageProperty(REPOSITORY_PACKAGE, "repository") {codegen.repositoryPackage = it}
		setDefaultPackageProperty(ENTITY_PACKAGE, "domain") {
			codegen.entityPackage = it
		}
		setDefaultPackageProperty(SERVICE_PACKAGE, "services") {codegen.servicePackage = it}
		setDefaultPackageProperty(CONVERTER_PACKAGE, "converters") {codegen.converterPackage = it}
		setDefaultPackageProperty(MAPPER_PACKAGE, "mappers") {codegen.mapperPackage = it}
		setDefaultPackageProperty(VALIDATION_PACKAGE, "validation.rules") {codegen.validationPackage = it}

		if (additionalProperties.containsKey(EVENT_MAPPING)) {
			codegen.hasEventMapping = additionalProperties[EVENT_MAPPING].toString().toBoolean()
		} else {
			codegen.hasEventMapping = true
			additionalProperties[EVENT_MAPPING] = codegen.hasEventMapping
		}
	}

	private fun setDefaultPackageProperty(key: String, suffix: String, function: (String) -> Unit) {
		val basePackage = codegen.additionalProperties()["appPackage"]
		if (!additionalProperties.containsKey(key)) {
			val propertyValue = "$basePackage.$suffix"
			additionalProperties[key] = "$basePackage.$suffix"
			function(propertyValue)
		} else {
			function(additionalProperties[key].toString())
		}
	}
}
