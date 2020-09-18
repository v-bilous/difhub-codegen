//package bhn.intellij.plugin.errorreport
//
//import com.fasterxml.jackson.databind.JsonNode
//import com.fasterxml.jackson.databind.ObjectMapper
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.MediaType.Companion.toMediaType
//
//class SentryApiClient {
//    companion object {
//        private const val authKey = "8653cb1aa544422e801994452699034f0050738bba5641549a71e76f47cb764c"
//        private val setPublicBody = """
//            {
//              "isPublic": true
//            }
//        """.trimIndent().toRequestBody("application/json".toMediaType())
//    }
//    private val client = OkHttpClient()
//    private val objectMapper = ObjectMapper()
//
//    fun getEvent(eventId: String): JsonNode? {
//        return getJson("https://sentry.io/api/0/projects/bhn/intellij-plugin/events/${eventId}/")
//    }
//
//    fun getIssue(issueId: String): JsonNode? {
//        return getJson("https://sentry.io/api/0/issues/${issueId}/")
//    }
//
//    fun setIssueToPublic(issueId: String): JsonNode? {
//        val request = Request.Builder()
//            .url("https://sentry.io/api/0/issues/${issueId}/").put(setPublicBody)
//            .header("Authorization", "Bearer $authKey")
//            .build()
//        val response = client.newCall(request).execute()
//        return objectMapper.readTree(response.body!!.byteStream())
//    }
//
//    private fun getJson(url: String):  JsonNode? {
//        val request = Request.Builder()
//                .url(url).get()
//                .header("Authorization", "Bearer $authKey")
//                .build()
//
//        val response = client.newCall(request).execute()
//        return objectMapper.readTree(response.body!!.byteStream())
//    }
//
//
//}