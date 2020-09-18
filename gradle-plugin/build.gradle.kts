plugins {
	`java-gradle-plugin`
}

repositories {
	mavenCentral()
	jcenter()
}

dependencies {
}

gradlePlugin {
	plugins {
		create("difhubPlugin") {
			id = "pro.bilous.codegen"
			implementationClass = "pro.bilous.codegen.DifhubPlugin"
		}
	}
}
