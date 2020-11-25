package pro.bilous.codegen.merge

import pro.bilous.codegen.experimental.diff_match_patch

data class CodeDiff(
	val diff: diff_match_patch.Diff,
	val originalOperation: diff_match_patch.Operation,
	var removeDiff: Boolean = false
)
