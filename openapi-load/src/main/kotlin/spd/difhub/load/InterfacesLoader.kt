package spd.difhub.load

import spd.difhub.config.ConfigReader
import spd.difhub.model.Model

class InterfacesLoader {
	fun load(system: String, app: String): List<Model>? {
		val difhub = ConfigReader.loadConfig().difhub

		val interfaceList =  ModelLoader(DefLoader()).loadModels(difhub.getInterfacesUrl(system, app))

		val intefaces = mutableListOf<Model>()

		interfaceList?.forEach {
			val `interface` = ModelLoader(DefLoader()).loadModel(difhub.getInterfaceUrl(system, app, it.identity.name))!!
			intefaces.add(`interface`)
		}
		return intefaces
	}
}
