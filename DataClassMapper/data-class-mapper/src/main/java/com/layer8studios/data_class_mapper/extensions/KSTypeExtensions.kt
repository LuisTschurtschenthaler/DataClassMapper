package com.layer8studios.processor.extensions

import com.google.devtools.ksp.symbol.KSType

internal fun KSType.getSimpleName(): String = this.declaration.simpleName.asString()

internal fun KSType.getPackageName(): String = this.declaration.packageName.asString()

internal fun KSType.getQualifiedName(): String? = this.declaration.qualifiedName?.asString()

internal fun KSType.getDefaultValue(): String {
    return when(this.getQualifiedName()) {
        "kotlin.String" -> "\"\""
        "kotlin.Int", "kotlin.Byte", "kotlin.Short" -> "0"
        "kotlin.Long" -> "0L"
        "kotlin.Boolean" -> "false"
        "kotlin.Float" -> "0.f"
        "kotlin.Double" -> "0.0"
        "kotlin.collections.List",
        "kotlin.collections.MutableList",
        "kotlin.collections.ArrayList" -> "emptyList()"
        "kotlin.collections.Set",
        "kotlin.collections.MutableSet",
        "kotlin.collections.HashSet",
        "kotlin.collections.LinkedHashSet" -> "emptySet()"
        "kotlin.collections.Map",
        "kotlin.collections.MutableMap",
        "kotlin.collections.HashMap",
        "kotlin.collections.LinkedHashMap" -> "emptyMap()"
        "kotlin.Char" -> "'\\u0000'"
        "kotlin.ULong" -> "0uL"
        "kotlin.UInt", "kotlin.UByte", "kotlin.UShort" -> "0u"
        "kotlin.Any" -> "Any()"
        "java.time.LocalDate" -> "java.time.LocalDate.now()"
        "java.time.LocalDateTime" -> "java.time.LocalDateTime.now()"
        "java.time.LocalTime" -> "java.time.LocalTime.now()"
        "java.util.Date" -> "java.util.Date()"
        "java.util.UUID" -> "java.util.UUID.randomUUID()"
        "java.math.BigDecimal" -> "java.math.BigDecimal.ZERO"
        "java.math.BigInteger" -> "java.math.BigInteger.ZERO"
        "java.nio.file.Path" -> "java.nio.file.Paths.get(\"\")"
        "java.net.URI" -> "java.net.URI(\"\")"
        "java.util.Locale" -> "java.util.Locale.getDefault()"
        else -> this.declaration.getFirstEnumEntry() ?: "null"
    }
}

internal fun KSType.isCollectionType(): Boolean {
    val collectionTypes = setOf(
        "kotlin.collections.List",
        "kotlin.collections.MutableList",
        "kotlin.collections.ArrayList",
        "kotlin.collections.Set",
        "kotlin.collections.MutableSet",
        "kotlin.collections.HashSet",
        "kotlin.collections.LinkedHashSet",
        "kotlin.collections.Map",
        "kotlin.collections.MutableMap",
        "kotlin.collections.HashMap",
        "kotlin.collections.LinkedHashMap"
    )
    return (this.getQualifiedName() in collectionTypes)
}

