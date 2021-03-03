package pro.bilous.difhub.convert

import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import org.junit.jupiter.api.Test
import pro.bilous.difhub.model.FieldsItem
import pro.bilous.difhub.model.Identity
import pro.bilous.difhub.model.Model
import pro.bilous.difhub.model.Structure
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class DefinitionReferenceConverterTest {

	@Test
	fun `should convert reference property`() {
		val field = FieldsItem(
			reference = "/organizations/Infort Technologies/datasets/Identity/versions/1.6.0",
			identity = Identity(name = "owner", id = "owner_id", description = "text description"),
			usage = "Property",
			optional = true,
			type = "Reference",
			format = "Reference(Patient | Practitioner | Employee)",
			count = 1,
			properties = null
		)
		val model = Model(
			identity = Identity(name = "TestModel", id = "test_id"),
			structure = Structure(fields = listOf(field))
		)

		val result = DefinitionConverter(model).convert()

		val schema = result[model.identity.name] as ObjectSchema
		val property = schema.properties["owner"]

		assertNotNull(property)
		assertTrue { property is ComposedSchema }; property as ComposedSchema

		assertEquals(field.identity.description, property.description)

		val refObject = property.allOf.first()
		assertEquals("#/components/schemas/ReferenceIdentity", refObject.`$ref`)

		val referenceSchema = result["ReferenceIdentity"] as ObjectSchema
		assertTrue { referenceSchema.properties.containsKey("id") }
		assertTrue { referenceSchema.properties.containsKey("name") }
		assertTrue { referenceSchema.properties.containsKey("description") }
		assertTrue { referenceSchema.properties.containsKey("type") }
		assertTrue { referenceSchema.properties.containsKey("uri") }

	}
}
