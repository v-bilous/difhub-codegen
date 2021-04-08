package pro.bilous.intellij.plugin.project

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ProjectDetailsTest {

	@Test
	fun `panel should contain enum switch`() {

		val moduleBuilder = ProjectModuleBuilder().apply {
			request = ProjectCreationRequest()
				.apply { difHubData = mapOf("test" to listOf("test")) }
		}
		val projectDetails = ProjectDetails(moduleBuilder, mock())

		assertNotNull(projectDetails.enumMenuComboBox)
		assertEquals("SimpleEnums", projectDetails.enumMenuComboBox.selectedItem)
	}
}
