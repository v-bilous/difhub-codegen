package pro.bilous.difhub.load

import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.model.Model

class DatasetsLoader {
	fun load(system: String, app: String, type: String? = null): List<Model>? {
		val difhub = ConfigReader.loadConfig().difhub
		val datasetList = ModelLoader(DefLoader()).loadModels(difhub.getDatasetsUrl(system, app))!!

		val datasets = mutableListOf<Model>()

		val allowedTypes =
		if (type.isNullOrEmpty()) {
			listOf("Structure", "Reference", "Resource", "Enum")
		} else {
			listOf(type)
		}

		datasetList
				.filter { it.`object` != null && allowedTypes.contains(it.`object`.usage) }
				.forEach {

					var url = difhub.getDatatsetTypeUrl(system, app, it.identity.name)
//					if (it.version != null) {
//						url = "$url/versions/${it.version.major}.${it.version.minor}.${it.version.revision}"
//					}
					val inteface = ModelLoader(DefLoader()).loadModel(url)!!
					datasets.add(inteface)
		}
		return datasets
	}
}
