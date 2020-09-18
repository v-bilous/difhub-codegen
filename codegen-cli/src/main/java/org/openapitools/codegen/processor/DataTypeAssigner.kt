package org.openapitools.codegen.processor

internal interface DataTypeAssigner {
	fun setReturnType(returnType: String)
	fun setReturnContainer(returnContainer: String)
}
