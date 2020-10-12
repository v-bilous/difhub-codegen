package org.openapitools.codegen

import com.google.common.collect.Maps
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.servers.Server
import org.openapitools.codegen.languages.AbstractJavaCodegen
import org.openapitools.codegen.processor.OperationResponseResolver
import org.openapitools.codegen.processor.FromModelProcessor
import org.openapitools.codegen.processor.FromPropertyProcessor
import org.openapitools.codegen.processor.ModelPropertyProcessor
import org.openapitools.codegen.processor.ModelsEnumPostProcessor
import org.openapitools.codegen.processor.OpenApiProcessor
import org.openapitools.codegen.processor.OperationsWithModelsProcessor
import org.openapitools.codegen.processor.OptsPostProcessor
import org.openapitools.codegen.processor.OptsPreProcessor
import org.openapitools.codegen.utils.ModelUtils
import org.slf4j.LoggerFactory

import java.io.File

import org.openapitools.codegen.utils.StringUtils.camelize

open class CodeCodegen : AbstractJavaCodegen() {
	companion object {
		private val LOGGER = LoggerFactory.getLogger(CodeCodegen::class.java)

		const val TITLE = "title"
		const val SERVER_PORT = "serverPort"
		const val BASE_PACKAGE = "basePackage"
		const val RESPONSE_WRAPPER = "responseWrapper"
		const val USE_TAGS = "useTags"
		const val IMPLICIT_HEADERS = "implicitHeaders"
		const val OPENAPI_DOCKET_CONFIG = "swaggerDocketConfig"
		const val DB_NAME = "dbName"
		const val BINDING_KEY = "addBindingEntity"
	}

	fun getOpenApi() = openAPI

	open fun findOpenApi() = openAPI

	private var modelPropertyProcessor: ModelPropertyProcessor
	private var title = "RESTful Application"

	fun getTitle() = title

//	override fun embeddedTemplateDir(): String {
//		return "$templateDir-embed"
//	}

	var basePackage = "code"
	private var configPackage: String? = null
	fun getConfigPackage() = configPackage
	fun setConfigPackage(it: String) {
		configPackage = it
	}

	var repositoryPackage: String? = null
	var entityPackage: String? = null
	var converterPackage: String? = null
	var servicePackage: String? = null
	var mapperPackage: String? = null
	var validationPackage: String? = null

	private var responseWrapper = ""

	fun getResponseWrapper() = responseWrapper

	private var useTags = true
	private var implicitHeaders = false
	fun isImplicitHeader() = implicitHeaders

	private var openapiDocketConfig = false
	var enableSubModules = true

	var hasEventMapping = true

	private var apiSuffix = "Api"
	var entityMode = false
	var dbName = "servicedb"

	var specIndex: Int = 0

	val metadataEnums = Maps.newHashMap<String, CodegenModel>()

	val modelFolder: String
		get() = if (entityMode) entityFolder else getFolder(modelPackage, "-domain-model")

	val apiFolder: String
		get() = if (entityMode) repositoryFolder else getFolder(apiPackage, "-resources")

	val apiTestFolder: String
		get() = getTestFolder(apiPackage, "-resources")

	val apiIntegrationTestFolded: String
		get() = getIntegrationTestFolder(apiPackage, "-resources")

	val repositoryFolder: String
		get() = getFolder(repositoryPackage, "-data-manager")

	val entityFolder: String
		get() = getFolder(entityPackage, "-data-manager")

	val apiConfigurationFolder: String
		get() = getFolder(configPackage, "-resources")

	val dataManagerConfigurationFolder: String
		get() = getFolder(configPackage, "-data-manager")

	val servicesFolder: String
		get() = getFolder("$basePackage.services", "-services")

	val databaseFolder: String
		get() {
			val subFolder = if (enableSubModules) "$artifactId-database" else ""
			return (subFolder + File.separator + "src/main/resources").replace(".", File.separator)
		}

	init {

		outputFolder = "generated-code/javaSpring"
		templateDir = "kotlin-codegen"
		embeddedTemplateDir = templateDir
		apiPackage = "$basePackage.controller"
		modelPackage = "$basePackage.restmodel"
		invokerPackage = "$basePackage.controllers"
		artifactId = "service-management"
		modelNameSuffix = if (entityMode) "" else "Model"

		// clioOptions default redifinition need to be updated
		updateOption(CodegenConstants.INVOKER_PACKAGE, this.getInvokerPackage())
		updateOption(CodegenConstants.ARTIFACT_ID, this.getArtifactId())
		updateOption(CodegenConstants.API_PACKAGE, apiPackage)
		updateOption(CodegenConstants.MODEL_PACKAGE, modelPackage)
		updateOption(CodegenConstants.MODEL_NAME_SUFFIX, modelNameSuffix)

		// spring uses the jackson lib
		additionalProperties["jackson"] = "true"

		cliOptions.add(CliOption(TITLE, "server title name or client service name").defaultValue(title))
		cliOptions.add(CliOption(BASE_PACKAGE, "base package (invokerPackage) for generated code").defaultValue(this.basePackage))
		cliOptions.add(CliOption(RESPONSE_WRAPPER, "wrap the responses in given type (Future,Callable,CompletableFuture,ListenableFuture,DeferredResult,HystrixCommand,RxObservable,RxSingle or fully qualified type)"))
		cliOptions.add(CliOption.newBoolean(USE_TAGS, "use tags for creating interface and controller classnames", useTags))
		cliOptions.add(CliOption.newBoolean(IMPLICIT_HEADERS, "Skip header parameters in the generated API methods using @ApiImplicitParams annotation.", implicitHeaders))
		cliOptions.add(CliOption.newBoolean(OPENAPI_DOCKET_CONFIG, "Generate Spring OpenAPI Docket configuration class.", openapiDocketConfig))
		//cliOptions.add(CliOption.newBoolean(SUB_MODULES, "Generate sub modules", enableSubModules));
		cliOptions.add(CliOption(DB_NAME, "database name for generated code").defaultValue(dbName))
		cliOptions.add(CliOption.newBoolean(BINDING_KEY, "add Binding entity support to the service", false))
		modelPropertyProcessor = ModelPropertyProcessor(this)
	}

	public override fun findMethodResponse(responses: ApiResponses): ApiResponse? {
		return super.findMethodResponse(responses)
	}

	override fun getTag() = CodegenType.SERVER
	override fun getName() = "bhn-codegen"
	override fun getHelp() = "Generates a Java SpringBoot Server application using the SpringFox integration."

	override fun processOpts() {
		OptsPreProcessor(this).process()
		super.processOpts()
		OptsPostProcessor(this).processOpts()
	}

	override fun addOperationToGroup(tag: String, resourcePath: String?, operation: Operation?, co: CodegenOperation, operations: MutableMap<String, List<CodegenOperation>>) {
		if (!useTags) {
			var basePath = resourcePath
			if (basePath!!.startsWith("/")) {
				basePath = basePath.substring(1)
			}
			val pos = basePath.indexOf("/")
			if (pos > 0) {
				basePath = basePath.substring(0, pos)
			}

			if (basePath == "") {
				basePath = "default"
			} else {
				co.subresourceOperation = co.path.isNotEmpty()
			}
			val opList = operations.computeIfAbsent(basePath) { listOf() }.toMutableList()
			opList.add(co)
			co.baseName = basePath
		} else {
			super.addOperationToGroup(tag, resourcePath, operation, co, operations)
		}
	}

	override fun preprocessOpenAPI(openAPI: OpenAPI?) {
		super.preprocessOpenAPI(openAPI)
		OpenApiProcessor(this).preprocessOpenAPI(openAPI!!)
	}

	override fun postProcessOperationsWithModels(objs: MutableMap<String, Any>, allModels: List<Any>?): Map<String, Any> {
		return OperationsWithModelsProcessor(this).postProcessOperationsWithModels(objs, allModels!!)
	}

	override fun postProcessSupportingFileData(objs: MutableMap<String, Any>): Map<String, Any> {
		generateYAMLSpecFile(objs)
		objs["metadataEnums"] = metadataEnums.values
		return objs
	}

	override fun toApiName(name: String): String {
		if (name.isEmpty()) {
			return "Default$apiSuffix"
		}
		return camelize(sanitizeName(name)) + apiSuffix
	}

	override fun setParameterExampleValue(p: CodegenParameter) {
		var type: String? = p.baseType
		if (type == null) {
			type = p.dataType
		}
		if ("File" == type) {
			var example = if (p.defaultValue == null) p.example else p.defaultValue

			if (example == null) {
				example = "/path/to/file"
			}
			example = "new org.springframework.core.io.FileSystemResource(new java.io.File(\"" + escapeText(example) + "\"))"
			p.example = example
		} else {
			super.setParameterExampleValue(p)
		}
	}

	fun setTitle(title: String) {
		this.title = title
	}

	fun setResponseWrapper(responseWrapper: String) {
		this.responseWrapper = responseWrapper
	}

	fun setUseTags(useTags: Boolean) {
		this.useTags = useTags
	}

	fun setImplicitHeaders(implicitHeaders: Boolean) {
		this.implicitHeaders = implicitHeaders
	}

	fun setOpenapiDocketConfig(openapiDocketConfig: Boolean) {
		this.openapiDocketConfig = openapiDocketConfig
	}

	fun setApiSuffix(apiSuffix: String) {
		this.apiSuffix = apiSuffix
	}

	override fun fromModel(name: String, model: Schema<*>): CodegenModel {
		return FromModelProcessor(this).process(super.fromModel(name, model))
	}

	override fun fromProperty(name: String?, p: Schema<*>?): CodegenProperty {
		return FromPropertyProcessor(this).process(name, p, super.fromProperty(name, p))
	}

	override fun fromOperation(path: String, httpMethod: String, operation: Operation, servers: List<Server>?): CodegenOperation {
		val codegenOperation = super.fromOperation(path, httpMethod, operation, servers)

		// try to fetch baseType with DifHub issue (wrong model)
		if (codegenOperation.returnBaseType == null) {
			OperationResponseResolver(this).resolve(operation, codegenOperation)
		}
		codegenOperation.imports.remove("Error")
		codegenOperation.imports.remove("List")

		return codegenOperation
	}

	override fun postProcessModelProperty(model: CodegenModel, property: CodegenProperty?) {
		super.postProcessModelProperty(model, property)
		ModelPropertyProcessor(this).postProcessModelProperty(model, property!!)
	}

	fun getImportMappings() = importMapping

	override fun postProcessModelsEnum(objs: Map<String, Any>): Map<String, Any> {
		return ModelsEnumPostProcessor(this).process(super.postProcessModelsEnum(objs))
	}

	override fun getSchemaType(p: Schema<*>?): String {
		val schema = ModelUtils.getReferencedSchema(openAPI, p)
		if (schema.type == "string" && schema.enum != null && schema.enum.isNotEmpty()) {
			return super.getSchemaType(p).removeSuffix(modelNameSuffix)
		}
		return super.getSchemaType(p)
	}

	override fun apiFilename(templateName: String, tag: String): String {
		val suffix = apiTemplateFiles()[templateName]

		val outSrc = "$outputDir/app-$artifactId/src/main/kotlin"

		val appPackage = additionalProperties["appPackage"]

		val result: String = if (templateName.endsWith("/service.mustache")) {
			"$outSrc/$appPackage/service/${tag}"
		} else if (templateName.endsWith("/repository.mustache")) {
			"$outSrc/$appPackage/repository/${tag}"
		} else if (templateName.endsWith("/api.mustache")) {
			"$outSrc/$appPackage/controller/api/${tag}"
		} else if (templateName.endsWith("/apiController.mustache")) {
			"$outSrc/$appPackage/controller/${tag}"
		} else {
			apiFileFolder() + File.separator + toApiFilename(tag)
		}
//		if (templateName == "resources/mapper.mustache") {
//			return this.outputFolder + File.separator + getFolder("$basePackage.mapper", "-resources") + File.separator + tag + "Mapper" + suffix
//		}
//		if (templateName == "resources/converter.mustache") {
//			return this.outputFolder + File.separator + getFolder("$basePackage.converters", "-resources") + File.separator + tag + "Converter" + suffix
//		}
//		return if (templateName == "resources/validationRules.mustache") {
//			this.outputFolder + File.separator + getFolder("$basePackage.validation.rules", "-resources") + File.separator + tag + "ValidationRule" + suffix
//		} else
		return result.replace(".", File.separator) + suffix
	}

	override fun apiFileFolder(): String {
		return (this.outputFolder + File.separator + apiFolder).replace('/', File.separatorChar)
	}

	override fun apiTestFileFolder(): String {
		return (this.outputFolder + File.separator + apiTestFolder).replace('/', File.separatorChar)
	}

	fun apiIntegrationTestFolder(): String {
		return (this.outputFolder + File.separator + apiIntegrationTestFolded).replace('/', File.separatorChar)
	}

	override fun modelFileFolder(): String {
		return (this.outputFolder + File.separator + modelFolder).replace('/', File.separatorChar)
	}

	override fun apiTestFilename(templateName: String, tag: String): String {
		val suffix = apiTestTemplateFiles()[templateName]
		val fileFolder = if (templateName.startsWith("resources/integration-test")) {
			apiIntegrationTestFolder()
		} else apiTestFileFolder()
		return fileFolder + File.separator + toApiTestFilename(tag) + suffix
	}

	override fun toApiTestFilename(name: String): String {
		return "${toApiName(name)}IT"
	}

	override fun toModelFilename(name: String): String {
		val fileName = super.toModelFilename(name)
		return if (metadataEnums.containsKey(fileName)) fileName.removeSuffix(modelNameSuffix) else fileName
	}

	private fun getFolder(sourcePackage: String?, subModule: String): String {
		val subFolder = "app-$artifactId"
		return (subFolder + File.separator + "src/main/kotlin" + File.separator + sourcePackage).replace(".", File.separator)
	}

	fun getTestFolder(sourcePackage: String?, subModule: String): String {
		val subFolder = "app-$artifactId"
		val rightSourcePkg = sourcePackage?.replace("repository", "controller")
		return (subFolder + File.separator + "src/test/kotlin" + File.separator + rightSourcePkg).replace(".", File.separator)
	}

	fun getIntegrationTestFolder(sourcePackage: String, subModule: String): String {
		val subFolder = if (enableSubModules) artifactId + subModule else ""
		return (subFolder + File.separator + "src/integration-test/kotlin" + File.separator + sourcePackage).replace(".", File.separator)

	}
}
