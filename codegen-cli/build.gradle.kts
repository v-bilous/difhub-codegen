plugins {
    kotlin("jvm")
	jacoco
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

jacoco {
	toolVersion = "0.8.5"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("com.google.guava:guava:28.0-jre")
    api("io.airlift:airline:0.8")
    implementation("com.googlecode.lambdaj:lambdaj:2.3.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.openapitools:openapi-generator:4.1.3")
    implementation("junit:junit:4.8.1")

	testImplementation(kotlin("test-junit"))
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.jacocoTestReport {
	reports {
		xml.isEnabled = true
	}
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)
}
