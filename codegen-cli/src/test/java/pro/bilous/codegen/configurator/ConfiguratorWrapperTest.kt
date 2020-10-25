package pro.bilous.codegen.configurator

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test
import org.openapitools.codegen.ClientOptInput
import pro.bilous.codegen.core.ICustomConfigurator
import pro.bilous.codegen.core.IGenerateInvoker
import org.openapitools.codegen.config.DynamicSettings
import kotlin.test.assertEquals

internal class ConfiguratorWrapperTest {

	@Test
	fun `should call fallback to generate one`() {
		val configurator: ICustomConfigurator = mock()
		val invoker: IGenerateInvoker = mock()
		val testInput = ClientOptInput()
		whenever(configurator.toCustomClientOptInput()).thenReturn(testInput)

		val wrapper = ConfiguratorWrapper(configurator, invoker)

		wrapper.generate()
		verify(invoker, times(1)).invoke(0, testInput)
	}

	@Test
	fun `should invoke generate for two apps`() {
		val configurator: ICustomConfigurator = mock()
		val invoker: IGenerateInvoker = mock()
		val settings = DynamicSettings()
		settings.dynamicProperties["application"] = listOf("User", "Party")
		settings.dynamicProperties["basePackage"] = "app.client"
		settings.dynamicProperties["system"] = "Client"

		val testInput = ClientOptInput()

		whenever(configurator.getCustomSettings()).thenReturn(settings)
		whenever(configurator.toCustomClientOptInput()).thenReturn(testInput)
		whenever(configurator.getSpecCopyValue()).thenReturn("/path/to/specs/") // should end with '/'

		val wrapper = ConfiguratorWrapper(configurator, invoker)

		wrapper.generate()

		verify(configurator, times(1)).setCustomProperty("systemLower", "client")
		verify(configurator, times(1)).setCustomProperty(eq("database"), any())

		argumentCaptor<List<String>>().apply {
			verify(configurator, times(1)).setCustomProperty(eq("appsLower"), capture())

			assertEquals(1, allValues.size)
			assertEquals(2, firstValue.size)
			assertEquals("user", firstValue[0])
			assertEquals("party", firstValue[1])
		}

		argumentCaptor<String>().apply {
			verify(configurator, times(2)).setCustomInputSpec(capture())

			assertEquals(2, allValues.size)
			assertEquals("/path/to/specs/user-api.yaml", firstValue)
			assertEquals("/path/to/specs/party-api.yaml", secondValue)
		}

		argumentCaptor<String>().apply {
			verify(configurator, times(2)).setCustomArtifactId(capture())

			assertEquals(2, allValues.size)
			assertEquals("user", firstValue)
			assertEquals("party", secondValue)
		}

		argumentCaptor<String>().apply {
			verify(configurator, times(2)).setCustomProperty(eq("appPackage"), capture())

			assertEquals(2, allValues.size)
			assertEquals("app.client.user", firstValue)
			assertEquals("app.client.party", secondValue)
		}

		argumentCaptor<String>().apply {
			verify(configurator, times(2)).setCustomProperty(eq("appRealName"), capture())

			assertEquals(2, allValues.size)
			assertEquals("User", firstValue)
			assertEquals("Party", secondValue)
		}

		argumentCaptor<String>().apply {
			verify(configurator, times(2)).setCustomProperty(eq("appNameLower"), capture())

			assertEquals(2, allValues.size)
			assertEquals("user", firstValue)
			assertEquals("party", secondValue)
		}

		verify(invoker, times(1)).invoke(0, testInput)
		verify(invoker, times(1)).invoke(1, testInput)

	}
}
