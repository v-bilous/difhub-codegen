package bhn.intellij.plugin

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId

object Version {
    private val pluginVersion: String =
        PluginManager.getPlugin(PluginId.getId("bhn.intellij.plugin"))!!.version

    fun get(): String {
        return pluginVersion
    }
}