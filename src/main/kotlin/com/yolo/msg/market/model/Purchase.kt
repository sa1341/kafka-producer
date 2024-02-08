package com.yolo.msg.market.model

import java.time.LocalDate

const val CC_NUMBER_REPLACEMENT = "xxxx-xxxx-xxxx-"
const val CC_DEFAULT_NUMBER_REPLACEMENT = "xxxx"

data class PurchasePattern(
    val zipCode: String,
    val item: String,
    val date: LocalDate,
    val amount: Double,
) {
    companion object {
        fun from(purchase: Purchase): PurchasePattern {
            return PurchasePattern(
                zipCode = purchase.zipCode,
                item = purchase.itemPurchased,
                date = purchase.purchaseDate,
                amount = purchase.price.times(purchase.quantity),
            )
        }
    }
}

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
    val storeId: String,
) {

    companion object {
        private fun maskCreditCard(creditCardNumber: String): String {
            val parts = creditCardNumber.split("-")
            return if (parts.size < 4) {
                CC_DEFAULT_NUMBER_REPLACEMENT
            } else {
                val last4Digits = creditCardNumber.split("-")[3]
                "$CC_NUMBER_REPLACEMENT$last4Digits"
            }
        }

        fun from(
            purchase: Purchase,
        ): Purchase {
            return Purchase(
                firstName = purchase.firstName,
                lastName = purchase.lastName,
                customerId = purchase.customerId,
                creditCardNumber = maskCreditCard(purchase.creditCardNumber),
                itemPurchased = purchase.itemPurchased,
                department = purchase.department,
                employeeId = purchase.employeeId,
                quantity = purchase.quantity,
                price = purchase.price,
                purchaseDate = purchase.purchaseDate,
                zipCode = purchase.zipCode,
                storeId = purchase.storeId,
            )
        }
    }
}
