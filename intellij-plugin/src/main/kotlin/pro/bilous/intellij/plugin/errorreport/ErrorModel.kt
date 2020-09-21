package pro.bilous.intellij.plugin.errorreport

import com.intellij.openapi.diagnostic.IdeaLoggingEvent

data class ErrorModel(val errorEvent: IdeaLoggingEvent? = null,
                      val pluginVersion: String,
                      val intellijVersion: String,
                      val os: String,
                      val java: String,
                      val additionalInfo: String?,
                      val issueTitle: String,
                      val exception: String? = null,
                      val userName: String?,
                      var sentryIssueId: String? = null,
                      var sentryShareLink: String? = null)
