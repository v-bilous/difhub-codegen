package bhn.intellij.plugin.gen

import bhn.intellij.plugin.PathTools
import com.intellij.openapi.vfs.VirtualFileManager
import io.airlift.airline.*
import org.openapitools.codegen.cmd.*
import java.util.*

class CodeGenerator {
    fun generate(projectPath: String) {
        val specFilePath = "${PathTools.getHomePath(projectPath)}/"
        val configFile = PathTools.getSettingsPath(projectPath)

        val args = arrayOf(
            "generate",
            "-g",
            "bhn-codegen",
            "-o",
            projectPath,
            "-i",
            specFilePath,
            "-c",
            configFile
        )

        val version = Version.readVersionFromResources()
        val builder = Cli.builder<Runnable>("codegen-cli")
            .withDescription("Code Generation CLI (version $version).")
            .withDefaultCommand(ListGenerators::class.java)
            .withCommands(
                ListGenerators::class.java,
                Generate::class.java,
                Meta::class.java,
                Help::class.java,
                ConfigHelp::class.java,
                Validate::class.java,
                Version::class.java,
                CompletionCommand::class.java
            )

        try {
            builder.build().parse(*args).run()

            VirtualFileManager.getInstance().syncRefresh()
        } catch (e: ParseArgumentsUnexpectedException) {
            System.err.printf(Locale.ROOT, "[error] %s%n%nSee 'openapi-generator help' for usage.%n", e.message)
        } catch (e: ParseOptionMissingException) {
            System.err.printf(Locale.ROOT, "[error] %s%n", e.message)
        } catch (e: ParseOptionMissingValueException) {
            System.err.printf(Locale.ROOT, "[error] %s%n", e.message)
        }
    }

}
