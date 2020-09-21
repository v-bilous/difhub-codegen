package pro.bilous.intellij.plugin.project

import com.intellij.openapi.module.ModuleType

object StdModuleTypes {
    val JAVA: ModuleType<*>

    init {
        try {
            JAVA = Class.forName("com.intellij.openapi.module.JavaModuleType").newInstance() as ModuleType<*>
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }

    }
}
