package org.openapitools.codegen

import io.swagger.v3.oas.models.Paths
import java.io.File
import java.io.IOException
import java.io.Reader
import java.lang.RuntimeException

open class InCodeGenerator : DefaultGenerator() {

	@Throws(IOException::class)
	override fun processTemplateToFile(templateData: Map<String, Any>, templateName: String, outputFilename: String): File? {

		return super.processTemplateToFile(templateData, templateName, outputFilename)
	}

	override fun opts(opts: ClientOptInput): Generator {
		val config = opts.config as CodeCodegen

//		config.modelTemplateFiles.remove("model.mustache")
//		config.modelTemplateFiles["entity.mustache"] = ".kt"

		return super.opts(opts)
	}

	override fun processPaths(paths: Paths?): MutableMap<String, MutableList<CodegenOperation>> {
		val paths =  super.processPaths(paths)
		// ignore paths permanently
		if (paths.containsKey("Entities")) {
			paths.remove("Entities")
		}
		return paths
	}

	override fun getFullTemplateFile(config: CodegenConfig?, templateFile: String?): String {
		val fileSrc = super.getFullTemplateFile(config, templateFile)
		return if (embeddedTemplateExists(fileSrc)) {
			fileSrc
		} else {
			config!!.templateDir() + File.separator + "embed" + File.separator + templateFile
		}
	}

	override fun writeToFile(filename: String, contents: String?): File {
		val newFilename = if (filename.contains("/.openapi-generator/")) {
			filename.replace("/.openapi-generator/", "/.difhub-codegen/")
		} else filename
		return super.writeToFile(newFilename, contents)
	}
}
