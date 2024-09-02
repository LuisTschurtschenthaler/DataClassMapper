
# Kotlin Data Class Mapper Library

[![Maven Central](https://img.shields.io/maven-central/v/io.github.luistschurtschenthaler/data-class-mapper.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:io.github.luistschurtschenthaler%20AND%20a:data-class-mapper)

An annotation-based Kotlin library using Kotlin Symbol Processing (KSP) for automatic generation of mapping functions between data classes, simplifying the transformation of API response models into domain models.

## Features

- **Annotation-Driven Mapping:** Use simple annotations to define mapping rules between data classes.
- **Supports Nested and Collection Mappings:** Handles complex nested data structures and collections with ease.
- **Customizable Property Names and Types:** Allows properties with different names or types to be mapped easily.
- **Ignore Fields:** Skip unwanted fields from being mapped using `@IgnoreProperty`.
- **Kotlin Symbol Processing (KSP) Support:** Leverages KSP for efficient annotation processing and code generation.
- **Type Safety and Null Safety:** Ensures type safety and handles nullable properties in mappings.
- **Automatic Code Generation:** Automatically generates mapping functions based on annotations, reducing boilerplate code.

## Installation

Add the following dependencies to your `build.gradle.kts` file to include the library and KSP processor:

```gradle
plugins {
    id("com.google.devtools.ksp") version "2.0.20-1.0.24"
}

dependencies {
    implementation("io.github.luistschurtschenthaler:data-class-mapper:<latest_version>")
    ksp("io.github.luistschurtschenthaler:data-class-mapper-ksp:<latest_version>")
}
```

Replace `latest_version` with the latest version available on [Maven Central](https://search.maven.org/search?q=g:io.github.luistschurtschenthaler%20AND%20a:data-class-mapper).

## Usage

Annotate your API response and domain data classes using `@Map`, `@MapProperty`, `@IgnoreProperty`, and `@MapCollectionObject` to automate the conversion between them.

### Example

Let's say you have an API response that provides user information, including their address and orders. The goal is to map this API response to a domain model using annotations.

#### Step 1: Define Data Classes

```kotlin
@Map(targetClass = UserDomain::class)
data class UserApiResponse(
    @IgnoreProperty
    val userId: String,

    @MapProperty(target = "fullName")
    val name: String,

    @MapProperty(targetClass = AddressDomain::class)
    val address: AddressResponse,

    @MapProperty
    @MapCollectionObject(target = OrderDomain::class)
    val orders: List<OrderResponse>
)

@Map(targetClass = AddressDomain::class)
data class AddressResponse(
    @MapProperty
    val street: String,

    @MapProperty
    val city: String,

    @MapProperty
    val zipCode: String
)

@Map(targetClass = OrderDomain::class)
data class OrderResponse(
    @MapProperty
    val orderId: String,

    @MapProperty
    val product: String,

    @MapProperty
    val quantity: Int
)
```

#### Step 2: Define Domain Classes

```kotlin
data class UserDomain(
    val fullName: String,
    val address: AddressDomain,
    val orders: List<OrderDomain>
)

data class AddressDomain(
    val street: String,
    val city: String,
    val zipCode: String
)

data class OrderDomain(
    val orderId: String,
    val product: String,
    val quantity: Int
)
```

#### Step 3: Generate Mapping Functions Automatically

After defining your classes and annotations, KSP will generate mapping functions for you during the build process. The generated function `toUserDomain()` can be used directly:

#### Step 4: Use the Generated Mapper Functions

```kotlin
fun main() {
    val userApiResponse = UserApiResponse(
        userId = "U123",
        name = "John Doe",
        address = AddressResponse(
            street = "123 Main St",
            city = "Anytown",
            zipCode = "12345"
        ),
        orders = listOf(
            OrderResponse(orderId = "O1", product = "Widget", quantity = 3),
            OrderResponse(orderId = "O2", product = "Gadget", quantity = 5)
        )
    )

    // Using the generated mapper function
    val userDomain: UserDomain = userApiResponse.toUserDomain() // Generated function
    println(userDomain)
}
```

### Annotations Overview

- `@Map(targetClass = ...)`: Defines the target class to which the data class will be mapped.
- `@MapProperty(target = "...")`: Specifies a custom target property name or type for mapping.
- `@IgnoreProperty`: Ignores the property during the mapping process.
- `@MapCollectionObject(target = ...)`: Maps a collection of objects to a specified target class.

## Contributing

Feel free to fork the project, submit issues, or open pull requests. Contributions are welcome!

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Conclusion

The Kotlin Data Class Mapper library significantly reduces boilerplate code, ensures type safety, and simplifies data class conversions in your Kotlin projects.
