package com.layer8studios.processor.extensions

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument

internal fun KSAnnotation.getShortName(): String = this.shortName.asString()

internal fun KSAnnotation.getFirstArgumentValue(): String = this.arguments.first().value.toString()

internal fun KSAnnotation.getFirstOrNullArgumentValue(): String? = this.arguments.firstOrNull()?.value as? String

internal fun KSAnnotation.findArgumentByName(name: String?): KSValueArgument? {
    return this.arguments.find { argument ->
        argument.getName() == name
    }
}
