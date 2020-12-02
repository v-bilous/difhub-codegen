package pro.bilous.difhub.load

import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.model.Model

class InterfacesLoader : IInterfacesLoader {
	override fun load(system: String, app: String): List<Model>? {
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
