package pro.bilous.codegen.process.models

import com.google.common.base.CaseFormat
import org.openapitools.codegen.CodegenModel
import pro.bilous.codegen.utils.SqlNamingUtils
import pro.bilous.codegen.utils.SuperclassRegistry

class ModelStrategyResolver(val model: CodegenModel) : IModelStrategyResolver {

	companion object {
		val importsToIgnore = arrayOf(
			"ApiModel", "ApiModelProperty", "JsonProperty", "Entity", "ResourceEntity", "Identity", "Property"
		)
	}

	override fun resolveParent(args: Args) {
		val extensions = model.vendorExtensions
		when {
			args.hasEntity -> {
				val parent = "BaseResource()"
				model.parent = parent
				model.imports.add("BaseResource")

				model.vars = model.vars.filter { "identity" != it.name && "entity" != it.name }
				extensions["hasTableEntity"] = true
			}
			args.hasId -> {
				model.parent = "BaseDomain()"
				model.imports.add("BaseDomain")
				model.vars = model.vars.filter { "id" != it.name && "identity" != it.name }
			}
			args.hasIdentity -> {
				model.parent = "BaseDomain()"
				model.imports.add("BaseDomain")
				// we are unable to support identity for now. So, just remove the field and add ID instead of it.
				model.vars = model.vars.filter { "id" != it.name && "identity" != it.name }
			}
			args.hasExtends -> {
				val extendProperty = model.requiredVars.find { it.name == "_extends" }!!
				val parentType = extendProperty.complexType
				model.parent = "${parentType}()"
				model.imports.add(parentType)
			}
		}
	}

	override fun cleanupImports() {
		importsToIgnore.forEach {
			model.imports.remove(it)
		}
	}

	override fun buildArgs(): Args {
		return Args(
			hasEntity = model.vars.any { "entity" == it.name.toLowerCase() },
			hasIdentity = model.vars.any { "identity" == it.name.toLowerCase() },
			hasId = model.vars.any { "id" == it.name.toLowerCase() } && model.name != "Identity",
			hasExtends = model.requiredVars.any { "_extends" == it.name.toLowerCase() }
		)
	}

	override fun addExtensions(args: Args) {
		val extensions = model.vendorExtensions
		if (!args.hasEntity && args.hasIdentity) {
			extensions["hasTableEntity"] = false
		}
		val tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, model.name)
		extensions["tableName"] = SqlNamingUtils.escapeSqlNameIfNeeded(tableName)
		extensions["isEmbeddable"] = !args.hasEntity && !args.hasIdentity && !args.hasId && !args.hasExtends
		extensions["addIdVar"] = false // !hasEntity && hasIdentity
		val isSuperclass = SuperclassRegistry.hasName(model.name)
		extensions["isSuperclass"] = isSuperclass
		extensions["hasIdentity"] = args.hasIdentity
		extensions["hasEntity"] = args.hasEntity
	}

	data class Args(
		val hasEntity: Boolean = false,
		val hasIdentity: Boolean = false,
		val hasId: Boolean = false,
		val hasExtends: Boolean = false
	)
}
