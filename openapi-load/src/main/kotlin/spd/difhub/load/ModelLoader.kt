package spd.difhub.load

import spd.difhub.model.Model
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.swagger.util.Json

class ModelLoader(private val defLoader: DefLoader) {

	companion object {
		val globalModelCache = mutableMapOf<String, String>()
	}

	init {
		Json.mapper().registerKotlinModule()
	}

	fun loadModel(reference: String): Model? {
		// remove the version suffix to always get the latest one.
		var fixedRef = reference
		if (fixedRef.contains("/versions/")) {
			fixedRef = reference.split("/").dropLast(2).joinToString("/")
		}
		val text = loadString(fixedRef)
		return if (text.isNullOrEmpty()) null else try {
			Json.mapper().readValue<Model>(text)
		} catch (e: MismatchedInputException) {
			System.err.println("Failed when model loading: $reference")
			println(e)
			null
		}
	}

	fun loadModels(reference: String): List<Model>? {
		val text = loadString(reference)
		return if (text.isNullOrEmpty()) null else Json.mapper().readValue<List<Model>>(text)
	}

	private inline fun <reified T> ObjectMapper.readValue(json: String): T
			= readValue(json, object : TypeReference<T>(){})

	private fun loadString(reference: String): String? {
		val fixedReference = reference.replace("//", "/")

		if (globalModelCache.containsKey(fixedReference)) {
			return globalModelCache[fixedReference]
		}
		val sourceText = defLoader.load(fixedReference)
		if (sourceText == null || sourceText.contains("EntityForbiddenException")) {
			return null
		}
		globalModelCache[fixedReference] = sourceText
		return sourceText
	}
}
