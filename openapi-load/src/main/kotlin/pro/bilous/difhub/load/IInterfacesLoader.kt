package pro.bilous.difhub.load

import pro.bilous.difhub.model.Model

interface IInterfacesLoader {
	fun load(system: String, app: String): List<Model>?
}
