package spd.difhub.load

import spd.difhub.config.ConfigReader

class SystemsLoader {

	fun loadSystems(): List<String> {
		val difhub = ConfigReader.loadConfig().difhub

		val models = ModelLoader(DefLoader()).loadModels(difhub.getSystemsUrl())
		val systems = mutableListOf<String>()

		models?.forEach {
			val name = it.identity.name
			if (name.isNotEmpty()) {
				systems.add(name)
			}
		}

		return systems
	}
}
