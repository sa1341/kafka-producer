package com.yolo.msg.market.model

data class RewardAccumulator(
    val customerId: String,
    val purchaseTotal: Double,
    var totalRewardPoints: Int,
    val currentRewardPoints: Int,
    val daysFromLastPurchase: Int = 0,
) {

    fun addRewardPoints(previousTotalPoints: Int) {
        this.totalRewardPoints += previousTotalPoints
    }

    companion object {
        fun from(purchase: Purchase): RewardAccumulator {
            val rewardPoints = purchase.price.times(purchase.quantity).toInt()
            return RewardAccumulator(
                customerId = "${purchase.lastName},${purchase.firstName}",
                purchaseTotal = purchase.price.times(purchase.quantity),
                currentRewardPoints = rewardPoints,
                totalRewardPoints = rewardPoints,
            )
        }
    }
}
