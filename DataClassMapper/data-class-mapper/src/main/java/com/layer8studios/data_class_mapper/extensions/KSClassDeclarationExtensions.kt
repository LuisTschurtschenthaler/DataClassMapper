package com.layer8studios.data_class_mapper.extensions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal fun KSClassDeclaration.isDataClass(): Boolean = this.modifiers.contains(Modifier.DATA)

internal fun KSClassDeclaration.getSimpleName(): String = this.simpleName.asString()

internal fun KSClassDeclaration.getPackageName(): String = this.packageName.asString()
