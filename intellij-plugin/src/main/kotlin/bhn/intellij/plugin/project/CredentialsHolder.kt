package bhn.intellij.plugin.project

import spd.difhub.write.YamlWriter

class CredentialsHolder {

    var username: String? = null
    var password: String? = null

    private fun writeCredentials(basePath: String) {
        val configFolder = "$basePath/.openapi-generator"

        val configMap = mapOf(
            "username" to username,
            "password" to password
        )
        YamlWriter("request.system").writeFile(configMap, configFolder, "settings")
    }
}