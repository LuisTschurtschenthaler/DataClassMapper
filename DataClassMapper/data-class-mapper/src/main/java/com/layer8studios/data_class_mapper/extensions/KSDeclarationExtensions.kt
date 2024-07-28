package com.layer8studios.processor.extensions

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration

internal fun KSDeclaration.isEnumClass(): Boolean {
    return (this is KSClassDeclaration && this.classKind == ClassKind.ENUM_CLASS)
}

internal fun KSDeclaration.getSimpleName(): String = this.simpleName.asString()

internal fun KSClassDeclaration.findFirstEnumEntry(): KSClassDeclaration? {
    return this.declarations
        .filterIsInstance<KSClassDeclaration>()
        .firstOrNull { declaration ->
            (declaration.classKind == ClassKind.ENUM_ENTRY)
        }
}

internal fun KSDeclaration.getFirstEnumEntry(): String? {
    if(!this.isEnumClass()) {
        return null
    }

    val firstEnumEntry = (this as KSClassDeclaration).findFirstEnumEntry()
    return firstEnumEntry?.let { "${this.getSimpleName()}.${it.getSimpleName()}" }
}

internal fun KSDeclaration.findPropertyByName(name: String?): KSAnnotation? {
    return this.annotations.find { annotation ->
        (annotation.getShortName() == name)
    }
}
