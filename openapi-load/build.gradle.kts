
configure<SourceSetContainer> {
	named("main") {
		java.srcDir("src/main/kotlin")
	}
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	api("io.swagger.parser.v3:swagger-parser:2.0.19")
	implementation("org.kohsuke.metainf-services:metainf-services:1.8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
	implementation("com.squareup.okhttp3:okhttp:4.2.0")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.9")
	implementation("com.amazonaws:aws-java-sdk-cognitoidp:1.11.699")
	implementation("org.json:json:20170516")

	testImplementation(kotlin("test-junit"))
	testImplementation("org.mockito:mockito-core:3.5.11")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
