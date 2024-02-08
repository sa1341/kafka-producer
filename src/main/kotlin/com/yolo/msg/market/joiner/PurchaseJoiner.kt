package com.yolo.msg.market.joiner

import com.yolo.msg.market.model.CorrelatedPurchase
import com.yolo.msg.market.model.Purchase
import org.apache.kafka.streams.kstream.ValueJoiner
import java.time.LocalDate

class PurchaseJoiner : ValueJoiner<Purchase, Purchase, CorrelatedPurchase> {

    override fun apply(purchase: Purchase?, otherPurchase: Purchase?): CorrelatedPurchase {
        val purchaseDate = purchase?.purchaseDate ?: LocalDate.now()
        val price = purchase?.price ?: 0
        val itemPurchased = purchase?.itemPurchased ?: ""

        val otherPurchaseDate = otherPurchase?.purchaseDate ?: LocalDate.now()
        val otherPrice = otherPurchase?.price ?: 0
        val otherItemPurchased = otherPurchase?.itemPurchased ?: ""

        val purchasedItems = buildList<String> {
            add(itemPurchased)
            add(otherItemPurchased)
        }.toList()

        val customerId = purchase?.customerId
        val otherCustomerId = otherPurchase?.customerId

        return CorrelatedPurchase(
            customerId = customerId ?: otherCustomerId,
            itemsPurchased = purchasedItems,
            totalAmount = price.toDouble().plus(otherPrice.toDouble()),
            firstPurchaseTime = purchaseDate,
            secondPurchaseTime = otherPurchaseDate,
        )
    }
}
