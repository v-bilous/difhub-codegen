package pro.bilous.intellij.plugin

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object Icons {
    val SpringBoot = Icons.load("/icon/boot.png")

    private fun load(paramString: String): Icon {
        return IconLoader.getIcon(paramString, Icons::class.java)
    }
}
