//package bhn.intellij.plugin.errorreport
//
//import com.intellij.diagnostic.LogMessage
//import com.intellij.openapi.application.ApplicationInfo
//import com.intellij.openapi.util.SystemInfo
//import io.sentry.Sentry
//import io.sentry.connection.EventSendCallback
//import io.sentry.event.Event
//import io.sentry.event.EventBuilder
//import io.sentry.event.interfaces.ExceptionInterface
//import java.lang.Exception
//import java.lang.IllegalStateException
//
//object SentryErrorPublisher: ErrorPublisher {
//
//    private const val TAG_PLATFORM_VERSION = "platform"
//    private const val TAG_OS = "os"
//    private const val TAG_OS_VERSION = "os_version"
//    private const val TAG_OS_ARCH = "os_arch"
//    private const val TAG_JAVA_VERSION = "java_version"
//    private const val TAG_JAVA_RUNTIME_VERSION = "java_runtime_version"
//
//    private const val EXTRA_OTHER_PLUGINS = "Other Plugins"
//    private const val EXTRA_ADDITIONAL_INFO = "Additional Info"
//
//    private const val dsn = "https://b66243a745f14d0f9d25e6d70b0ff711@sentry.io/1833401"
//    private val sentry = Sentry.init(dsn)
//    private val apiClient = SentryApiClient()
//
//    var lastEventId: String? = null
//    var lastShareLink: String? = null
//
//    var eventSendCallback: EventSendCallback? = null
//
//    init {
//        sentry.addEventSendCallback(object : EventSendCallback {
//            override fun onSuccess(ev: Event) {
//                try {
//                    loadDataFromApi(ev)
//                } catch (e: Exception) {
//                    Sentry.capture(e)
//                }
//            }
//
//            override fun onFailure(e: Event, exception: Exception) {
//                try {
//                    eventSendCallback?.onFailure(e, exception)
//                } catch (e: Exception) {
//                    Sentry.capture(e)
//                }
//            }
//
//        })
//    }
//
//    private fun loadDataFromApi(ev: Event) {
//        lastEventId = ev.id.toString().replace("-", "")
//        val issueId = loadIssueIdFromEvent(lastEventId!!)
//        var issue = apiClient.getIssue(issueId)!!
//        if (!issue.hasNonNull("shareId")) {
//            issue = apiClient.setIssueToPublic(issueId)!!
//        }
//        val shareId = issue.get("shareId").asText()
//        lastShareLink = "https://sentry.io/share/issue/${shareId}/"
//        eventSendCallback?.onSuccess(ev)
//    }
//
//    var delayTime = 1000L
//    private fun loadIssueIdFromEvent(eventId: String): String {
//        if (delayTime > 5000L) {
//            throw IllegalStateException("Failed with timeout when read IssueID")
//        }
//        Thread.sleep(delayTime)
//        val event = apiClient.getEvent(lastEventId!!)!!
//        if (event.has("groupID")) {
//            return event.get("groupID").asText()
//        }
//        delayTime += 500L
//        return loadIssueIdFromEvent(eventId)
//    }
//
//    override fun sendError(error: ErrorModel) {
//        try {
//            val eventBuilder = createEvent(error)
//            sentry.sendEvent(eventBuilder)
//        } catch (e: Exception) {
//            Sentry.capture(e)
//        }
//    }
//
//    private fun createEvent(error: ErrorModel): EventBuilder? {
//        val ideaEvent = error.errorEvent!!
//        val eventBuilder = EventBuilder()
//        with(error) {
//            eventBuilder
//                .withMessage(issueTitle)
//                .withRelease(pluginVersion)
//                .withTag(TAG_PLATFORM_VERSION, getPlatformVersion())
//                .withTag(TAG_OS, SystemInfo.OS_NAME)
//                .withTag(TAG_OS_VERSION, SystemInfo.OS_VERSION)
//                .withTag(TAG_OS_ARCH, SystemInfo.OS_ARCH)
//                .withTag(TAG_JAVA_VERSION, SystemInfo.JAVA_VERSION)
//                .withTag(TAG_JAVA_RUNTIME_VERSION, SystemInfo.JAVA_RUNTIME_VERSION)
//        }
//        val data = ideaEvent.data
//        if (data is LogMessage) {
//            val throwable = data.throwable
//            eventBuilder.withSentryInterface(ExceptionInterface(throwable))
//        } else if (ideaEvent.throwable != null) {
//            eventBuilder.withSentryInterface(ExceptionInterface(ideaEvent.throwable))
//        }
//        if (error.additionalInfo != null) {
//            eventBuilder.withExtra(EXTRA_ADDITIONAL_INFO, error.additionalInfo)
//        }
//        return eventBuilder
//    }
//
//    private fun getPlatformVersion(): String? {
//        return ApplicationInfo.getInstance().build.asString()
//    }
//
//}