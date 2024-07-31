package com.layer8studios.data_class_mapper.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.layer8studios.data_class_mapper.annotations.IgnoreProperty
import com.layer8studios.data_class_mapper.annotations.Map
import com.layer8studios.data_class_mapper.annotations.MapCollectionObject
import com.layer8studios.data_class_mapper.annotations.MapProperty
import com.layer8studios.data_class_mapper.extensions.findArgumentByName
import com.layer8studios.data_class_mapper.extensions.findPropertyByName
import com.layer8studios.data_class_mapper.extensions.getDefaultValue
import com.layer8studios.data_class_mapper.extensions.getFirstArgumentValue
import com.layer8studios.data_class_mapper.extensions.getFirstOrNullArgumentValue
import com.layer8studios.data_class_mapper.extensions.getPackageName
import com.layer8studios.data_class_mapper.extensions.getPropertyType
import com.layer8studios.data_class_mapper.extensions.getSimpleName
import com.layer8studios.data_class_mapper.extensions.isCollectionType
import com.layer8studios.data_class_mapper.extensions.isDataClass
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

internal class MapperProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
): SymbolProcessor {

    inner class MapperVisitor: KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if(!classDeclaration.isDataClass()) {
                logger.error("The class ${classDeclaration.getSimpleName()} is not a data class", classDeclaration)
                return
            }

            val annotation = classDeclaration.findPropertyByName(Map::class.simpleName) ?: return
            val targetClass = annotation.findArgumentByName("targetClass")?.value ?: return

            val target = (targetClass as? KSType) ?: return
            val targetClassDeclaration = (target.declaration as? KSClassDeclaration) ?: return

            if(!targetClassDeclaration.isDataClass()) {
                logger.error("The class ${targetClassDeclaration.getSimpleName()} is not a data class", targetClassDeclaration)
                return
            }

            generateMapperFile(
                packageName = classDeclaration.getPackageName(),
                className = classDeclaration.getSimpleName(),
                classDeclaration = classDeclaration,
                target = target,
                targetClassDeclaration = targetClassDeclaration
            )
        }
    }


    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Map::class.qualifiedName!!)

        symbols.filterIsInstance<KSClassDeclaration>()
            .distinct()
            .filter { symbol -> symbol.validate() }
            .forEach { symbol -> symbol.accept(MapperVisitor(), Unit) }

        return symbols.filterNot { symbol ->
            symbol.validate()
        }.toList()
    }


    private fun generateMapperFile(
        packageName: String,
        className: String,
        classDeclaration: KSClassDeclaration,
        target: KSType,
        targetClassDeclaration: KSClassDeclaration
    ) {
        val targetClassName = target.getSimpleName()
        val targetClassPackage = target.getPackageName()

        val fileName = "${className}To${targetClassName}Mapper"
        val propertiesMapping = classDeclaration.getAllProperties()
            .filter { property ->
                (property.findPropertyByName(IgnoreProperty::class.simpleName) == null)
            }
            .mapNotNull { property ->
                val propertyType = property.getPropertyType()
                val targetProperty = findTargetProperty(property, targetClassDeclaration)
                val targetPropertyType = targetProperty?.getPropertyType()

                when(targetProperty == null) {
                    true -> null
                    false -> generatePropertyMapping(
                        property = property,
                        propertyType = propertyType,
                        isSourceNullable = propertyType.isMarkedNullable,
                        targetProperty = targetProperty,
                        isTargetNullable = targetPropertyType?.isMarkedNullable
                    )
                }
            }.joinToString(",\n")

        val mapperFunction = FunSpec.builder("to${targetClassName}")
            .receiver(ClassName(packageName, className))
            .returns(ClassName(targetClassPackage, targetClassName))
            .addStatement("return %T(\n$propertiesMapping\n)", ClassName(targetClassPackage, targetClassName))
            .build()

        val file = FileSpec.builder(packageName, fileName)
            .addFunction(mapperFunction)
            .build()

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false, classDeclaration.containingFile!!),
            packageName = packageName,
            fileName = fileName
        ).use { output ->
            output.write(file.toString().toByteArray())
        }
    }

    private fun findTargetProperty(
        sourceProperty: KSPropertyDeclaration,
        targetClassDeclaration: KSClassDeclaration
    ): KSPropertyDeclaration? {
        val mapPropertyAnnotation = sourceProperty.findPropertyByName(MapProperty::class.simpleName) ?: return null
        val targetPropertyName = mapPropertyAnnotation.getFirstOrNullArgumentValue().takeIf { argument ->
            (argument?.isNotEmpty() == true)
        } ?: sourceProperty.getSimpleName()

        val targetProperty = targetClassDeclaration.getAllProperties().find { property ->
            (property.getSimpleName() == targetPropertyName)
        }

        if(targetProperty == null) {
            logger.error("Property '${targetPropertyName}' for class '${targetClassDeclaration.getSimpleName()}' was not found")
            return null
        }

        return targetProperty
    }

    private fun generatePropertyMapping(
        property: KSPropertyDeclaration,
        propertyType: KSType,
        isSourceNullable: Boolean,
        targetProperty: KSPropertyDeclaration? = null,
        isTargetNullable: Boolean? = null
    ): String {
        if(targetProperty == null || isTargetNullable == null) {
            return ""
        }

        val mapPropertyAnnotation = property.findPropertyByName(MapProperty::class.simpleName)
        val targetClass = mapPropertyAnnotation?.findArgumentByName("targetClass")?.value as? KSType

        val defaultValue = when(isSourceNullable && !isTargetNullable) {
            true -> " ?: ${propertyType.getDefaultValue()}"
            false -> ""
        }

        val mapCollectionObjectAnnotation = property.findPropertyByName(MapCollectionObject::class.simpleName)
        return when(mapCollectionObjectAnnotation != null && propertyType.isCollectionType()) {
            true -> generateCollectionMapping(
                property = property,
                mapListAnnotation = mapCollectionObjectAnnotation,
                isSourceNullable = isSourceNullable,
                targetProperty = targetProperty,
                isTargetNullable = isTargetNullable
            )
            false -> generateSimplePropertyMapping(
                sourcePropertyName = property.getSimpleName(),
                targetPropertyName = targetProperty.getSimpleName(),
                targetClass = targetClass,
                defaultValue = defaultValue
            )
        }
    }

    private fun generateCollectionMapping(
        property: KSPropertyDeclaration,
        mapListAnnotation: KSAnnotation,
        isSourceNullable: Boolean,
        targetProperty: KSPropertyDeclaration,
        isTargetNullable: Boolean
    ): String {
        val targetType = mapListAnnotation.getFirstArgumentValue().split(".").last()
        val propertyType = property.getPropertyType()
        val defaultValue = propertyType.getDefaultValue()

        val mappedValue = when(!isSourceNullable || isTargetNullable) {
            true -> "this.${property.getSimpleName()}.map { it.to$targetType() }"
            false -> "this.${property.getSimpleName()}?.map { it.to$targetType() } ?: $defaultValue"
        }
        return "\t${targetProperty.getSimpleName()} = $mappedValue"
    }

    private fun generateSimplePropertyMapping(
        sourcePropertyName: String,
        targetPropertyName: String,
        targetClass: KSType?,
        defaultValue: String
    ): String = when(targetClass == null || targetClass.declaration.getSimpleName() == "Any") {
        true -> "\t$targetPropertyName = this.$sourcePropertyName$defaultValue"
        false -> "\t$targetPropertyName = this.$sourcePropertyName.to${targetClass.getSimpleName()}()$defaultValue"
    }
}
