package spd.difhub.load

import spd.difhub.auth.Authorizer

object TokenReader {

	private val authorizer = Authorizer()

	fun readAuth(): String {
		return "Bearer ${authorizer.authToken()}"
	}
}
