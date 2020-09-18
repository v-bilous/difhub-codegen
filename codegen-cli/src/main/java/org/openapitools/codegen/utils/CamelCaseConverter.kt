package org.openapitools.codegen.utils

import com.google.common.base.Joiner
import java.util.*

object CamelCaseConverter {

	private val joiner = Joiner.on("_")

	fun convert(s: String): String {
		val text = s.replace("_", "").replace(" ", "")
		val result = LinkedList<String>()
		for (w in text.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
			result.add(w)
		}
		return joiner.join(result).toUpperCase()
	}
}
