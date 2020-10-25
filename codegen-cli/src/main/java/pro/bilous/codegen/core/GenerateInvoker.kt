package pro.bilous.codegen.core

import org.openapitools.codegen.ClientOptInput

class GenerateInvoker : IGenerateInvoker {
	override fun invoke(index: Int, optInput: ClientOptInput) {
		DataCodeGenerator(index).opts(optInput).generate()
	}
}
