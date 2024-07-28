package com.layer8studios.processor.extensions

import com.google.devtools.ksp.symbol.KSValueArgument

internal fun KSValueArgument.getName(): String? = this.name?.asString()
