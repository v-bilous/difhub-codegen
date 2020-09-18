package bhn.intellij.plugin.errorreport

interface ErrorPublisher {

    fun sendError(error: ErrorModel)
}