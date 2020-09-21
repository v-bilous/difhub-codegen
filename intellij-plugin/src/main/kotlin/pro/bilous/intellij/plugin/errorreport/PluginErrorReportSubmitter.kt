package pro.bilous.intellij.plugin.errorreport

import pro.bilous.intellij.plugin.Version
import com.intellij.diagnostic.LogMessage
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.util.Consumer
//import io.sentry.Sentry
//import io.sentry.connection.EventSendCallback
//import io.sentry.event.Event
import org.slf4j.LoggerFactory
import java.awt.Component

class PluginErrorReportSubmitter : ErrorReportSubmitter() {
    companion object {
        private val logger = LoggerFactory.getLogger(PluginErrorReportSubmitter::class.java)
    }

    override fun getReportActionText()
        = "Report to Plugin Developer (Please include your email address)"

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<SubmittedReportInfo>
    ): Boolean {
        val error = events.firstOrNull() ?: return false
        logger.error("Plugin failed with error: $error", error)
//        try {
//            val errorModel = createErrorBean(error, additionalInfo)
//            SentryErrorPublisher.eventSendCallback = object : EventSendCallback {
//                override fun onSuccess(event: Event) {
//                    errorModel.sentryIssueId = SentryErrorPublisher.lastEventId
//                    errorModel.sentryShareLink = SentryErrorPublisher.lastShareLink
//                    MicrosoftTeamsPublisher.sendError(errorModel)
//                }
//
//                override fun onFailure(event: Event, exception: java.lang.Exception?) {
//                    MicrosoftTeamsPublisher.sendError(errorModel)
//                }
//            }
//            SentryErrorPublisher.sendError(errorModel)
//        } catch (e: Exception) {
//            logger.error("Report failed with error:", e)
//            Sentry.capture(e)
//            return false
//        }
        return true
    }

    private fun createErrorBean(event: IdeaLoggingEvent, additionalInfo: String?): ErrorModel {
        return ErrorModel(
            errorEvent = event,
            additionalInfo = additionalInfo,
            pluginVersion = Version.get(),
            intellijVersion = getIntellijVersion(),
            os = "${System.getProperty("os.name")} ${System.getProperty("os.version")}",
            userName = System.getProperty("user.name"),
            java = "${System.getProperty("java.vendor")} ${System.getProperty("java.version")}",
            exception = event.throwableText,
            issueTitle = event.message ?: (event.data as LogMessage).throwable.message ?: "Unknown Error"
        )
    }

    private fun getIntellijVersion(): String {
        val appInfo = ApplicationInfoEx.getInstanceEx()
        return "${appInfo.versionName} ${appInfo.majorVersion}.${appInfo.minorVersion} ${appInfo.apiVersion}"
    }
}
