package org.openapitools.codegen.processor

import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.CodegenParameter
import java.util.ArrayList

class OperationsWithModelsProcessor(val codegen: CodeCodegen) {

	@Suppress("UNCHECKED_CAST")
	fun postProcessOperationsWithModels(objs: MutableMap<String, Any>, allModels: List<Any>):  Map<String, Any> {
		val operations = objs["operations"] as MutableMap<String, Any>
		val ops = operations["operation"] as MutableList<CodegenOperation>
		for (operation in ops) {
			val responses = operation.responses
			if (responses != null) {
				for (resp in responses) {
					if ("0" == resp.code) {
						resp.code = "200"
					}
					doDataTypeAssignment(resp.dataType, object : DataTypeAssigner {
						override fun setReturnType(returnType: String) {
							resp.dataType = returnType
						}
						override fun setReturnContainer(returnContainer: String) {
							resp.containerType = returnContainer
						}
					})
				}
			}

			doDataTypeAssignment(operation.returnType, object : DataTypeAssigner {
				override fun setReturnType(returnType: String) {
					operation.returnType = returnType
				}
				override fun setReturnContainer(returnContainer: String) {
					operation.returnContainer = returnContainer
				}
			})
			if (codegen.isImplicitHeader()) {
				removeHeadersFromAllParams(operation.allParams)
			}
		}
		OperationAddon(codegen).populate(objs)

		return objs
	}

	/**
	 * @param returnType The return type that needs to be converted
	 * @param dataTypeAssigner An object that will assign the data to the respective fields in the model.
	 */
	private fun doDataTypeAssignment(returnType: String?, dataTypeAssigner: DataTypeAssigner) {
		if (returnType == null) {
			dataTypeAssigner.setReturnType("Void")
		} else if (returnType.startsWith("List")) {
			val end = returnType.lastIndexOf(">")
			if (end > 0) {
				dataTypeAssigner.setReturnType(returnType.substring("List<".length, end).trim { it <= ' ' })
				dataTypeAssigner.setReturnContainer("List")
			}
		} else if (returnType.startsWith("Map")) {
			val end = returnType.lastIndexOf(">")
			if (end > 0) {
				dataTypeAssigner.setReturnType(returnType.substring("Map<".length, end).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' })
				dataTypeAssigner.setReturnContainer("Map")
			}
		} else if (returnType.startsWith("Set")) {
			val end = returnType.lastIndexOf(">")
			if (end > 0) {
				dataTypeAssigner.setReturnType(returnType.substring("Set<".length, end).trim { it <= ' ' })
				dataTypeAssigner.setReturnContainer("Set")
			}
		}
	}

	/**
	 * This method removes header parameters from the list of parameters and also
	 * corrects last allParams hasMore state.
	 * @param allParams list of all parameters
	 */
	private fun removeHeadersFromAllParams(allParams: MutableList<CodegenParameter>) {
		if (allParams.isEmpty()) {
			return
		}
		val copy = ArrayList(allParams)
		allParams.clear()

		for (p in copy) {
			if (!p.isHeaderParam) {
				allParams.add(p)
			}
		}
		if (allParams.isNotEmpty()) {
			allParams[allParams.size - 1].hasMore = false
		}
	}
}
