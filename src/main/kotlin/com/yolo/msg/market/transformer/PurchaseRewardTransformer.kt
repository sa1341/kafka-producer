package com.yolo.msg.market.transformer

import com.yolo.msg.market.model.Purchase
import com.yolo.msg.market.model.RewardAccumulator
import org.apache.kafka.streams.kstream.ValueTransformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

class PurchaseRewardTransformer(
    val storeName: String
) : ValueTransformer<Purchase, RewardAccumulator> {

    private var stateStore: KeyValueStore<String, Int>? = null
    private var context: ProcessorContext? = null

    override fun init(context: ProcessorContext?) {
        this.context = context
        this.stateStore = this.context?.getStateStore(storeName)
    }

    override fun transform(value: Purchase): RewardAccumulator {
        val rewardAccumulator = RewardAccumulator.from(value)
        val accumulatedSoFar = stateStore?.get(rewardAccumulator.customerId)

        accumulatedSoFar?.let {
            rewardAccumulator.addRewardPoints(accumulatedSoFar)
        }
        stateStore?.put(rewardAccumulator.customerId, rewardAccumulator.totalRewardPoints)

        return rewardAccumulator
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
