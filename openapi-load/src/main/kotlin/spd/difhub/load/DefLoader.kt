package spd.difhub.load

import spd.difhub.config.ConfigReader
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class DefLoader {
	companion object {
		var authHeader = "missing"
		private val client = OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.readTimeout(7, TimeUnit.SECONDS)
				.build()
	}

	var loadAttempts = 0
	fun load(path: String): String? {
		val difhub = ConfigReader.loadConfig().difhub

		val url = "${difhub.api}/$path"
		val request = Request.Builder()
				.url(url)
				.addHeader("Authorization", authHeader)
				.build()


		val response = try {
			client.newCall(request).execute()
		} catch (e: SocketTimeoutException) {
			if (loadAttempts < 5) {
				Thread.sleep(3000)
				println("Failed attempt to load: retry in 3 second.")
				loadAttempts += 1
				return load(path)
			}
			println("Max retry attempt to load reached: Load Failed.")
			return null
		}
		val result = response.body?.string()
		if (result != null && isUnauthorized(response, result)) {
			authHeader = TokenReader.readAuth()
			//File("/difhub.auth").writeText(authHeader) //TODO  Exception in thread "main" java.lang.IllegalArgumentException: URI is not hierarchical
			return load(path)
		} else if (result != null && isNotFound(response, result)) {
			return null
		}
		println("url loaded: $url")
		return result
	}

	private fun isUnauthorized(response: Response, body: String): Boolean {
		return response.code == 401 || body.contains("\"status\": 401")
	}

	private fun isNotFound(response: Response, body: String): Boolean {
		return response.code == 404 || body.contains("\"status\": 404")
	}
 }
