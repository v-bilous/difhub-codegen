package org.openapitools.codegen.processor

import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import org.openapitools.codegen.CodeCodegen
import org.openapitools.codegen.CodegenProperty
import org.openapitools.codegen.utils.ModelUtils
import java.util.HashMap

class FromPropertyProcessor(val codegen: CodeCodegen) {
	fun process(name: String?, p: Schema<*>?, property: CodegenProperty): CodegenProperty {

		// Extract allowableValues for the allOf property since it's not implemented in the Generator

		if (p is ComposedSchema && p.allOf.isNotEmpty()) {

			val referencedSchema = ModelUtils.getReferencedSchema(codegen.getOpenApi(), p.allOf.first())

			//Referenced enum case:
			if (referencedSchema.enum != null && referencedSchema.enum.isNotEmpty()) {
				val _enum = referencedSchema.enum

				val allowableValues = HashMap<String, Any>()
				allowableValues["values"] = _enum
				if (allowableValues.size > 0) {
					property.allowableValues = allowableValues
				}
			}
		}


		return property
	}

}
