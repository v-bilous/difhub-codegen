package pro.bilous.codegen.core

import org.junit.Ignore
import org.junit.Test
import org.openapitools.codegen.config.GeneratorSettings
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

@Ignore
class CodegenReflectionTest {

	@Test
	fun tryReflection() {
		val codegen = InCustomCodegenConfigurator()
		val members = codegen::class.members

		val field = InCustomCodegenConfigurator::class.declaredMemberProperties.first {
			it.name == "generatorSettingsBuilder"
		} as KMutableProperty<*>
		val testName = "genTestName"
		val builder = GeneratorSettings.newBuilder().withGeneratorName(testName)
		field.isAccessible = true
		field.setter.call(codegen, builder)

		println("test")
	}
}
