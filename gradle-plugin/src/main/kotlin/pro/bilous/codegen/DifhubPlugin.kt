package pro.bilous.codegen

import org.gradle.api.Plugin
import org.gradle.api.Project

class DifhubPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("difhub", DifhubExtension::class.java)
        project.task("difhub").doLast {
            println("${extension.login} from ${extension.password}")
        }
    }

}