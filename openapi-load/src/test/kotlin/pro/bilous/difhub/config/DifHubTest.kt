package pro.bilous.difhub.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DifHubTest {

	private val conf = DifHub("", "test org")

	@Test
	fun `test system url`() {
		assertEquals("organizations/test org/systems", conf.getSystemsUrl())
	}

	@Test
	fun `test applications url`() {
		val system = "testSystem"
		assertEquals(
			"organizations/test org/systems/testSystem/applications",
			conf.getApplicationsUrl(system)
		)
	}

	@Test
	fun `test application url`() {
		val system = "testSystem"
		val app = "testApp"
		assertEquals(
			"organizations/test org/systems/testSystem/applications/testApp",
			conf.getApplicationUrl(system, app)
		)
	}

	@Test
	fun `test application settings url`() {
		val system = "testSystem"
		val app = "testApp"
		assertEquals(
			"organizations/test org/systems/testSystem/applications/testApp/settings",
			conf.getApplicationSettingsUrl(system, app)
		)
	}

	@Test
	fun `test application interfaces url`() {
		val system = "testSystem"
		val app = "testApp"
		assertEquals(
			"organizations/test org/systems/testSystem/applications/testApp/interfaces",
			conf.getInterfacesUrl(system, app)
		)
	}

	@Test
	fun `test application interface url`() {
		val system = "testSystem"
		val app = "testApp"
		val name = "testName"
		assertEquals(
			"organizations/test org/systems/testSystem/applications/testApp/interfaces/testName",
			conf.getInterfaceUrl(system, app, name)
		)
	}

	@Test
	fun `test application datasets url`() {
		val system = "testSystem"
		val app = "testApp"
		assertEquals(
			"organizations/test org/systems/testSystem/applications/testApp/datasets",
			conf.getDatasetsUrl(system, app)
		)
	}

	@Test
	fun `test application dataset url`() {
		val system = "testSystem"
		val app = "testApp"
		val name = "testName"
		assertEquals(
			"organizations/test org/systems/testSystem/applications/testApp/datasets/testName",
			conf.getDatatsetTypeUrl(system, app, name)
		)
	}
}
