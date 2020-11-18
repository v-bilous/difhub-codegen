package pro.bilous.codegen.utils

object SuperclassRegistry {
	val modelNames = setOf("BaseResource")

	fun hasName(name: String): Boolean {
		return modelNames.contains(name)
	}
}
