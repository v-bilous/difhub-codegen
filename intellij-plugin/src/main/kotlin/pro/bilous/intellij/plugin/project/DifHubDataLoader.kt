package pro.bilous.intellij.plugin.project

import pro.bilous.difhub.load.ApplicationsLoader
import pro.bilous.difhub.load.SystemsLoader

class DifHubDataLoader {

    fun loadAllSystemsAndApps(): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()
        val systems = SystemsLoader().loadSystems()
        //val systems = arrayListOf("Test1", "Test2")
        systems.forEach {
            val appList = ApplicationsLoader().loadAppBySystem(it)
            //val appList = arrayListOf("AppTest1", "AppTest2")
            result[it] = appList
        }
        return result
    }
}
