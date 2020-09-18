plugins {
    id("org.jetbrains.intellij")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
}


dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":openapi-load"))
    implementation(project(":codegen-cli"))

//    implementation ("in.code:openapi-gen:1.0.0-SNAPSHOT") {
//        exclude(group = "org.slf4j")
//    }
//    implementation ("in.code:codegen-cli:1.0.0-SNAPSHOT") {
//        exclude(group = "org.slf4j")
//    }
//    implementation("com.google.guava:guava:'28.1-jre")
//    implementation ("io.sentry:sentry:1.7.27")  {
//        exclude group: 'org.slf4j'
//    }
}

group = "pro.bilous.code"
version = "0.0.7"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
//    pulginName = "DifHub"
    version = "2019.3"
}

tasks.patchPluginXml {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
    sinceBuild("192")
}