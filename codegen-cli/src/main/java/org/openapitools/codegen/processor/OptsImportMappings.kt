package org.openapitools.codegen.processor

import org.openapitools.codegen.CodeCodegen

class OptsImportMappings(val codegen: CodeCodegen) {

	private val importMapping = codegen.importMapping()
	private val basePackage = codegen.basePackage

	private val defaultMapping = mapOf(
			"Identity" to "$basePackage.domain.Identity",
			"Entity" to "$basePackage.domain.Entity",
			"Binding" to "$basePackage.domain.Binding",
			"History" to "$basePackage.domain.History",
			"BaseRecord<String>" to "com.bhn.datamanagement.datamodel.BaseRecord",
			"DomainResource" to "DomainResource",
			"BindingType" to "BindingType",
			"Translation" to "Translation",
			"Error" to "Error",
			"Entity_1" to "Entity",
			"Error_1" to "Error",
			//"Property" to "Property",
			"Property_1" to "Property",
			"Resource" to "org.springframework.core.io.Resource",
			"EventMapping" to "com.bhn.events.EventMapping",
			"JSONObject" to "org.json.simple.JSONObject",
			"Type" to "org.hibernate.annotations.Type",
			"AuditSource" to "AuditSource",
			"EntityState" to "EntityState",
			"EntityType" to "EntityType",
			"Severity" to "Severity",
			"BigDecimal" to "java.math.BigDecimal",
			"MetaDataAnnotation" to "com.bhn.validation.annotations.MetaDataAnnotation",
			"Guid" to "com.bhn.validation.annotations.Guid",
			"FunctionModel" to "com.bhn.datamanagement.restmodel.FunctionModel",
			"Function" to "Function",
			"BaseResource" to "$basePackage.domain.BaseResource",
			"BaseDomain" to "$basePackage.domain.BaseDomain",
			"JsonType" to "org.hibernate.annotations.Type",
	)

	fun addDefaultMappings() {
		importMapping.putAll(defaultMapping)
	}
}
