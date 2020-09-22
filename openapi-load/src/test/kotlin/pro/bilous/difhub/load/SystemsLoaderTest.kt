package pro.bilous.difhub.load

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import pro.bilous.difhub.model.Identity
import pro.bilous.difhub.model.Model
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SystemsLoaderTest {

	@Test
	fun `test systems load`() {
		val loader = SystemsLoader()
		val modelLoaderMock = mock<IModelLoader> {
			on { loadModels(any()) } doReturn
					listOf(
						Model(identity = Identity(name = "system1")),
						Model(identity = Identity(name = "system2"))
					)
		}
		loader.modelLoader = modelLoaderMock
		val systems = loader.loadSystems()

		assertTrue { systems.isNotEmpty() }
		assertEquals("system1", systems[0])
		assertEquals("system2", systems[1])
	}

	@Test
	fun `test systems load when missing entries`() {
		val loader = SystemsLoader()
		val modelLoaderMock = mock<IModelLoader> {
			on { loadModels(any()) } doReturn listOf()
		}
		loader.modelLoader = modelLoaderMock
		val systems = loader.loadSystems()
		assertTrue { systems.isEmpty() }
	}

	@Test
	fun `test systems load when identity name is empty`() {
		val loader = SystemsLoader()
		val modelLoaderMock = mock<IModelLoader> {
			on { loadModels(any()) } doReturn
					listOf(
						Model(identity = Identity(name = "")),
						Model(identity = Identity(name = "system2"))
					)
		}
		loader.modelLoader = modelLoaderMock
		val systems = loader.loadSystems()
		assertTrue { systems.isNotEmpty() }
		assertEquals("system2", systems[0])	}
}
