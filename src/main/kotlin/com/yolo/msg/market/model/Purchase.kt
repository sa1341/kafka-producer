package com.yolo.msg.market.model

import java.time.LocalDate

data class Purchase(
    val firstName: String,
    val lastName: String,
    val customerId: String,
    val creditCardNumber: String,
    val itemPurchased: String,
    val department: String,
    val employeeId: String,
    val quantity: Int,
    val price: Double,
    val purchaseDate: LocalDate,
    val zipCode: String,
    val storeId: String
)
