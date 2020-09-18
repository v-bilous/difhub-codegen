package spd.difhub

import spd.difhub.console.Console
import spd.difhub.convert.DifHubToSwaggerConverter
import spd.difhub.write.YamlWriter

fun main() {
	Console.select()
	DifHubToSwaggerConverter(Console.system!!).convertAll().forEach {
		YamlWriter(it.appName).write(it.openApi)
	}
}


