package pro.bilous.codegen.process.filename

import org.slf4j.LoggerFactory

class ModelFileNameResolver(val args: ModelFileNameArgs) {

	companion object {
		private val log = LoggerFactory.getLogger(ModelFileNameResolver::class.java)
	}

	private val metadataEnums = args.metadataEnums
	private val modelNameSuffix = args.modelNameSuffix

	private val modelNameToReplace = mapOf(
		"Entity" to "ResourceEntity"
//		"Identity"
	)

	/**
	 * We gonna use this function to change the target name of the model name.
	 * Use case for the Common module is to put some of the common files using the custom names.
	 * @param fileName – original filename from the codegen
	 * @return original file name or modified
	 */
	fun resolve(fileName: String): String {
		//log.debug("File Name received to resolve $fileName")
		return when {
			metadataEnums.containsKey(fileName) -> {
				fileName.removeSuffix(modelNameSuffix)
			}
			modelNameToReplace.containsKey(fileName) -> {
				modelNameToReplace[fileName] ?: error("failed while replacing the name")
			}
			else -> fileName
		}
	}
}
