package pro.bilous.codegen.process.filename

import org.openapitools.codegen.CodegenModel

data class ModelFileNameArgs(
	val metadataEnums: Map<String, CodegenModel>,
	val modelNameSuffix: String
)
