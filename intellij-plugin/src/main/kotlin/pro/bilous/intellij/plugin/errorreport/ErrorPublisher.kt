package pro.bilous.intellij.plugin.errorreport

interface ErrorPublisher {

    fun sendError(error: ErrorModel)
}
