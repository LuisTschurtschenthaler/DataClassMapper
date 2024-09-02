package com.layer8studios.dataclassmapper

import com.layer8studios.data_class_mapper.annotations.IgnoreProperty
import com.layer8studios.data_class_mapper.annotations.Map
import com.layer8studios.data_class_mapper.annotations.MapCollectionObject
import com.layer8studios.data_class_mapper.annotations.MapProperty

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

    val userDomain = userApiResponse.toUserDomain()
    println(userDomain)
}
