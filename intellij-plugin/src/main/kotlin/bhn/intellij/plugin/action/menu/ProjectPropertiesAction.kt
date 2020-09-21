package bhn.intellij.plugin.action.menu

import bhn.intellij.plugin.PathTools
import bhn.intellij.plugin.project.ProjectFileManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.VirtualFileManager

class ProjectPropertiesAction : AnAction() {

	private val fileManager = ProjectFileManager()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val filePath = PathTools.getSettingsPath(project.basePath)
        val file = VirtualFileManager.getInstance().findFileByUrl("file://$filePath")
		if (file == null) {
			fileManager.createAndOpenProjectSettings(PathTools.getHomePath(project.basePath), project)
		} else {
			OpenFileDescriptor(project, file).navigate(true)
		}
    }
}
