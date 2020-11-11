package pro.bilous.codegen.merge

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FileXmlContent1Test {

	@Test
	fun `should pass test and merge content`() {
		val fileMerge = FileMerge()
		val fakeFilePath = "/path/to/file/test.xml"
		val result = fileMerge.mergeFileContent(testXmlExisting, testXmlNew, fakeFilePath)

		assertEquals(expectedXmlResult, result)
	}

	private val testXmlExisting = """
	<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd">
	<include file="liquibase/settings.xml"/>

	<include file="liquibase/migrations/generatedChangeLog.xml"/>

	<!--codegen:merge-->
	<include file="liquibase/migrations/test_data.xml"/>

	<!--keep the file at bottom -->
<!--
	<include file="liquibase/migrations/metadata_data.xml"/>
-->
	</databaseChangeLog>

"""

	private val testXmlNew = """
	<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd">
	<include file="liquibase/settings.xml"/>

	<include file="liquibase/migrations/generatedChangeLog.xml"/>

	<!--keep the file at bottom -->
<!--
	<include file="liquibase/migrations/metadata_data.xml"/>
-->
	</databaseChangeLog>

"""

	private val expectedXmlResult = """
	<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd">
	<include file="liquibase/settings.xml"/>

	<include file="liquibase/migrations/generatedChangeLog.xml"/>

	<!--codegen:merge-->
	<include file="liquibase/migrations/test_data.xml"/>

	<!--keep the file at bottom -->
<!--
	<include file="liquibase/migrations/metadata_data.xml"/>
-->
	</databaseChangeLog>

"""
}
