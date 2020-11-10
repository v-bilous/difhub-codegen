package pro.bilous.codegen.merge

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FileMergeTest {

	@Test
	fun `file extension should support merge`() {
		val merge = FileMerge()
		assertTrue(merge.supportsMerge("/test/path/file/testFileName.kt"))
		assertTrue(merge.supportsMerge("/test/path/file/build.gradle.kts"))
		assertTrue(merge.supportsMerge("/test/path/file/generatedChangelog.xml"))
		assertTrue(merge.supportsMerge("/test/path/file/application-development.yml"))
		assertTrue(merge.supportsMerge("/test/path/file/.editorconfig"))
		assertTrue(merge.supportsMerge("/test/path/file/.gitignore"))
		assertTrue(merge.supportsMerge("/test/path/file/.properties"))
	}

	@Test
	fun `file extension should not support merge`() {
		val merge = FileMerge()
		assertFalse(merge.supportsMerge("/test/path/file/testFileName.some"))
		assertFalse(merge.supportsMerge("/test/path/file/build.gradle.bat"))
		assertFalse(merge.supportsMerge("/test/path/file/generatedChangelog.md"))
		assertFalse(merge.supportsMerge("/test/path/file/application-development.yaml"))
	}

	@Test
	fun `should return prefix for extension`() {
		val merge = FileMerge()
		assertEquals("//codegen:merge", merge.getMergePrefix("/test/path/file/testFileName.kt"))
		assertEquals("//codegen:merge", merge.getMergePrefix("/test/path/file/build.gradle.kts"))
		assertEquals("<!--codegen:merge-->", merge.getMergePrefix("/test/path/file/generatedChangelog.xml"))
		assertEquals("#codegen:merge", merge.getMergePrefix("/test/path/file/application-development.yml"))
		assertEquals("#codegen:merge", merge.getMergePrefix("/test/path/file/.editorconfig"))
		assertEquals("#codegen:merge", merge.getMergePrefix("/test/path/file/.gitignore"))
		assertEquals("#codegen:merge", merge.getMergePrefix("/test/path/file/.properties"))
	}

	@Test
	fun `should throw exception for extension`() {
		val merge = FileMerge()
		assertThrows(IllegalArgumentException::class.java) {
			merge.getMergePrefix("/test/path/file/testFileName.bat")
		}
	}
}
