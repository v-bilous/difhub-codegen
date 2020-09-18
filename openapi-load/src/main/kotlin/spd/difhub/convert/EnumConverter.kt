package spd.difhub.convert

import spd.difhub.model.FieldsItem
import spd.difhub.model.Model
import spd.difhub.model.RecordsItem

class EnumConverter(private val source: Model) {

	fun convert(): EnumModel {
		val nameOrder = findField(usage = "name")?.order ?: 1
		val descOrder = findField(usage = "description")?.order ?: 2
		val valueOrder = findField(usage = "value")?.order ?: 3

		val entries = mutableSetOf<EnumEntry>()
		source.data?.records?.forEach {
			if (it.values != null && it.values.isNotEmpty()) {
				entries.add(EnumEntry(
						getFieldValue(it, nameOrder),
						getFieldValue(it, valueOrder),
						getFieldValue(it, descOrder)
				))
			}
		}
		return EnumModel(entries)
	}

	private fun getFieldValue(recordsItem: RecordsItem, order: Int): String? {
		return if (recordsItem.values != null && recordsItem.values.size >= order) {
			normalizeValue(recordsItem.values[order - 1])
		} else null
	}

	private fun normalizeValue(s: String?): String? {
		val result = if (s != null && s.startsWith("\"")) s.removePrefix("\"") else s
		return if (result != null && result.contains("\"")) result.replace("\"", "'") else result
	}

	private fun findField(usage: String): FieldsItem? {
		return source.structure!!.fields?.find { it.usage.toLowerCase() == usage}
	}
}

data class EnumModel(
		val values: Set<EnumEntry>,
		val description: String = ""
)

data class EnumEntry(
	val name: String?,
	val value: String?,
	val description: String?
)
