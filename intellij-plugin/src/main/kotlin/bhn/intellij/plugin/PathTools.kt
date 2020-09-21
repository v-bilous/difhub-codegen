package bhn.intellij.plugin

import java.lang.IllegalStateException

object PathTools {
	private const val HOME_PATH = ".difhub-codegen"
	private const val CREDENTIALS_FILE = ".credentials"
	private const val SETTINGS_FILE = "settings"

	fun getHomePath(projectPath: String?): String {
		if (projectPath == null) {
			throw IllegalStateException("projectPath can not be null")
		}
		return "${projectPath}/$HOME_PATH"
	}

	fun getCredentialsPath(projectPath: String?): String {
		return "${getHomePath(projectPath)}/$CREDENTIALS_FILE.yaml"
	}

	fun getSettingsPath(projectPath: String?): String {
		return "${getHomePath(projectPath)}/$SETTINGS_FILE.yaml"
	}
}
