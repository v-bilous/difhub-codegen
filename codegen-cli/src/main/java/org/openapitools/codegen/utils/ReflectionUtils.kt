package org.openapitools.codegen.utils

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun setValueToField(kClass: KClass<*>,
					instance: Any,
					filedName: String,
					fieldValue: Any) {
	val field = kClass.declaredMemberProperties.first { it.name == filedName } as KMutableProperty<*>
	field.isAccessible = true
	field.setter.call(instance, fieldValue)
}
