package pro.bilous.codegen.core

import io.swagger.v3.oas.models.Paths
import org.openapitools.codegen.*
import org.slf4j.LoggerFactory
import pro.bilous.codegen.merge.FileMerge
import pro.bilous.codegen.process.filename.FilePathResolver
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

open class InCodeGenerator : DefaultGenerator() {

	companion object {
		private val log = LoggerFactory.getLogger(InCodeGenerator::class.java)
	}

	var codegen: CodeCodegen? = null
	val fileMerge = FileMerge()

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

	override fun writeToFile(filename: String, contents: ByteArray): File {
		if (codegen!!.isEnableMerge()) {
			return writeFileWithMerge(filename, contents)
		}
		return super.writeToFile(filename, contents)
	}

	private fun writeFileWithMerge(filename: String, contents: ByteArray): File {
		val tempFilename = "$filename.tmp"
		// Use Paths.get here to normalize path (for Windows file separator, space escaping on Linux/Mac, etc)
		val outputFile = java.nio.file.Paths.get(filename).toFile()
		var tempFile: File? = null
		try {
			tempFile = writeToFileRaw(tempFilename, contents)
			if (!filesEqual(tempFile, outputFile)) {
				if (outputFile.exists() && fileMerge.supportsMerge(filename)) { // support merge
					mergeFilesAndWriteToTemp(tempFile, outputFile, filename)
				}
				log.info("writing file $filename")
				Files.move(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
				tempFile = null
			} else {
				log.info("skipping unchanged file $filename")
			}
		} finally {
			if (tempFile != null && tempFile.exists()) {
				try {
					tempFile.delete()
				} catch (ex: Exception) {
					log.error("Error removing temporary file $tempFile", ex)
				}
			}
		}
		return outputFile
	}

	private fun mergeFilesAndWriteToTemp(tempFile: File, outputFile: File, filename: String) {
		val tempContent = tempFile.readText()
		val outputContent = outputFile.readText()
		val contentResult = fileMerge.mergeFileContent(outputContent, tempContent, filename)
		writeToFile(tempFile.path, contentResult)
	}

	@Throws(IOException::class)
	private fun writeToFileRaw(filename: String, contents: ByteArray): File {
		// Use Paths.get here to normalize path (for Windows file separator, space escaping on Linux/Mac, etc)
		val output = java.nio.file.Paths.get(filename).toFile()
		if (output.parent != null && !File(output.parent).exists()) {
			val parent = java.nio.file.Paths.get(output.parent).toFile()
			parent.mkdirs()
		}
		Files.write(output.toPath(), contents)
		return output
	}

	@Throws(IOException::class)
	private fun filesEqual(file1: File, file2: File): Boolean {
		return file1.exists() && file2.exists() && Arrays.equals(
			Files.readAllBytes(file1.toPath()),
			Files.readAllBytes(file2.toPath())
		)
	}
}
