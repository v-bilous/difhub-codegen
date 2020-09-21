package pro.bilous.difhub.auth

import pro.bilous.difhub.j.AuthenticationHelper
import java.lang.IllegalArgumentException

class CognitoWrapper {
	companion object {
		const val USER_POOL = "us-east-1_lp8EgjLHh"
		const val CLIENT_ID = "7g9viev199moq7s41el8909sd9"
	}

	fun login(): String {
		var username = System.getProperty("DIFHUB_USERNAME")
		if (username.isNullOrEmpty()) {
			username = System.getenv("DIFHUB_ORG_NAME") ?: throw IllegalArgumentException("Username not found")
		}
		var password = System.getProperty("DIFHUB_PASSWORD")
		if (password.isNullOrEmpty()) {
			password = System.getenv("DIFHUB_PASSWORD") ?: throw IllegalArgumentException("Password not found")
		}
		return authorizeUser(username, password)
	}

	private fun authorizeUser(username: String, password: String): String {
		val helper = AuthenticationHelper(USER_POOL, CLIENT_ID, "")
		return helper.PerformSRPAuthentication(username, password)
	}
}
