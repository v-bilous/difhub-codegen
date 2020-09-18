package org.openapitools.codegen

import io.airlift.airline.Cli
import io.airlift.airline.Help
import io.airlift.airline.ParseArgumentsUnexpectedException
import io.airlift.airline.ParseOptionMissingException
import io.airlift.airline.ParseOptionMissingValueException
import org.openapitools.codegen.cmd.*

import java.util.Locale
import kotlin.system.exitProcess

object OpenAPIGenerator {

	@JvmStatic
	fun main(args: Array<String>) {
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

			// If CLI is run without a command, consider this an error. This exists after initial parse/run
			// so we can present the configured "default command".
			// We can check against empty args because unrecognized arguments/commands result in an exception.
			// This is useful to exit with status 1, for example, so that misconfigured scripts fail fast.
			// We don't want the default command to exit internally with status 1 because when the default command is something like "list",
			// it would prevent scripting using the command directly. Example:
			//     java -jar cli.jar list --short | tr ',' '\n' | xargs -I{} echo "Doing something with {}"
			if (args.isEmpty()) {
				exitProcess(1)
			}
		} catch (e: ParseArgumentsUnexpectedException) {
			System.err.printf(Locale.ROOT, "[error] %s%n%nSee 'openapi-generator help' for usage.%n", e.message)
			exitProcess(1)
		} catch (e: ParseOptionMissingException) {
			System.err.printf(Locale.ROOT, "[error] %s%n", e.message)
			exitProcess(1)
		} catch (e: ParseOptionMissingValueException) {
			System.err.printf(Locale.ROOT, "[error] %s%n", e.message)
			exitProcess(1)
		}

	}
}
