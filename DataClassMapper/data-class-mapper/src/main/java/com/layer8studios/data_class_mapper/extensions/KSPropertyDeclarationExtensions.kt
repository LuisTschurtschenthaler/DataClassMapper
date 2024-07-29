package com.layer8studios.data_class_mapper.extensions

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType

internal fun KSPropertyDeclaration.getSimpleName(): String = this.simpleName.asString()

internal fun KSPropertyDeclaration.getPropertyType(): KSType = this.type.resolve()
