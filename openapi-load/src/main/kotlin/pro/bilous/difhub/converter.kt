package pro.bilous.difhub

import pro.bilous.difhub.console.Console
import pro.bilous.difhub.convert.DifHubToSwaggerConverter
import pro.bilous.difhub.write.YamlWriter

fun main() {
	Console.select()
	DifHubToSwaggerConverter(Console.system!!).convertAll().forEach {
		YamlWriter(it.appName).write(it.openApi)
	}
}


