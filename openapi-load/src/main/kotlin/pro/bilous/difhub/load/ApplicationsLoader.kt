package pro.bilous.difhub.load

import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.model.Model

class ApplicationsLoader {

	fun loadAppBySystem(system: String): List<String> {
		val difhub = ConfigReader.loadConfig().difhub
		val models = ModelLoader(DefLoader()).loadModels(difhub.getApplicationsUrl(system))

		val apps = mutableListOf<String>()
		models?.filter {
			it.`object`!!.usage == "Service"
		}?.forEach {
			val name = it.identity.name
			if (name.isNotEmpty()) {
				apps.add(name)
			}
		}
		return apps
	}

	fun loadAll(system: String): List<Model>? {
		val difhub = ConfigReader.loadConfig().difhub
		return ModelLoader(DefLoader()).loadModels(difhub.getApplicationsUrl(system))
	}

	fun loadOne(system: String, app: String): Model? {
		val difhub = ConfigReader.loadConfig().difhub
		return ModelLoader(DefLoader()).loadModel(difhub.getApplicationUrl(system, app))
	}

	fun loadAppSettings(system: String, app: String): Model? {
		val difhub = ConfigReader.loadConfig().difhub
		return ModelLoader(DefLoader()).loadModel(difhub.getApplicationSettingsUrl(system, app))
	}

}
