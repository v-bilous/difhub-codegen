package pro.bilous.difhub.load

import pro.bilous.difhub.model.Model

interface IDatasetsLoader {
	fun load(system: String, app: String, type: String? = null): List<Model>?
}
