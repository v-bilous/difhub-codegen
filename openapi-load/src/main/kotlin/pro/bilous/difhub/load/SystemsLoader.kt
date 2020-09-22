package pro.bilous.difhub.load

import pro.bilous.difhub.config.Config
import pro.bilous.difhub.config.ConfigReader

class SystemsLoader {

	var modelLoader: IModelLoader = ModelLoader(DefLoader())
	var config = ConfigReader.loadConfig()

	fun loadSystems(): List<String> {
		val difhub = config().difhub

		val models = loader().loadModels(difhub.getSystemsUrl())
		val systems = mutableListOf<String>()

		models?.forEach {
			val name = it.identity.name
			if (name.isNotEmpty()) {
				systems.add(name)
			}
		}

		return systems
	}

	private fun config(): Config {
		return config
	}

	private fun loader(): IModelLoader {
		return modelLoader
	}
}
