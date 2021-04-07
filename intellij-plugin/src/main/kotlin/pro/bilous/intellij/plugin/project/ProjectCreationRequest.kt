package pro.bilous.intellij.plugin.project

class ProjectCreationRequest {

    var username: String? = null
    var password: String? = null
    var organization: String? = null

    var difHubData: Map<String, List<String>>? = null

    var system: String = ""
    var applications = mutableSetOf<String>()

    var artifactId = ""
    var groupId = ""
    var version = "0.0.1-SNAPSHOT"
    var description = ""
    var title = ""
    var basePackage = ""
    var dbName = ""
    var database = "MySQL"
	var enumsType = "SimpleEnums"
    var addKotlin = true
    var dateLibrary = "default"
    var addBindingEntity = true
    var authorizationEnabled = false

    fun getMetadata(): Any? {
        return null
    }
}
