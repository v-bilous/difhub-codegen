//package bhn.intellij.plugin.errorreport
//
//import okhttp3.OkHttpClient
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.Request
//import org.slf4j.LoggerFactory
//
//object MicrosoftTeamsPublisher: ErrorPublisher {
//    private val logger = LoggerFactory.getLogger(MicrosoftTeamsPublisher::class.java)
//    private const val webhookUrl
//            = "https://outlook.office.com/webhook/4e31aa8c-618a-41e2-81ea-7b9c26ace015@09f55992-c50c-4562-8657-b1bd6acc36c5" +
//              "/IncomingWebhook/41ec610936754ed3857e626f8a0a21bc/f4ecd3ec-c4dd-4808-80d3-a189ee2302f9"
//    private val client = OkHttpClient()
//
//
//    override fun sendError(error: ErrorModel) {
//        val body = if (error.sentryShareLink.isNullOrEmpty()) createIssueReportBody(error) else createBodyWithSentryLink(error)
//        val result = post(body)
//        logger.warn(result)
//    }
//
//    private fun post(json: String): String {
//        val body = json.toRequestBody("application/json".toMediaType())
//        val request = Request.Builder().url(webhookUrl).post(body).build()
//        val response = client.newCall(request).execute()
//        return response.body!!.string()
//    }
//
//    private fun createBodyWithSentryLink(error: ErrorModel): String {
//        with(error) {
//            return """
//            {
//                "@type": "MessageCard",
//                "@context": "http://schema.org/extensions",
//                "themeColor": "0076D7",
//                "summary": "Failed Intellij Idea Plugin",
//                "sections": [{
//                    "activityTitle": "$issueTitle",
//                    "facts": [{
//                        "name": "Plugin version",
//                        "value": "$pluginVersion"
//                    }, {
//                        "name": "Intellij version",
//                        "value": "$intellijVersion"
//                    }, {
//                        "name": "OS",
//                        "value": "$os"
//                    }, {
//                        "name": "Java",
//                        "value": "$java"
//                    }, {
//                        "name": "User Name",
//                        "value": "$userName"
//                    },{
//                        "name": "User Message",
//                        "value": "$additionalInfo"
//                    }],
//                    "markdown": false
//                }],
//                "potentialAction": [{
//                    "@type": "OpenUri",
//                    "name": "View Issue",
//                    "targets": [{
//                        "os": "default",
//                        "uri": "$sentryShareLink"
//                    }]
//                }]
//            }
//        """
//        }
//    }
//
//    private fun createIssueReportBody(error: ErrorModel): String {
//        with(error) {
//            return """
//            {
//                "@type": "MessageCard",
//                "@context": "http://schema.org/extensions",
//                "themeColor": "0076D7",
//                "summary": "Failed Intellij Idea Plugin",
//                "sections": [{
//                    "activityTitle": "$issueTitle",
//                    "facts": [{
//                        "name": "Plugin version",
//                        "value": "$pluginVersion"
//                    }, {
//                        "name": "Intellij version",
//                        "value": "$intellijVersion"
//                    }, {
//                        "name": "OS",
//                        "value": "$os"
//                    }, {
//                        "name": "Java",
//                        "value": "$java"
//                    }, {
//                        "name": "User Name",
//                        "value": "$userName"
//                    },{
//                        "name": "User Message",
//                        "value": "$additionalInfo"
//                    }],
//                    "markdown": true
//                }]
//            }
//        """
//        }
//    }
//}