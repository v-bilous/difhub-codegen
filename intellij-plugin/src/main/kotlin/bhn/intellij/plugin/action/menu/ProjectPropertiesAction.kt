package bhn.intellij.plugin.action.menu

import bhn.intellij.plugin.Icons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.VirtualFileManager
import java.lang.IllegalStateException

class ProjectPropertiesAction : AnAction(Icons.projectSettings) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val filePath = "${project.basePath}/.openapi-generator/settings.yaml"
        val file = VirtualFileManager.getInstance().findFileByUrl("file://$filePath")
            ?: throw IllegalStateException("Missing project properties file, location: $filePath")
        OpenFileDescriptor(project, file).navigate(true)
    }
}