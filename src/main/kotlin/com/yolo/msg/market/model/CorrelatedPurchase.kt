package com.yolo.msg.market.model

import java.time.LocalDate
data class CorrelatedPurchase(
    val customerId: String?,
    val itemsPurchased: List<String>,
    val totalAmount: Double,
    val firstPurchaseTime: LocalDate,
    val secondPurchaseTime: LocalDate
)
