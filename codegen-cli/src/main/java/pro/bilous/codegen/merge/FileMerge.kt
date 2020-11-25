package pro.bilous.codegen.merge

import pro.bilous.codegen.experimental.diff_match_patch
import java.lang.IllegalArgumentException
import java.util.*

class FileMerge {

	companion object {
		private const val codeInject = "//codegen:merge"
		private const val confInject = "#codegen:merge"
		val mergeExt = mapOf(
			".kts" to codeInject,
			".kt" to codeInject,
			".xml" to "<!--codegen:merge-->",
			".yml" to confInject,
			".properties" to confInject,
			".gitignore" to confInject,
			".editorconfig" to confInject,
		)
	}

	fun supportsMerge(filename: String): Boolean {
		return mergeExt.containsKey(getFileExtension(filename))
	}

	fun getMergePrefix(filename: String): String {
		val fileExtension = getFileExtension(filename)
		if (supportsMerge(fileExtension)) {
			return mergeExt.getValue(fileExtension)
		}
		throw IllegalArgumentException("Not supported file extension for file: $filename")
	}

	fun mergeFileContent(existingContent: String, newContent: String, filename: String): String {
		val dmp = diff_match_patch()
		val diff = diffLines(newContent, existingContent, dmp)
		val injectPrefix = getMergePrefix(filename)

		val codeDiff = diff.map { CodeDiff(it, it.operation) }

		codeDiff.forEachIndexed { index, it ->
			when (it.diff.operation) {
				diff_match_patch.Operation.INSERT -> {
					val textBlock = it.diff.text.trimStart()
					val isInjectAnnotated = textBlock.startsWith(injectPrefix)
					if (!isInjectAnnotated) {
						it.diff.operation = diff_match_patch.Operation.EQUAL
						it.removeDiff = true
					} else {
						// consider as replacement operation in case if previous to INSERT is DELETE
						if (index > 0 && codeDiff[index - 1].originalOperation == diff_match_patch.Operation.DELETE) {
							codeDiff[index - 1].diff.operation = diff_match_patch.Operation.DELETE
						}
					}
				}
				diff_match_patch.Operation.DELETE -> {
					// delete operation is not supported, can not delete from template!
					it.diff.operation = diff_match_patch.Operation.EQUAL
				}
				else -> {
					println("ignoring diff")
				}
			}
		}

		val diffsToApply = LinkedList(codeDiff.filter { !it.removeDiff }.map { it.diff })

		val patch = dmp.patch_make(newContent, diffsToApply)

		val result = dmp.patch_apply(patch, newContent)

		val contentResult = result[0]
		println(result[1])
		return contentResult as String
	}

	private fun diffLines(text1: String, text2: String, dmp: diff_match_patch): LinkedList<diff_match_patch.Diff> {
		val linesResult = dmp.diff_linesToChars(text1, text2)
		val lineText1 = linesResult.chars1
		val lineText2 = linesResult.chars2
		val lineArray = linesResult.lineArray
		val diffs = dmp.diff_main(lineText1, lineText2, false)
		dmp.diff_charsToLines(diffs, lineArray)
		return diffs
	}

	private fun getFileExtension(filename: String): String {
		return ".${filename.split(".").last()}"
	}

 }
