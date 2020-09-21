package pro.bilous.difhub.load

import pro.bilous.difhub.auth.Authorizer

object TokenReader {

	private val authorizer = Authorizer()

	fun readAuth(): String {
		return "Bearer ${authorizer.authToken()}"
	}
}
