package pro.bilous.intellij.plugin.action.menu

import pro.bilous.intellij.plugin.PathTools
import pro.bilous.intellij.plugin.project.ProjectFileManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.VirtualFileManager

class DifHubLoginAction: AnAction() {

	private val fileManager = ProjectFileManager()

    override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return
		val filePath = PathTools.getCredentialsPath(project.basePath)
		val file = VirtualFileManager.getInstance().findFileByUrl("file://$filePath")
		if (file == null) {
			fileManager.createAndOpenProjectCredentials(PathTools.getHomePath(project.basePath), project)
		} else {
			OpenFileDescriptor(project, file).navigate(true)
		}
    }
}
