package pro.bilous.codegen.core

import io.swagger.v3.oas.models.Paths
import org.openapitools.codegen.*
import org.slf4j.LoggerFactory
import pro.bilous.codegen.process.filename.FilePathResolver
import java.io.File
import java.io.IOException
import kotlin.jvm.Throws

open class InCodeGenerator : DefaultGenerator() {

	companion object {
		private val log = LoggerFactory.getLogger(InCodeGenerator::class.java)
	}

	var codegen: CodeCodegen? = null

	/**
	 * This is the final point to override things right before the processing templates and writing file to filesystem.
	 * All params can be changed before this final stage (pass to super to execute the stage)
	 * @param templateData – map of data to process
	 * @param templateName – name of the template to pick up for the processing
	 * @param outputFilename – target full name of the file
	 * @return file written to the filesystem
	 */
	@Throws(IOException::class)
	override fun processTemplateToFile(templateData: Map<String, Any>, templateName: String, outputFilename: String): File? {
		log.debug("processTemplateToFile, templateName: $templateName, outputFilename: $outputFilename")
		val target = FilePathResolver().resolve(templateData, templateName, outputFilename)
		return if (target.shouldWriteFile) {
			super.processTemplateToFile(templateData, templateName, target.targetPath)
		} else null
	}

	override fun opts(opts: ClientOptInput): Generator {
		codegen = opts.config as CodeCodegen

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
