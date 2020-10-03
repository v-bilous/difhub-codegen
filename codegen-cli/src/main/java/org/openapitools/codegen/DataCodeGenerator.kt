package org.openapitools.codegen

class DataCodeGenerator(private val specIndex: Int = 0) : InCodeGenerator() {

	init {
		setGeneratorPropertyDefault(CodegenConstants.APIS, java.lang.Boolean.TRUE.toString())
		setGeneratorPropertyDefault(CodegenConstants.MODELS, java.lang.Boolean.TRUE.toString())
		setGeneratorPropertyDefault(CodegenConstants.SUPPORTING_FILES, java.lang.Boolean.TRUE.toString())
		setGeneratorPropertyDefault(CodegenConstants.MODEL_TESTS, java.lang.Boolean.FALSE.toString())
		setGeneratorPropertyDefault(CodegenConstants.MODEL_DOCS, java.lang.Boolean.FALSE.toString())
		setGeneratorPropertyDefault(CodegenConstants.API_TESTS, java.lang.Boolean.TRUE.toString())
		setGeneratorPropertyDefault(CodegenConstants.API_DOCS, java.lang.Boolean.FALSE.toString())
	}

	override fun opts(opts: ClientOptInput): Generator {
		val appRoot = "app-module/src/main/kotlin"
		val testRoot = "app-module/src/test/kotlin"

		val config = opts.config as CodeCodegen
		config.entityMode = true
		config.setModelNameSuffix("")
		config.modelTemplateFiles.remove("model.mustache")
		config.modelTemplateFiles["$appRoot/domain/entity.mustache"] = ".kt"

		config.apiTemplateFiles.remove("api.mustache")
		config.apiTemplateFiles.remove("apiController.mustache")

		config.apiTemplateFiles["$appRoot/controller/api.mustache"] = "Api.kt"
		config.apiTemplateFiles["$appRoot/controller/apiController.mustache"] = "Controller.kt"

		config.apiTemplateFiles["$appRoot/repository/repository.mustache"] = "Repository.kt"
		config.apiTemplateFiles["$appRoot/service/service.mustache"] = "Service.kt"

		config.apiTestTemplateFiles.clear()
		config.apiTestTemplateFiles["$testRoot/controller/apiTest.kt.mustache"] = ".kt"
//		config.apiTemplateFiles["resources/mapper.mustache"] = ".kt"
//		config.apiTemplateFiles["resources/converter.mustache"] = ".kt"
//		config.apiTemplateFiles["resources/validationRules.mustache"] = ".kt"

//		config.setApiSuffix("Repository")

		config.specIndex = specIndex

		return super.opts(opts)
	}

}
