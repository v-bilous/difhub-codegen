package pro.bilous.intellij.plugin.action

import pro.bilous.intellij.plugin.Version
import pro.bilous.intellij.plugin.errorreport.ErrorModel
//import bhn.intellij.plugin.errorreport.MicrosoftTeamsPublisher
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ex.ApplicationInfoEx

class ReportIssueAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = ReportIssueDialog()

        if (dialog.showAndGet()) {
//            val reportModel = createReportModel(dialog)
//            MicrosoftTeamsPublisher.sendError(reportModel)
        }
    }

    private fun createReportModel(dialog: ReportIssueDialog): ErrorModel {
        return ErrorModel(
            additionalInfo = dialog.textArea.text,
            pluginVersion = Version.get(),
            intellijVersion = getIntellijVersion(),
            os = "${System.getProperty("os.name")} ${System.getProperty("os.version")}",
            userName = System.getProperty("user.name"),
            java = "${System.getProperty("java.vendor")} ${System.getProperty("java.version")}",
            issueTitle = "Custom Issue submit by ${System.getProperty("user.name")}"
        )
    }

    private fun getIntellijVersion(): String {
        val appInfo = ApplicationInfoEx.getInstanceEx()
        return "${appInfo.versionName} ${appInfo.majorVersion}.${appInfo.minorVersion} ${appInfo.apiVersion}"
    }
}
