package pro.bilous.codegen.process

import com.google.common.base.CaseFormat
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
import org.slf4j.LoggerFactory
import pro.bilous.codegen.process.models.CommonModelsProcessor

class FromModelProcessor(val codegen: CodeCodegen) {
	companion object {
		private val LOGGER = LoggerFactory.getLogger(FromModelProcessor::class.java)
	}

	fun process(codegenModel: CodegenModel): CodegenModel {
		codegenModel.vars = codegenModel.vars.filter { !it.name.startsWith("_") }
		val hasEntity = codegenModel.vars.any { "entity" == it.name.toLowerCase() }
		val hasIdentity = codegenModel.vars.any { "identity" == it.name.toLowerCase() }
		val hasId = codegenModel.vars.any { "id" == it.name.toLowerCase() } && codegenModel.name != "Identity"
		val extensions = codegenModel.vendorExtensions

		codegenModel.imports.remove("Property")

		if (hasEntity) {
			val parent = if (codegen.entityMode) "BaseResource()" else "ProjectBaseModel"
			codegenModel.parent = parent
			codegenModel.imports.add("BaseResource")

			codegenModel.vars = codegenModel.vars.filter { "identity" != it.name && "entity" != it.name }
			extensions["hasTableEntity"] = true
		} else if (hasId) {
			codegenModel.parent = "BaseDomain()"
			codegenModel.imports.add("BaseDomain")
			codegenModel.vars = codegenModel.vars.filter { "id" != it.name && "identity" != it.name }
		} else if (hasIdentity) {
			codegenModel.parent = "BaseDomain()"
			codegenModel.imports.add("BaseDomain")
			// we are unable to support identity for now. So, just remove the field and add ID instead of it.
			codegenModel.vars = codegenModel.vars.filter { "id" != it.name && "identity" != it.name }
		}
		if (codegen.entityMode) {
			codegenModel.imports.remove("ApiModel")
			codegenModel.imports.remove("ApiModelProperty")
			codegenModel.imports.remove("JsonProperty")
			codegenModel.imports.remove("Entity")
			codegenModel.imports.remove("ResourceEntity")
			codegenModel.imports.remove("Identity")

			if (!hasEntity && hasIdentity) {
//				val parent = "BaseRecord<String>"
//				codegenModel.imports.add(parent)
//				codegenModel.parent = parent
				extensions["hasTableEntity"] = false
			}
		}
		// do not add suffix to the Enum!
		if (codegenModel.isEnum) {
			LOGGER.debug("enum detected")
			codegenModel.classname = codegenModel.name
			//codegenModel.classFilename = codegenModel.name
		}
		val tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, codegenModel.name)
		extensions["tableName"] = tableName
		extensions["isEmbeddable"] = !hasEntity && !hasIdentity && !hasId
		extensions["addIdVar"] = false // !hasEntity && hasIdentity

		val properties = codegen.additionalProperties()
		CommonModelsProcessor(properties).process(model = codegenModel)

		// should introduce processor for the default values
		codegenModel.vars.forEach {
			if (it.required && it.defaultValue == "null") {
				it.defaultValue = null
			}
		}
		return codegenModel
	}
}
