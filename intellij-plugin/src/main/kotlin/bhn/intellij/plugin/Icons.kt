package bhn.intellij.plugin

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object Icons {
    val SpringBoot = load("/icon/boot.png")
    val CubaStudio = load("/icon/cuba_studio.svg")
    val restApi = load("/icon/tree/restApi.svg")
    val project = load("/icon/tree/project.svg")
    val projectSettings = load("/icon/tree/projectProperties.svg")

    private fun load(paramString: String): Icon {
        return IconLoader.getIcon(paramString, Icons::class.java)
    }
}