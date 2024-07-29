package com.layer8studios.data_class_mapper.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Map(val targetClass: KClass<*>)
