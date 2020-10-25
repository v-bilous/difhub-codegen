package pro.bilous.codegen.core

import org.openapitools.codegen.ClientOptInput

interface IGenerateInvoker {
	fun invoke(index: Int, optInput: ClientOptInput)
}
