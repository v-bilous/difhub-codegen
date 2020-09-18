package spd.difhub.auth

class Authorizer {

	private val cognito = CognitoWrapper()

	fun authToken(): String {
		return cognito.login()
	}
}
