package pro.bilous.codegen.process.models

interface IModelStrategyResolver {
	fun resolveParent(args: ModelStrategyResolver.Args)
	fun cleanupImports()
	fun buildArgs(): ModelStrategyResolver.Args
	fun addExtensions(args: ModelStrategyResolver.Args)
}
