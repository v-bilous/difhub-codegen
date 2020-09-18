package spd.difhub.console

import spd.difhub.load.ApplicationsLoader
import spd.difhub.load.SystemsLoader

object Console {
	var system: String? = null
	var application: String? = null

	fun select() {
		selectSystem()
	}

	private fun selectSystem() {
		val systems = SystemsLoader().loadSystems()
		system = selectFromList(systems, "System")
	}

	private fun selectApplication() {
		val applications = ApplicationsLoader().loadAppBySystem(system!!)
		application = selectFromList(applications, "Application")
	}

	private fun selectFromList(names: List<String>, title: String): String {
		println("Choose number of the $title:")
		names.forEachIndexed { index, it ->
			println("$index. $it")
		}
		val select = readLine()
		if (select.isNullOrEmpty()) {
			selectSystem()
		}
		val number = select!!.toInt()
		if (number > names.size || number < 0) {
			selectSystem()
		}
		return names[number]
	}
}
