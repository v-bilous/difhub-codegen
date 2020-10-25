package pro.bilous.codegen.process

internal interface DataTypeAssigner {
	fun setReturnType(returnType: String)
	fun setReturnContainer(returnContainer: String)
}
