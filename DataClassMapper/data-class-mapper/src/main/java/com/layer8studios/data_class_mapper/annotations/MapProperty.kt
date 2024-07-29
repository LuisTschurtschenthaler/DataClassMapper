package com.layer8studios.data_class_mapper.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class MapProperty(
    val target: String = "",
    val targetClass: KClass<*> = Any::class
)
