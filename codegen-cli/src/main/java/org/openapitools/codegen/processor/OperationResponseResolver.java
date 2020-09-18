package org.openapitools.codegen.processor;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.openapitools.codegen.CodeCodegen;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.utils.ModelUtils;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class OperationResponseResolver {

	private CodeCodegen codeCodegen;
	private OpenAPI openAPI;

	public OperationResponseResolver(CodeCodegen codeCodegen) {
		this.codeCodegen = codeCodegen;
		openAPI = codeCodegen.getOpenApi();
	}

	public void resolve(Operation operation, CodegenOperation op) {
		ApiResponse methodResponse = codeCodegen.findMethodResponse(operation.getResponses());
		// no valid reference found in the response
		if (isEmpty(methodResponse.get$ref())) {
			return;
		}
		Map<String, Schema> schemas = ModelUtils.getSchemas(this.openAPI);

		Schema fakeSchema = new Schema();
		fakeSchema.set$ref(methodResponse.get$ref());

		Schema responseSchema = ModelUtils.unaliasSchema(this.openAPI, fakeSchema);
		if (responseSchema != null) {
			CodegenProperty cm = codeCodegen.fromProperty("response", responseSchema);

			if (ModelUtils.isArraySchema(responseSchema)) {
				ArraySchema as = (ArraySchema) responseSchema;
				CodegenProperty innerProperty = codeCodegen.fromProperty("response", as.getItems());
				op.returnBaseType = innerProperty.baseType;
			} else if (ModelUtils.isMapSchema(responseSchema)) {
				CodegenProperty innerProperty = codeCodegen.fromProperty("response", ModelUtils.getAdditionalProperties(responseSchema));
				op.returnBaseType = innerProperty.baseType;
			} else {
				if (cm.complexType != null) {
					op.returnBaseType = cm.complexType;
				} else {
					op.returnBaseType = cm.baseType;
				}
			}

			// generate examples
			String exampleStatusCode = "200";
			for (String key : operation.getResponses().keySet()) {
				if (operation.getResponses().get(key) == methodResponse && !key.equals("default")) {
					exampleStatusCode = key;
				}
			}
			//op.examples = new ExampleGenerator(schemas, this.openAPI).generateFromResponseSchema(exampleStatusCode, responseSchema, DefaultCodegen.getProducesInfo(this.openAPI, operation));
			op.defaultResponse = codeCodegen.toDefaultValue(responseSchema);
			op.returnType = cm.dataType;
			op.hasReference = schemas.containsKey(op.returnBaseType);

			// lookup discriminator
			Schema schema = schemas.get(op.returnBaseType);
			if (schema != null) {
				CodegenModel cmod = codeCodegen.fromModel(op.returnBaseType, schema);
				op.discriminator = cmod.discriminator;
			}

			if (cm.isContainer) {
				op.returnContainer = cm.containerType;
				if ("map".equals(cm.containerType)) {
					op.isMapContainer = true;
				} else if ("list".equalsIgnoreCase(cm.containerType)) {
					op.isListContainer = true;
				} else if ("array".equalsIgnoreCase(cm.containerType)) {
					op.isListContainer = true;
				}
			} else {
				op.returnSimpleType = true;
			}
			if (codeCodegen.languageSpecificPrimitives().contains(op.returnBaseType) || op.returnBaseType == null) {
				op.returnTypeIsPrimitive = true;
			}
		}
	}
}
