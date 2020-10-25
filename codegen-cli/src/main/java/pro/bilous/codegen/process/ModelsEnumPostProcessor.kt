package pro.bilous.codegen.process

import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenModel
import pro.bilous.codegen.utils.CamelCaseConverter
import java.util.HashMap

class ModelsEnumPostProcessor(val codegen: CodeCodegen) {

	fun process(objs: Map<String, Any>): Map<String, Any> {
		val imports = objs["imports"] as MutableList<Map<String, String>>
		val models = objs["models"] as MutableList<Any>
		for (_mo in models) {
			val mo = _mo as Map<String, Any>
			val cm = mo["model"] as CodegenModel
			// for enum model
			if (java.lang.Boolean.TRUE == cm.isEnum && cm.allowableValues != null) {
				cm.imports.add(codegen.getImportMappings()["JsonValue"])
				val item = HashMap<String, String>()
				item["import"] = codegen.getImportMappings()["JsonValue"].toString()
				imports.add(item)
				codegen.metadataEnums[cm.classFilename] = cm
				cm.vendorExtensions["isMetadataEnum"] = true
				val className = cm.classname.replace("_", "").replace(" ", "").removeSuffix("Model")
				cm.vendorExtensions["enumGroupName"] = CamelCaseConverter.convert(className)
				cm.isAlias = true
			}
		}
		return objs
	}
}
