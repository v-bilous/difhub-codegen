package pro.bilous.difhub.convert

fun normalizeTypeName(source: String): String {
	return source.replace(" ", "").trimStart().trimEnd().capitalize()
}
