package com.layer8studios.data_class_mapper.extensions

import com.google.devtools.ksp.symbol.KSValueArgument

internal fun KSValueArgument.getName(): String? = this.name?.asString()
