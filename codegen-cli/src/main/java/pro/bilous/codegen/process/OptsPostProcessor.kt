package pro.bilous.codegen.process

import com.google.common.base.CaseFormat
import com.samskivert.mustache.Mustache
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.SupportingFile
import org.openapitools.codegen.templating.mustache.CaseFormatLambda
import org.openapitools.codegen.templating.mustache.LowercaseLambda
import java.io.File
import java.util.regex.Matcher

class OptsPostProcessor(val codegen: CodeCodegen) {

	private val modelDocTemplateFiles = codegen.modelDocTemplateFiles()
	private val apiDocTemplateFiles = codegen.apiDocTemplateFiles()
	private val apiTemplateFiles = codegen.apiTemplateFiles()
	private val apiTestTemplateFiles = codegen.apiTestTemplateFiles()
	private val additionalProperties = codegen.additionalProperties()
	private val supportingFiles = codegen.supportingFiles()
	private val typeMapping = codegen.typeMapping()

	private val entityMode = codegen.entityMode

	private val mainIndex = codegen.specIndex == 0

	private val artifactId = codegen.artifactId

	private val basePackage: String
		get() = codegen.basePackage

	private val sourceFolder: String
		get() = codegen.sourceFolder

	private val configPackage: String?
		get() = codegen.getConfigPackage()

	private fun getModelFolder(): String = codegen.modelFolder
	private fun getDataManagerConfigurationFolder(): String = codegen.dataManagerConfigurationFolder
	private fun getRepositoryFolder(): String = codegen.repositoryFolder
	private fun getEntityFolder(): String = codegen.entityFolder
	private fun getApiFolder(): String = codegen.apiFolder
	private fun getServicesFolder(): String = codegen.servicesFolder
	private fun getDatabaseFolder(): String = codegen.databaseFolder

	fun processOpts() {
		// clear model and api doc template as this codegen
		// does not support auto-generated markdown doc at the moment
		//TODO: add doc templates
		modelDocTemplateFiles.remove("model_doc.mustache")
		apiDocTemplateFiles.remove("api_doc.mustache")

		if (additionalProperties.containsKey(CodeCodegen.DB_NAME)) {
			codegen.dbName = additionalProperties[CodeCodegen.DB_NAME] as String
		}

		if (additionalProperties.containsKey(CodeCodegen.TITLE)) {
			codegen.setTitle(additionalProperties[CodeCodegen.TITLE] as String)
		}

		if (additionalProperties.containsKey(CodeCodegen.BASE_PACKAGE)) {
			codegen.basePackage = additionalProperties[CodeCodegen.BASE_PACKAGE] as String
		} else {
			additionalProperties[CodeCodegen.BASE_PACKAGE] = codegen.basePackage
		}

		if (additionalProperties.containsKey(CodeCodegen.RESPONSE_WRAPPER)) {
			codegen.setResponseWrapper(additionalProperties[CodeCodegen.RESPONSE_WRAPPER] as String)
		}

		if (additionalProperties.containsKey(CodeCodegen.USE_TAGS)) {
			codegen.setUseTags(java.lang.Boolean.valueOf(additionalProperties[CodeCodegen.USE_TAGS].toString()))
		}

		if (additionalProperties.containsKey(CodeCodegen.IMPLICIT_HEADERS)) {
			codegen.setImplicitHeaders(java.lang.Boolean.valueOf(additionalProperties[CodeCodegen.IMPLICIT_HEADERS].toString()))
		}

		if (additionalProperties.containsKey(CodeCodegen.OPENAPI_DOCKET_CONFIG)) {
			codegen.setOpenapiDocketConfig(java.lang.Boolean.valueOf(additionalProperties[CodeCodegen.OPENAPI_DOCKET_CONFIG].toString()))
		}


		typeMapping["file"] = "Resource"
		OptsImportMappings(codegen).addDefaultMappings()

		val appRoot = "app-${artifactId.toLowerCase()}"

		if (mainIndex) {
			setupRootFiles()
			addCommonModuleFiles()
			setupRawFiles()
			setupIdeaFiles()
		}

		val baseResourceSrcFolder = "$appRoot/src/main/kotlin"
		val baseResourceResFolder = "$appRoot/src/main/resources"
		val baseResourceIntegrationTestFolder = "$appRoot.src.integration-test.resources"

		val inputAppRoot = "app-module"
		val appPackage = additionalProperties["appPackage"]
		val appName = additionalProperties["appRealName"]

		val openApiWrapper = OpenApiWrapper(codegen)
		val hasEntityBlocks = openApiWrapper.isOpenApiContainsType("Entity")
		val hasIdentityBlocks = openApiWrapper.isOpenApiContainsType("Identity")
		additionalProperties["hasEntityBlocks"] = hasEntityBlocks
		additionalProperties["hasIdentityBlocks"] = hasIdentityBlocks


		val inputSrcRoot = "$inputAppRoot/src/main/kotlin"
		val inputResRoot = "$inputAppRoot/src/main/resources"
		addSupportFile(source = "$inputSrcRoot/springBootApplication.mustache", folder = "$baseResourceSrcFolder.$appPackage", target = "${appName}Application.kt")

		//apiTemplateFiles["$inputSrcRoot/controller/apiController.mustache"] = "Controller.java"
		//apiTestTemplateFiles["resources/integration-test/apiTest.mustache"] = ".kt"

		additionalProperties["javaVersion"] = "1.8"

		val inputTest = "common/src/test/kotlin/"
		val destTest = "$appRoot/src/test/kotlin/$appPackage"

		addSupportFile(source = "$inputTest/controller/AbstractIntegrationTest.kt.mustache", folder = "$destTest/controller", target = "AbstractIntegrationTest.kt")
		addSupportFile(source = "$inputTest/controller/CommonIntegrationTest.kt.mustache", folder = "$destTest/controller", target = "CommonIntegrationTest.kt")

		val inputResTest = "app-module/src/test/resources/"
		val destResTest = "$appRoot/src/test/resources/"

		addSupportFile(source = "$inputResTest/application.yml.mustache", folder = destResTest, target = "application.yml")
		addSupportFile(source = "$inputResTest/application-testcontainers.yml.mustache", folder = destResTest, target = "application-testcontainers.yml")

		setupModuleFiles()

		//addSupportFile("resources/errorCode-default.mustache", baseResourceResFolder, "errorCode-default.json")

		val integrationTestConfigFolder = codegen.getIntegrationTestFolder(basePackage, "-resources")

		addSupportFile("$inputResRoot/application.yml.mustache", baseResourceResFolder, "application.yml")
		addSupportFile("$inputResRoot/application-dev.yml.mustache", baseResourceResFolder, "application-dev.yml")
		addSupportFile("$inputResRoot/application-prod.yml.mustache", baseResourceResFolder, "application-prod.yml")
		if (isAuthorizationEnabled()) {
			addSupportFile("$inputResRoot/application-security.yml.mustache", baseResourceResFolder, "application-security.yml")
		}
		addSupportFile(source = "$inputResRoot/hibernate-types.properties", folder = baseResourceResFolder, target = "hibernate-types.properties")

//		addSupportFile("resources/integration-test/configurationIT.mustache", "$integrationTestConfigFolder/config", "ConfigurationIT.kt")
//		addSupportFile("resources/integration-test/mockBeansConfigIT.mustache", "$integrationTestConfigFolder/config", "MockBeansConfigIT.kt")
//		addSupportFile("resources/integration-test/application.mustache", "$baseResourceIntegrationTestFolder/config", "application.yml")

		additionalProperties["lower"] = LowercaseLambda()
		additionalProperties["upperCamelToUpperUnderscore"] = CaseFormatLambda(CaseFormat.UPPER_CAMEL, CaseFormat.UPPER_UNDERSCORE)

		if (entityMode) {
			//supportingFiles.clear()

			val inputRoot = "app-module/src/main/resources/liquibase"
			val destinationRoot = "app-${artifactId.toLowerCase()}/src/main/resources/liquibase"

			addSupportFile(source = "$inputRoot/liquibase-changeLog.xml", target = "$destinationRoot/liquibase-changeLog.xml")
			addSupportFile(source = "$inputRoot/settings.xml.mustache", target = "$destinationRoot/settings.xml")
			addSupportFile(source = "$inputRoot/migrations/changeLog.mustache", target = "$destinationRoot/migrations/generatedChangeLog.xml")
//			addSupportFile(source = "$inputRoot/migrations/common_tables.xml.mustache", target = "$destinationRoot/migrations/common_tables.xml")
			addSupportFile(source ="$inputRoot/migrations/metadata_data.xml", target = "$destinationRoot/migrations/metadata_data.xml")
			addSupportFile(source ="$inputRoot/migrations/metadata_csv.mustache", target = "$destinationRoot/migrations/metadata.csv")
		}
		// add lambda for mustache templates
		additionalProperties["lambdaEscapeDoubleQuote"] = Mustache.Lambda { fragment, writer -> writer.write(fragment.execute().replace("\"".toRegex(), Matcher.quoteReplacement("\\\""))) }
		additionalProperties["lambdaRemoveLineBreak"] = Mustache.Lambda { fragment, writer -> writer.write(fragment.execute().replace("\\r|\\n".toRegex(), "")) }
	}

	private fun setupRootFiles() {
		addSupportFile(source = "build.gradle.kts.mustache", target = "build.gradle.kts")
		addSupportFile(source = "settings.gradle.kts.mustache", target = "settings.gradle.kts")
		addSupportFile(source = "gradlew", target = "gradlew")
		addSupportFile(source = "gradlew.bat", target = "gradlew.bat")
		addSupportFile(source = "docker-compose.yml.mustache", target = "docker-compose.yml")

		addSupportFile(source = "README.mustache", target = "README.md")
		addSupportFile(source = "gradle/wrapper/gradle-wrapper.properties", folder = "", target = "gradle/wrapper/gradle-wrapper.properties")

		addSupportFile(
			source = ".gitlab-ci.yml.mustache",
			target = ".gitlab-ci.yml",
			condition = cicdEnabled()
		)
	}

	private fun addCommonModuleFiles() {
		val commonRoot = "common"
		val inputSrc = "$commonRoot/src/main/kotlin/"
		val destSrc = "$commonRoot/src/main/kotlin/$basePackage"
		addSupportFile(source = "$commonRoot/build.gradle.kts.mustache", folder = commonRoot, target = "build.gradle.kts")
		addSupportFile(source = "$inputSrc/config/WebConfig.kt.mustache", folder = "$destSrc/config", target = "WebConfig.kt")
		addSupportFile(source = "$inputSrc/config/SpringFoxConfig.kt.mustache", folder = "$destSrc/config", target = "SpringFoxConfig.kt")
		addSupportFile(source = "$inputSrc/constant/EntityState.kt.mustache", folder = "$destSrc/constant", target = "EntityState.kt")
		addSupportFile(source = "$inputSrc/controller/AbstractController.kt.mustache", folder = "$destSrc/controller", target = "AbstractController.kt")
		addSupportFile(source = "$inputSrc/controller/CommonController.kt.mustache", folder = "$destSrc/controller", target = "CommonController.kt")
		addSupportFile(source = "$inputSrc/controller/CommonParameterizedController.kt.mustache", folder = "$destSrc/controller", target = "CommonParameterizedController.kt")
		if (!OpenApiWrapper(codegen).isOpenApiContainsType("BaseResource")) {
			addSupportFile(source = "$inputSrc/listener/BaseResourceListener.kt.mustache", folder = "$destSrc/listener", target = "BaseResourceListener.kt")
			addSupportFile(source = "$inputSrc/domain/BaseResource.kt.mustache", folder = "$destSrc/domain", target = "BaseResource.kt")
		}
		if (!OpenApiWrapper(codegen).isOpenApiContainsType("BaseDomain")) {
			addSupportFile(source = "$inputSrc/domain/BaseDomain.kt.mustache", folder = "$destSrc/domain", target = "BaseDomain.kt")
		}
//		addSupportFile(source = "$inputSrc/domain/ResourceEntity.kt.mustache", folder = "$destSrc/domain", target = "ResourceEntity.kt")
//		addSupportFile(source = "$inputSrc/domain/History.kt.mustache", folder = "$destSrc/domain", target = "History.kt")
//		addSupportFile(source = "$inputSrc/domain/Identity.kt.mustache", folder = "$destSrc/domain", target = "Identity.kt")
		addSupportFile(source = "$inputSrc/exception/InvalidRequestException.kt.mustache", folder = "$destSrc/exception", target = "InvalidRequestException.kt")
		addSupportFile(source = "$inputSrc/exception/ResourceNotFoundException.kt.mustache", folder = "$destSrc/exception", target = "ResourceNotFoundException.kt")
		addSupportFile(source = "$inputSrc/listener/BaseDomainListener.kt.mustache", folder = "$destSrc/listener", target = "BaseDomainListener.kt")
		addSupportFile(source = "$inputSrc/repository/CommonRepository.kt.mustache", folder = "$destSrc/repository", target = "CommonRepository.kt")
		addSupportFile(source = "$inputSrc/service/AbstractService.kt.mustache", folder = "$destSrc/service", target = "AbstractService.kt")
		addSupportFile(source = "$inputSrc/service/CommonService.kt.mustache", folder = "$destSrc/service", target = "CommonService.kt")
		if (isAuthorizationEnabled()) {
			addSupportFile(source = "$inputSrc/security/urlmapper/CommonUrlAccessMapper.kt.mustache", folder = "$destSrc/security/urlmapper", target = "CommonUrlAccessMapper.kt")
			addSupportFile(source = "$inputSrc/security/urlmapper/UrlAccessMapper.kt.mustache", folder = "$destSrc/security/urlmapper", target = "UrlAccessMapper.kt")
			addSupportFile(source = "$inputSrc/config/KeycloakConfig.kt.mustache", folder = "$destSrc/config", target = "KeycloakConfig.kt")
		}
	}

	private fun setupModuleFiles() {
		val inputRoot = "app-module/"
		val destinationRoot = "app-${artifactId.toLowerCase()}"
		addSupportFile(source = "$inputRoot/build.gradle.kts.mustache",  target = "$destinationRoot/build.gradle.kts")
		// add kubernetes manifests for the application
		addSupportFile(
			source = "kube/kube-app.yml.mustache",
			target = "kube/kube-${artifactId.toLowerCase()}.yml",
			condition = cicdEnabled()
		)
	}

	private fun setupRawFiles() {
		addSupportFile(source = "raw/_gitignore", target = ".gitignore")
		addSupportFile(source = "raw/_editorconfig", target = ".editorconfig")
	}

	private fun setupIdeaFiles() {
		addSupportFile(source = "idea/runConfiguration.mustache", target = ".idea/runConfigurations/Application.xml")
	}

	private fun addSupportFile(source: String, folder: String = "", target: String, condition: Boolean = true) {
		if (condition) {
			supportingFiles.add(SupportingFile(source, folder.replace(".", File.separator), target))
		}
	}

	private fun cicdEnabled() = additionalProperties.containsKey("cicd") && additionalProperties["cicd"] as Boolean

	private fun isAuthorizationEnabled(): Boolean {
		return true == additionalProperties.get(CodeCodegen.AUTHORIZATION_ENABLED) as Boolean?
	}

}
